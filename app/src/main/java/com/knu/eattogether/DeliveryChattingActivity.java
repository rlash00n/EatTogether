package com.knu.eattogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class DeliveryChattingActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ChattingAdapter adapter;
    EditText input;
    Button send;

    FirebaseAuth auth;
    String userid;

    private ArrayList<ChattingItem> list = new ArrayList<>();

    ValueEventListener seenListener;
    DatabaseReference ref;

    ImageView back;
    TextView title;
    Button out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_chatting);

        auth = FirebaseAuth.getInstance();
        userid = auth.getCurrentUser().getUid();

        Intent intent = getIntent();
        final String postid = intent.getStringExtra("postid");

        //로그인이랑 알람 상태 확인하는거------------------

        recyclerView = findViewById(R.id.chatting_rv);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        back = findViewById(R.id.chatting_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        out = findViewById(R.id.chatting_out);
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("채팅방을 나가시겠습니까?");

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        title = findViewById(R.id.chatting_title);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                PostItem item = snapshot.getValue(PostItem.class);
                title.setText(item.getTitle());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        seenMessage(userid, postid);

        input = findViewById(R.id.et_chat);
        send = findViewById(R.id.bt_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ct = input.getText().toString();
                if(ct.equals("") || ct == null){
                    return;
                }
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("DeliveryChatList").child(postid);
                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        ChatListItem item = snapshot.getValue(ChatListItem.class);
                        ArrayList<String> tmp = item.getChatters_list();
                        ArrayList<String> receiver = new ArrayList<>();
                        for(String s : tmp){
                            if(!s.equals(userid)){
                                receiver.add(s);
                            }
                        }
                        //
                        //알림 보내시고... receiver ArrayList 있으니까 저기에 있는 사람들한테 다 알림보내시고
                        //
                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("DeliveryChat").child(postid).push();
                        HashMap result = new HashMap<>();
                        final long t = System.currentTimeMillis();
                        ArrayList<ArrayList<String>> seen = new ArrayList<ArrayList<String>>();
                        for(String s : tmp){
                            ArrayList<String> ttmp = new ArrayList<>();
                            ttmp.add(s);
                            if(s.equals(userid)) ttmp.add("y");
                            else ttmp.add("n");
                            seen.add(ttmp);
                        }
                        result.put("contents", ct);
                        result.put("senderid", userid);
                        result.put("receiverid", receiver);
                        result.put("time", t + "");
                        result.put("seen", seen);
                        result.put("newchatter","n");
                        result.put("newid",null);
                        result.put("outchatter","n");
                        result.put("outid",null);

                        reference2.setValue(result);

                        input.setText("");

                        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("DeliveryChatList").child(postid);
                        reference3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                ChatListItem item1 = snapshot.getValue(ChatListItem.class);
                                item1.setTime(t+"");
                                reference3.setValue(item1);
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            }
        });
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("DeliveryChat").child(postid);
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    ChattingItem item = snapshot1.getValue(ChattingItem.class);
                    if(item.getNewchatter().equals("y")){
                        list.add(new ChattingItem(item.getContents(),item.getSenderid(),item.getReceiversid(),item.getTime(),item.getSeen(),item.getNewchatter(),item.getNewid(),item.getOutchatter(),item.getOutid(),Code.ViewType.THIRD));
                    }
                    else if(item.getOutchatter().equals("y")){
                        list.add(new ChattingItem(item.getContents(),item.getSenderid(),item.getReceiversid(),item.getTime(),item.getSeen(),item.getNewchatter(),item.getNewid(),item.getOutchatter(),item.getOutid(),Code.ViewType.FOURTH));
                    }
                    else if(item.getSenderid().equals(userid)){
                        list.add(new ChattingItem(item.getContents(),item.getSenderid(),item.getReceiversid(),item.getTime(),item.getSeen(),item.getNewchatter(),item.getNewid(),item.getOutchatter(),item.getOutid(),Code.ViewType.SECOND));
                    }
                    else{
                        list.add(new ChattingItem(item.getContents(),item.getSenderid(),item.getReceiversid(),item.getTime(),item.getSeen(),item.getNewchatter(),item.getNewid(),item.getOutchatter(),item.getOutid(),Code.ViewType.FIRST));
                    }
                }

                adapter = new ChattingAdapter(getApplicationContext(), list);
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void seenMessage(final String userid, String postid){
        ref = FirebaseDatabase.getInstance().getReference("DeliveryChat").child(postid);
        seenListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    ChattingItem2 item2 = snapshot1.getValue(ChattingItem2.class);
                    String s = item2.getContents();
                    if(s == null) continue;
                    if(!item2.getSenderid().equals(userid)){
                        ArrayList<ArrayList<String>> tmp = item2.getSeen();
                        for(ArrayList<String> ss : tmp){
                            if(ss.get(0).equals(userid)){
                                ss.set(1,"y");
                            }
                        }
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seen", tmp);
                        snapshot1.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ref.removeEventListener(seenListener);
        currentUser("none");
    }
}