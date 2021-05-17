package com.knu.eattogether;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import java.util.List;

public class OnlyImageActivity extends AppCompatActivity {

    ViewPager viewPager;
    OnlyImageViewPagerAdapter viewPagerAdapter;
    private List<String> mDataList;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_image);

        Intent intent = getIntent();
        mDataList = intent.getStringArrayListExtra("list");
        position = intent.getExtras().getInt("position");

        viewPager = findViewById(R.id.onlyimage_vp);
        viewPagerAdapter = new OnlyImageViewPagerAdapter(OnlyImageActivity.this, mDataList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(position);

    }
}