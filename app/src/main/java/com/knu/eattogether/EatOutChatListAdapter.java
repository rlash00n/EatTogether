package com.knu.eattogether;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EatOutChatListAdapter extends RecyclerView.Adapter<EatOutChatListAdapter.ViewHolder> {

    FirebaseAuth auth;
    String myid;

    private Context context = null;
    private List<ChatListItem> mDataList;
    private ArrayList<ChattingItem2> list = new ArrayList<>();

    public EatOutChatListAdapter(Context context, List<ChatListItem> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.chat_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull EatOutChatListAdapter.ViewHolder holder, int position) {
        auth = FirebaseAuth.getInstance();
        myid = auth.getUid();

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("EatOutPost").child(mDataList.get(position).getPostid());
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    PostItem item = snapshot.getValue(PostItem.class);
                    holder.title.setText(item.getTitle());
                    holder.chat_people.setText(item.getCur_people());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("EatOutChat").child(mDataList.get(position).getPostid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    list.clear();
                    int cnt = 0;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        ChattingItem2 item2 = snapshot1.getValue(ChattingItem2.class);
                        if (item2.getContents() == null) continue;
                        list.add(item2);

                        ArrayList<String> rec = item2.getReceiversid();
                        Map<String, Object> seen = new HashMap<>();
                        seen = item2.getSeenUsers();
                        if (rec.contains(myid) && !seen.containsKey(myid)) cnt++;
                    }
                    if (list.size() > 0) {
                        Collections.sort(list, new Ascending());
                        holder.contents.setText(list.get(0).getContents());

                        Long t = Long.parseLong(list.get(0).getTime());
                        holder.time.setText(formatTimeString(t));
                    }
                    if (cnt == 0) {
                        holder.chat_new.setVisibility(View.GONE);
                    } else if (cnt <= 99) {
                        holder.chat_new.setVisibility(View.VISIBLE);
                        holder.chat_new.setText(cnt + "");
                    } else {
                        holder.chat_new.setVisibility(View.VISIBLE);
                        holder.chat_new.setText("99+");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView chat_people;
        TextView time;
        TextView contents;
        TextView chat_new;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.chat_title);
            chat_people = itemView.findViewById(R.id.chat_people);
            time = itemView.findViewById(R.id.chat_time);
            contents = itemView.findViewById(R.id.chat_contents);
            chat_new = itemView.findViewById(R.id.chat_new);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Intent intent = new Intent(v.getContext(), EatOutChattingActivity.class);
                    intent.putExtra("postid", mDataList.get(pos).getPostid());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    class Ascending implements Comparator<ChattingItem2> {

        @Override
        public int compare(ChattingItem2 o1, ChattingItem2 o2) {
            return o2.getTime().compareTo(o1.getTime());
        }
    }

    public String formatTimeString(long regTime) {
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;
        String msg = "";
        if (diffTime < DeliveryBoardAdapter.TIME_MAXIMUM.SEC) {
            msg = "방금 전";
        } else if ((diffTime /= DeliveryBoardAdapter.TIME_MAXIMUM.SEC) < DeliveryBoardAdapter.TIME_MAXIMUM.MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= DeliveryBoardAdapter.TIME_MAXIMUM.MIN) < DeliveryBoardAdapter.TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= DeliveryBoardAdapter.TIME_MAXIMUM.HOUR) < DeliveryBoardAdapter.TIME_MAXIMUM.DAY) {
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= DeliveryBoardAdapter.TIME_MAXIMUM.DAY) < DeliveryBoardAdapter.TIME_MAXIMUM.MONTH) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }
}