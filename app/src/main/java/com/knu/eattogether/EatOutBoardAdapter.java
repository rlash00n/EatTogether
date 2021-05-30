package com.knu.eattogether;


import android.content.Context;
import android.content.Intent;
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

public class EatOutBoardAdapter extends RecyclerView.Adapter<EatOutBoardAdapter.ViewHolder> {

    private Context context = null;
    private List<PostItem> mDataList;
    private ArrayList<UsersItem> ulist = new ArrayList<>();

    public EatOutBoardAdapter(Context context, List<PostItem> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public EatOutBoardAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.board_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EatOutBoardAdapter.ViewHolder holder, int position) {
        // 각 아이템들에 대한 매칭
        PostItem item1 = mDataList.get(position);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(item1.getUserid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                UsersItem item2 = snapshot.getValue(UsersItem.class);

                Glide.with(context).load(item2.getProfileuri()).into(holder.writer_image);
                holder.writer_nick.setText(item2.getNickname());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        holder.title.setText(item1.getTitle());
        holder.content.setText(item1.getContents());

        Long t = Long.parseLong(item1.getWritetime());
        holder.time.setText(formatTimeString(t));

        holder.pic_cnt.setText(item1.getImageurilist().size()+"");
        holder.cur_ppl.setText(item1.getCur_people());
        holder.max_ppl.setText(item1.getMax_people());

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView writer_image;
        TextView writer_nick;
        TextView title;
        TextView content;
        TextView time;
        TextView pic_cnt;
        TextView cur_ppl;
        TextView max_ppl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            writer_image = itemView.findViewById(R.id.board_writer_image);
            writer_nick = itemView.findViewById(R.id.board_writer_nick);
            title = itemView.findViewById(R.id.board_title);
            content = itemView.findViewById(R.id.board_content);
            time = itemView.findViewById(R.id.board_time);
            pic_cnt = itemView.findViewById(R.id.board_picture_cnt);
            cur_ppl = itemView.findViewById(R.id.board_cur_people);
            max_ppl = itemView.findViewById(R.id.board_max_people);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Intent intent = new Intent(v.getContext(), EatOutBoardDetailActivity.class);
                    intent.putExtra("pid", mDataList.get(pos).getPostid());
                    intent.putExtra("uid", mDataList.get(pos).getUserid());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    public static class TIME_MAXIMUM{
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    public String formatTimeString(long regTime) {
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;
        String msg = "";
        if (diffTime < TIME_MAXIMUM.SEC) {
            msg = "방금 전";
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }
        return msg;
    }
}
