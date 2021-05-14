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

import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.ViewHolder> {

    private Context context = null;
    private List<PostItem> mDataList;

    public BoardAdapter(Context context, List<PostItem> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.board_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardAdapter.ViewHolder holder, int position) {
        // 각 아이템들에 대한 매칭
        PostItem item1 = mDataList.get(position);

        Glide.with(holder.itemView).load(mDataList.get(position).getProfile()).into(holder.writer_image);
        holder.writer_nick.setText(item1.getNick());
        holder.title.setText(item1.getTitle());
        holder.content.setText(item1.getContents());

        Long t = Long.parseLong(item1.getWritetime());
        holder.time.setText(formatTimeString(t));

        holder.pic_cnt.setText(item1.getPicturecnt());
        holder.cur_ppl.setText(item1.getCur_people());
        holder.max_ppl.setText(item1.getMax_people());

    }

    @Override
    public int getItemCount() {
        return (mDataList != null ? mDataList.size() : 0);
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
