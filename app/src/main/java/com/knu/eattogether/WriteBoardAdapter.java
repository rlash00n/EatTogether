package com.knu.eattogether;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WriteBoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private List<WriteBoardItem> mDataList;

    public WriteBoardAdapter(Context context, List<WriteBoardItem> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public interface OnOtherClickListener{
        void onOtherClick(View v, int position);
    }
    private OnOtherClickListener mOtherListener = null;
    public void setOnOtherClickListener(OnOtherClickListener listener){
        this.mOtherListener = listener;
    }

    public interface OnPlusClickListener{
        void onPlusClick(View v, int position);
    }
    private OnPlusClickListener mPlusListener = null;
    public void setOnPlusClickListener(OnPlusClickListener listener){
        this.mPlusListener = listener;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType == Code.ViewType.FIRST){
            view = inflater.inflate(R.layout.write_board_item1, parent, false);
            //return 각각의 뷰홀더
            return new WriteBoardAdapter.FirstViewHolder(view);
        }
        else {
            view = inflater.inflate(R.layout.write_board_item2, parent, false);
            //return 각각의 뷰홀더
            return  new WriteBoardAdapter.SecondViewHolder(view);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof FirstViewHolder){  // 선택한 이미지들
            GradientDrawable drawable = (GradientDrawable) context.getDrawable(R.drawable.background_rounding);
            ((FirstViewHolder)holder).imageView.setBackground(drawable);
            ((FirstViewHolder)holder).imageView.setClipToOutline(true);
            Glide.with(context).load(mDataList.get(position).getImageuri()).into(((FirstViewHolder)holder).imageView);
        }
        else if(holder instanceof SecondViewHolder){}
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).getViewtype();
    }

    private class FirstViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public FirstViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.writeboard_item_imageview1);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(mOtherListener != null){
                            mOtherListener.onOtherClick(v, pos);
                        }
                    }
                }
            });
        }
    }

    private class SecondViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public SecondViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.writeboard_item_imageview2);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(mPlusListener != null){
                            mPlusListener.onPlusClick(v,pos);
                        }
                    }
                }
            });
        }
    }
}
