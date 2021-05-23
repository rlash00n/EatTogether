package com.knu.eattogether;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChattingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context = null;
    private List<ChattingItem> mDataList;

    public ChattingAdapter(Context context, List<ChattingItem> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType == Code.ViewType.FIRST){
            view = inflater.inflate(R.layout.chatting_item_others, parent, false);
            //return 각각의 뷰홀더
            return new FirstViewHolder(view);
        }
        else if(viewType == Code.ViewType.SECOND) {
            view = inflater.inflate(R.layout.chatting_item_me, parent, false);
            //return 각각의 뷰홀더
            return  new SecondViewHolder(view);
        }
        else if(viewType == Code.ViewType.THIRD){
            view = inflater.inflate(R.layout.chatting_item_new_chatter, parent, false);
            return new ThirdViewHolder(view);
        }
        else{
            view = inflater.inflate(R.layout.chatting_item_out_chatter, parent, false);
            return new FourthViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof FirstViewHolder){
            ((FirstViewHolder) holder).contents.setText(mDataList.get(position).getContents());

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(mDataList.get(position).getSenderid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UsersItem item = snapshot.getValue(UsersItem.class);
                    Glide.with(context).load(item.getProfileuri()).into(((FirstViewHolder) holder).profile);
                    ((FirstViewHolder) holder).nickname.setText(item.getNickname());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            ((FirstViewHolder) holder).contents.setText(mDataList.get(position).getContents());
            int cnt=0;
            for(ArrayList<String> s : mDataList.get(position).getSeen()){
                if(s.get(1).equals("n")){
                    cnt++;
                }
            }
            if(cnt==0){
                ((FirstViewHolder) holder).seen.setVisibility(View.GONE);
            }
            else{
                ((FirstViewHolder) holder).seen.setText(cnt+"");
            }

            Long t = Long.parseLong(mDataList.get(position).getTime());
            ((FirstViewHolder) holder).time.setText(formatTimeString(t));

        }
        else if(holder instanceof SecondViewHolder){
            ((SecondViewHolder) holder).contents.setText(mDataList.get(position).getContents());
            int cnt = 0;
            for(ArrayList<String> s : mDataList.get(position).getSeen()){
                if(s.get(1).equals("n")){
                    cnt++;
                }
            }
            if(cnt==0){
                ((SecondViewHolder) holder).seen.setVisibility(View.GONE);
            }
            else{
                ((SecondViewHolder) holder).seen.setText(cnt+"");
            }

            Long t = Long.parseLong(mDataList.get(position).getTime());
            ((SecondViewHolder) holder).time.setText(formatTimeString(t));

        }
        else if(holder instanceof ThirdViewHolder){
            //new chatter 왔을 때...
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(mDataList.get(position).getNewid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    UsersItem item = snapshot.getValue(UsersItem.class);
                    ((ThirdViewHolder) holder).new_chatter.setText(item.getNickname()+"님이 참여하셨습니다.");
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
        else{
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(mDataList.get(position).getNewid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    UsersItem item = snapshot.getValue(UsersItem.class);
                    ((FourthViewHolder) holder).out_chatter.setText(item.getNickname()+"님이 나가셨습니다.");
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).getViewType();
    }

    public class FirstViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profile;
        TextView nickname;
        TextView contents;
        TextView seen;
        TextView time;
        public FirstViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.chatting_profile_others);
            nickname = itemView.findViewById(R.id.chatting_nickname_others);
            contents = itemView.findViewById(R.id.chatting_content_others);
            seen = itemView.findViewById(R.id.chatting_seen_others);
            time = itemView.findViewById(R.id.chatting_time_others);
        }
    }


    public class SecondViewHolder extends RecyclerView.ViewHolder{

        TextView contents;
        TextView seen;
        TextView time;
        public SecondViewHolder(@NonNull View itemView) {
            super(itemView);
            contents = itemView.findViewById(R.id.chatting_content_me);
            seen = itemView.findViewById(R.id.chatting_seen_me);
            time = itemView.findViewById(R.id.chatting_time_me);
        }
    }

    public class ThirdViewHolder extends RecyclerView.ViewHolder{

        TextView new_chatter;
        public ThirdViewHolder(@NonNull View itemView) {
            super(itemView);
            new_chatter = itemView.findViewById(R.id.chatting_new_chatter);
        }
    }

    public class FourthViewHolder extends RecyclerView.ViewHolder{

        TextView out_chatter;
        public FourthViewHolder(@NonNull View itemView) {
            super(itemView);
            out_chatter = itemView.findViewById(R.id.chatting_out_chatter);
        }
    }


    public String formatTimeString(long regTime) {
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;
        String msg = "";
        if (diffTime < BoardAdapter.TIME_MAXIMUM.SEC) {
            msg = "방금 전";
        } else if ((diffTime /= BoardAdapter.TIME_MAXIMUM.SEC) < BoardAdapter.TIME_MAXIMUM.MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= BoardAdapter.TIME_MAXIMUM.MIN) < BoardAdapter.TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= BoardAdapter.TIME_MAXIMUM.HOUR) < BoardAdapter.TIME_MAXIMUM.DAY) {
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= BoardAdapter.TIME_MAXIMUM.DAY) < BoardAdapter.TIME_MAXIMUM.MONTH) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }
}
