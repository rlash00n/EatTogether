package com.knu.eattogether;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.knu.eattogether.Notification.APIService;
import com.knu.eattogether.Notification.Client;
import com.knu.eattogether.Notification.MyResponse;
import com.knu.eattogether.Notification.NotificationData;
import com.knu.eattogether.Notification.SendData;
import com.knu.eattogether.Notification.Token;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryChattingActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ChattingAdapter adapter;
    EditText input;
    Button send;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userid = auth.getCurrentUser().getUid();
    String postid;

    private ArrayList<ChattingItem> list = new ArrayList<>();

    //ChildEventListener seenListener;
    ValueEventListener seenListener;
    DatabaseReference ref;
    DatabaseReference reference1;

    ImageView back;
    TextView title;
    TextView people;
    Button out;

    String receiveralarm;

    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_chatting);

        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");

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
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                PostItem item = snapshot.getValue(PostItem.class);
                                String ss = item.getUserid();
                                if(userid.equals(ss)){
                                    // 글 작성자가 채팅방 나가기 -> 글 삭제 + 채팅방 삭제 + 채팅 리스트 삭제
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
                                    builder1.setMessage("게시글과 채팅방 모두 삭제됩니다.\n삭제하시겠습니까?");

                                    builder1.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            image_delete(postid);
                                        }
                                    });
                                    builder1.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    builder1.show();

                                }
                                else{
                                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("DeliveryChatList").child(postid);
                                    reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            ChatListItem item = snapshot.getValue(ChatListItem.class);
                                            ArrayList<String> tmp = new ArrayList<>();
                                            for(String s : item.getChatters_list()){
                                                if(!userid.equals(s)){
                                                    tmp.add(s);
                                                }
                                            }
                                            item.setChatters_list(tmp);
                                            reference1.setValue(item);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                                    //Chatting 에 out chatter 등장
                                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("DeliveryChat").child(postid).push();
                                    HashMap result = new HashMap<>();

                                    /*
                                    ArrayList<String> s = new ArrayList<>();
                                    ArrayList<ArrayList<String>> sss = new ArrayList<ArrayList<String>>();
                                    result.put("contents", null);
                                    result.put("senderid", null);
                                    result.put("receiverid", s);
                                    result.put("time", null);
                                    result.put("seen", sss);
                                    result.put("newid",null);
                                     */
                                    result.put("newchatter","n");
                                    result.put("outchatter","y");
                                    result.put("outid",userid);

                                    reference2.setValue(result);

                                    // post에 cur_people --;
                                    cur_people_minus(postid);

                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
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
        people = findViewById(R.id.chatting_people);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                PostItem item = snapshot.getValue(PostItem.class);
                title.setText(item.getTitle());
                people.setText(item.getCur_people());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

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

                                DatabaseReference reference4 = FirebaseDatabase.getInstance().getReference("Users").child(s);
                                reference4.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        UsersItem item = snapshot.getValue(UsersItem.class);
                                        receiveralarm = item.getAlram();

                                        if(receiveralarm.equals("1")){
                                            DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("UserTokenList").child(s);
                                            reference3.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()) {
                                                        final Token item2 = snapshot.getValue(Token.class);
                                                        for (String ss : item2.getTokenList()) {
                                                            // 접속해있는 기기(token)마다 알림 전송
                                                            final Runnable runnable = new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
                                                                    apiService.sendNotification(new NotificationData(new SendData(ct, postid, userid, "1"), ss))
                                                                            .enqueue(new Callback<MyResponse>() {
                                                                                @Override
                                                                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                                                                    if (response.code() == 200) {
                                                                                        if (response.body().success == 1) {
                                                                                            Log.e("Notification", "success");
                                                                                        }
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                                                                }
                                                                            });
                                                                }
                                                            };

                                                            Thread tr = new Thread(runnable);
                                                            tr.start();

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                }
                                            });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });

                            }
                        }

                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("DeliveryChat").child(postid).push();
                        HashMap result = new HashMap<>();
                        final long t = System.currentTimeMillis();

                        result.put("contents", ct);
                        result.put("senderid", userid);
                        result.put("receiversid", receiver);
                        result.put("time", t + "");
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

        reference1 = FirebaseDatabase.getInstance().getReference("DeliveryChat").child(postid);
        seenListener = reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                Map<String, Object> seenUsersMap = new HashMap<>();

                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    String key = snapshot1.getKey();
                    ChattingItem item_origin = snapshot1.getValue(ChattingItem.class);
                    ChattingItem item_modify = snapshot1.getValue(ChattingItem.class);

                    if(item_origin.getReceiversid().contains(userid) || userid.equals(item_origin.getSenderid())) {
                        Map<String, Object> modi = new HashMap<>();
                        modi = item_modify.getSeenUsers();
                        modi.put(userid, true);

                        item_modify.setSeenUsers(modi);

                    }
                    seenUsersMap.put(key, item_modify);

                    if(item_origin.getNewchatter().equals("y")){
                        list.add(new ChattingItem(item_origin.getContents(),item_origin.getSenderid(),item_origin.getReceiversid(),item_origin.getTime(),item_origin.getSeenUsers(),item_origin.getNewchatter(),item_origin.getNewid(),item_origin.getOutchatter(),item_origin.getOutid(),Code.ViewType.THIRD));
                    }
                    else if(item_origin.getOutchatter().equals("y")){
                        list.add(new ChattingItem(item_origin.getContents(),item_origin.getSenderid(),item_origin.getReceiversid(),item_origin.getTime(),item_origin.getSeenUsers(),item_origin.getNewchatter(),item_origin.getNewid(),item_origin.getOutchatter(),item_origin.getOutid(),Code.ViewType.FOURTH));
                    }
                    else if(item_origin.getSenderid().equals(userid)){
                        list.add(new ChattingItem(item_origin.getContents(),item_origin.getSenderid(),item_origin.getReceiversid(),item_origin.getTime(),item_origin.getSeenUsers(),item_origin.getNewchatter(),item_origin.getNewid(),item_origin.getOutchatter(),item_origin.getOutid(),Code.ViewType.SECOND));
                    }
                    else{
                        list.add(new ChattingItem(item_origin.getContents(),item_origin.getSenderid(),item_origin.getReceiversid(),item_origin.getTime(),item_origin.getSeenUsers(),item_origin.getNewchatter(),item_origin.getNewid(),item_origin.getOutchatter(),item_origin.getOutid(),Code.ViewType.FIRST));
                    }
                }

                if(list.size()>0 && !list.get(list.size() - 1).getSeenUsers().containsKey(userid)) {
                    FirebaseDatabase.getInstance().getReference("DeliveryChat").child(postid).updateChildren(seenUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {

                        }
                    });
                }

                adapter = new ChattingAdapter(getApplicationContext(), list);
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        //seenMessage(userid, postid);

    }

    /*
    private void seenMessage(final String userid, String postid){
        ref = FirebaseDatabase.getInstance().getReference("DeliveryChat").child(postid);
        seenListener = ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                ChattingItem2 item = snapshot.getValue(ChattingItem2.class);
                String s = item.getContents();
                if(!(s == null)) {
                    if (!item.getSenderid().equals(userid)) {
                        ArrayList<ArrayList<String>> tmp = item.getSeen();
                        for (ArrayList<String> ss : tmp) {
                            if (ss.get(0).equals(userid) && ss.get(1).equals("n")) {
                                ss.set(1, "y");
                            }
                        }
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seen", tmp);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                ChattingItem2 item = snapshot.getValue(ChattingItem2.class);
                String s = item.getContents();
                if(!(s == null)) {
                    if (!item.getSenderid().equals(userid)) {
                        ArrayList<ArrayList<String>> tmp = item.getSeen();
                        for (ArrayList<String> ss : tmp) {
                            if (ss.get(0).equals(userid) && ss.get(1).equals("n")) {
                                ss.set(1, "y");
                            }
                        }
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seen", tmp);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

     */

    /*
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
                            if(ss.get(0).equals(userid) && ss.get(1).equals("n")){
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

     */

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
        reference1.removeEventListener(seenListener);
        currentUser("none");
    }

    public void cur_people_minus(String postid){
        DatabaseReference reference4 = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
        reference4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                PostItem item = snapshot.getValue(PostItem.class);
                String ccp = item.getCur_people();
                int ccnt = Integer.parseInt(ccp) - 1;
                String ccp2 = ccnt + "";
                item.setCur_people(ccp2);
                reference4.setValue(item);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void image_delete(final String postid){
        DatabaseReference reference5 = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
        reference5.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostItem item = snapshot.getValue(PostItem.class);
                String s = item.getImageexist();
                if(s.equals("1")){
                    for(int i=0;i<item.getImagenamelist().size();i++){
                        storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://eattogether-1647c.appspot.com").child("images/"+item.getImagenamelist().get(i));
                        storageRef.delete();
                    }
                }
                post_delete(postid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void post_delete(final String postid){
        DatabaseReference reference6 = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
        reference6.removeValue();
        chatlist_delete(postid);
    }

    private void chatlist_delete(final String postid){
        DatabaseReference reference7 = FirebaseDatabase.getInstance().getReference("DeliveryChatList").child(postid);
        reference7.removeValue();
        chat_delete(postid);
    }

    private void chat_delete(final String postid){
        DatabaseReference reference8 = FirebaseDatabase.getInstance().getReference("DeliveryChat").child(postid);
        reference8.removeValue();
        finish();
    }
}