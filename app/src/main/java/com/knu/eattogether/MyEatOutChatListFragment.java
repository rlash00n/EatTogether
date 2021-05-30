package com.knu.eattogether;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyEatOutChatListFragment extends Fragment {

    DatabaseReference reference;
    FirebaseAuth auth;
    String userid;

    private RecyclerView recyclerView;
    private EatOutChatListAdapter adapter;
    private ArrayList<ChatListItem> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my_eat_out_chat_list, container, false);

        auth = FirebaseAuth.getInstance();

        userid = auth.getCurrentUser().getUid();
        recyclerView = view.findViewById(R.id.eatout_chat_rcv);
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        reference = FirebaseDatabase.getInstance().getReference("EatOutChatList");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    ChatListItem item = snapshot1.getValue(ChatListItem.class);
                    for(String s : item.getChatters_list()){
                        if(s.equals(userid))
                            list.add(item);
                    }
                }

                Collections.sort(list, new MyEatOutChatListFragment.Ascending());
                adapter = new EatOutChatListAdapter(view.getContext(), list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    class Ascending implements Comparator<ChatListItem> {

        @Override
        public int compare(ChatListItem o1, ChatListItem o2) {
            return o2.getTime().compareTo(o1.getTime());
        }

    }
}