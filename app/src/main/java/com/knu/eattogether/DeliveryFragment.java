package com.knu.eattogether;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DeliveryFragment extends Fragment {

    private RecyclerView deliveryRecyclerView;
    private BoardAdapter boardAdapter;
    private ArrayList<PostItem> list = new ArrayList<>();
    private DatabaseReference databaseReference;

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
        deliveryRecyclerView.setHasFixedSize(true); // 리사이클러뷰 기존 성능 강화

        LinearLayoutManager manager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        deliveryRecyclerView.setLayoutManager(manager);

        databaseReference = FirebaseDatabase.getInstance().getReference("DeliveryPost");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                list.clear(); // 기존 배열리스트가 존재하지 않게 초기화
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    PostItem item1 = snapshot1.getValue(PostItem.class); // 만들어 뒀던 PostItem 객체에 데이터를 담는다
                    list.add(item1); //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                }
                boardAdapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        boardAdapter = new BoardAdapter(view.getContext(), list);
        deliveryRecyclerView.setAdapter(boardAdapter);

        return view;
    }

}