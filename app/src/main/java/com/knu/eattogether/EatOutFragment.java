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

public class EatOutFragment extends Fragment {

    private RecyclerView recyclerView;
    private EatOutBoardAdapter boardAdapter;
    private ArrayList<PostItem> plist = new ArrayList<>();
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_eat_out, container, false);

        FloatingActionButton fab = view.findViewById(R.id.eatoutboard_floatingactionbutton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), WriteEatOutBoardActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = view.findViewById(R.id.eatoutboard_recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.eatout_swiperefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reference = FirebaseDatabase.getInstance().getReference("EatOutPost");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        //?????????????????? ????????????????????? ???????????? ???????????? ???
                        plist.clear(); // ?????? ?????????????????? ???????????? ?????? ?????????
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            PostItem item1 = snapshot1.getValue(PostItem.class); // ????????? ?????? PostItem ????????? ???????????? ?????????
                            plist.add(item1); //?????? ??????????????? ?????????????????? ?????? ????????????????????? ?????? ??????

                            Collections.sort(plist, new EatOutFragment.Ascending());
                            boardAdapter = new EatOutBoardAdapter(view.getContext(), plist);
                            recyclerView.setAdapter(boardAdapter);
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("EatOutPost");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //?????????????????? ????????????????????? ???????????? ???????????? ???
                plist.clear(); // ?????? ?????????????????? ???????????? ?????? ?????????
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    PostItem item1 = snapshot1.getValue(PostItem.class); // ????????? ?????? PostItem ????????? ???????????? ?????????
                    plist.add(item1); //?????? ??????????????? ?????????????????? ?????? ????????????????????? ?????? ??????

                    Collections.sort(plist, new EatOutFragment.Ascending());
                    boardAdapter = new EatOutBoardAdapter(view.getContext(), plist);
                    recyclerView.setAdapter(boardAdapter);
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