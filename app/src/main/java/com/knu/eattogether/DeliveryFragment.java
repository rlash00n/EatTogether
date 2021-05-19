package com.knu.eattogether;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DeliveryFragment extends Fragment {

    private RecyclerView deliveryRecyclerView;
    private BoardAdapter boardAdapter;
    private ArrayList<PostItem> plist = new ArrayList<>();
    DatabaseReference reference;
    DatabaseReference reference2;
    DatabaseReference reference3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_delivery, container, false);

        FloatingActionButton fab = view.findViewById(R.id.board_floatingactionbutton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), WriteDeliveryBoardActivity.class);
                startActivity(intent);
            }
        });

        deliveryRecyclerView = view.findViewById(R.id.deliveryboard_recyclerview); // 아이디 연결
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        deliveryRecyclerView.setLayoutManager(manager);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.delivery_swiperefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reference = FirebaseDatabase.getInstance().getReference("DeliveryPost");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                        plist.clear(); // 기존 배열리스트가 존재하지 않게 초기화
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            PostItem item1 = snapshot1.getValue(PostItem.class); // 만들어 뒀던 PostItem 객체에 데이터를 담는다
                            plist.add(item1); //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비

                            Collections.sort(plist, new Ascending());
                            boardAdapter = new BoardAdapter(view.getContext(), plist);
                            deliveryRecyclerView.setAdapter(boardAdapter);
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("DeliveryPost");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                plist.clear(); // 기존 배열리스트가 존재하지 않게 초기화
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    PostItem item1 = snapshot1.getValue(PostItem.class); // 만들어 뒀던 PostItem 객체에 데이터를 담는다
                    plist.add(item1); //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비

                    Collections.sort(plist, new Ascending());
                    boardAdapter = new BoardAdapter(view.getContext(), plist);
                    deliveryRecyclerView.setAdapter(boardAdapter);
                }

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        return view;
    }

    class Ascending implements Comparator<PostItem> {

        @Override
        public int compare(PostItem o1, PostItem o2) {
            return o2.getWritetime().compareTo(o1.getWritetime());
        }

    }

}