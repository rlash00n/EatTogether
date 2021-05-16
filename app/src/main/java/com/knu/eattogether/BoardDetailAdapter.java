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

public class BoardDetailAdapter extends RecyclerView.Adapter<BoardDetailAdapter.ViewHolder> {

    private Context context = null;
    private List<BoardDetailItem> mDataList;

    public BoardDetailAdapter() {
    }

    public BoardDetailAdapter(Context context, List<BoardDetailItem> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public interface OnImageClickListener {
        void onImageClick(View v, int position);
    }
    private OnImageClickListener mListener = null;
    public void setOnImageClickListener(OnImageClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_recyclerview_item, parent, false);
        return new ViewHolder(view);    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull @NotNull BoardDetailAdapter.ViewHolder holder, int position) {
        GradientDrawable drawable = (GradientDrawable) context.getDrawable(R.drawable.background_rounding);
        holder.imageView.setBackground(drawable);
        holder.imageView.setClipToOutline(true);
        Glide.with(context).load(mDataList.get(0).getImageurilist().get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mDataList.get(0).getImageurilist().size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.detail_image);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(mListener != null){
                            mListener.onImageClick(v, pos);
                        }
                    }
                }
            });
        }
    }
}
