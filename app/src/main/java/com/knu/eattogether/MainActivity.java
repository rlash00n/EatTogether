package com.knu.eattogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    DeliveryFragment deliveryFragment;
    EatOutFragment eatOutFragment;
    MyPageFragment chatFragment;

    TextView main_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        main_tv = findViewById(R.id.main_text);

        deliveryFragment = new DeliveryFragment();
        eatOutFragment = new EatOutFragment();
        chatFragment = new MyPageFragment();

        getSupportFragmentManager().beginTransaction().
                replace(R.id.main_frame_layout, deliveryFragment)
                .commitAllowingStateLoss();

        bottomNavigationView.setSelectedItemId(R.id.tab1);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.tab1:
                        main_tv.setText("배달 투게더");
                        getSupportFragmentManager().beginTransaction().
                                replace(R.id.main_frame_layout, deliveryFragment)
                                .commitAllowingStateLoss();
                        return true;

                    case R.id.tab2:
                        main_tv.setText("외식 투게더");
                        getSupportFragmentManager().beginTransaction().
                                replace(R.id.main_frame_layout, eatOutFragment)
                                .commitAllowingStateLoss();
                        return true;

                    case R.id.tab3:
                        main_tv.setText("마이 페이지");
                        getSupportFragmentManager().beginTransaction().
                                replace(R.id.main_frame_layout, chatFragment)
                                .commitAllowingStateLoss();
                        return true;
                }
                return false;
            };
        });
    }


}