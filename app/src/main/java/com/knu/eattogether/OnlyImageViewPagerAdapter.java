package com.knu.eattogether;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OnlyImageViewPagerAdapter extends PagerAdapter {

    private Context context;
    private List<String> mDataList;

    public OnlyImageViewPagerAdapter() {
    }

    public OnlyImageViewPagerAdapter(Context context, List<String> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = null;
        if(context != null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.only_image_viewpager_item, container, false);

            ImageView imageView = (ImageView)view.findViewById(R.id.onlyimage_vp_item);
            Glide.with(view).load(mDataList.get(position)).into(imageView);
        }
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (View)object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }
}
