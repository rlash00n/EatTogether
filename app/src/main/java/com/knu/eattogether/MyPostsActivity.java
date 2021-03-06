package com.knu.eattogether;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class MyPostsActivity extends AppCompatActivity {

    TabLayout tabLayout;
    MyDeliveryBoardFragment myDeliveryFragment;
    MyEatOutBoardFragment myEatOutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        myDeliveryFragment = new MyDeliveryBoardFragment();
        myEatOutFragment = new MyEatOutBoardFragment();

        getSupportFragmentManager().beginTransaction().
                replace(R.id.myposts_framelayout, myDeliveryFragment)
                .commitAllowingStateLoss();

        tabLayout = findViewById(R.id.myposts_tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("배달 투게더"));
        tabLayout.addTab(tabLayout.newTab().setText("외식 투게더"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position==0){
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.myposts_framelayout, myDeliveryFragment)
                            .commitAllowingStateLoss();
                }
                else{
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.myposts_framelayout, myEatOutFragment)
                            .commitAllowingStateLoss();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}