package com.knu.eattogether;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class MyChatListActivity extends AppCompatActivity {

    TabLayout tabLayout;
    MyDeliveryChatListFragment myDeliveryChatListFragment;
    MyEatOutChatListFragment myEatOutChatListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chat_list);

        myDeliveryChatListFragment = new MyDeliveryChatListFragment();
        myEatOutChatListFragment = new MyEatOutChatListFragment();

        getSupportFragmentManager().beginTransaction().
                replace(R.id.mychats_framelayout, myDeliveryChatListFragment)
                .commitAllowingStateLoss();

        tabLayout = findViewById(R.id.mychats_tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("배달 투게더"));
        tabLayout.addTab(tabLayout.newTab().setText("외식 투게더"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position==0){
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.mychats_framelayout, myDeliveryChatListFragment)
                            .commitAllowingStateLoss();
                }
                else{
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.mychats_framelayout, myEatOutChatListFragment)
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