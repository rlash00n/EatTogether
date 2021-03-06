package com.knu.eattogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class DeliveryBoardDetailActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    String userid;

    DatabaseReference reference;
    DatabaseReference reference2;
    DatabaseReference reference3;
    DatabaseReference reference4;
    DatabaseReference reference5;
    DatabaseReference reference6;
    DatabaseReference reference7;
    DatabaseReference reference8;

    FirebaseStorage storage;

    ImageView back;
    Button remake;
    Button del;
    Button chat;

    CircleImageView profile;
    TextView nick;
    TextView time;

    TextView title;
    TextView contents;

    TextView cur_people;
    TextView max_people;
    TextView participating;

    RecyclerView recyclerView;
    BoardDetailAdapter adapter;
    ArrayList<BoardDetailItem> list = new ArrayList<>();

    private ArrayList<UsersItem> Userlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_board_detail);

        Intent intent = getIntent();
        final String postid = intent.getStringExtra("pid");
        final String postuserid = intent.getStringExtra("uid");

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.delivery_detail_swiperefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reference = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        PostItem item = snapshot.getValue(PostItem.class);

                        Long t = Long.parseLong(item.getWritetime());
                        time.setText(formatTimeString(t));

                        title.setText(item.getTitle());
                        contents.setText(item.getContents());

                        cur_people.setText(item.getCur_people());
                        max_people.setText(item.getMax_people());

                        list.clear();
                        list.add(new BoardDetailItem(item.getImageurilist()));
                        adapter = new BoardDetailAdapter(DeliveryBoardDetailActivity.this, list);
                        recyclerView.setAdapter(adapter);

                        reference2 = FirebaseDatabase.getInstance().getReference("Users").child(item.getUserid());
                        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                UsersItem item1 = snapshot.getValue(UsersItem.class);

                                Glide.with(DeliveryBoardDetailActivity.this).load(item1.getProfileuri()).into(profile);
                                nick.setText(item1.getNickname());
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        userid = mAuth.getCurrentUser().getUid();

        profile = findViewById(R.id.detail_profileuri);
        nick = findViewById(R.id.detail_nickname);
        time = findViewById(R.id.detail_time);

        title = findViewById(R.id.detail_title);
        contents = findViewById(R.id.detail_contents);

        cur_people = findViewById(R.id.detail_cur_people);
        max_people = findViewById(R.id.detail_max_people);

        recyclerView = findViewById(R.id.detail_recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(DeliveryBoardDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);

        back = findViewById(R.id.detail_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        remake = findViewById(R.id.detail_remake);
        del = findViewById(R.id.detail_del);
        chat = findViewById(R.id.detail_chat);
        participating = findViewById(R.id.detail_participate);

        if(!userid.equals(postuserid)){
            remake.setVisibility(View.GONE);
            del.setVisibility(View.GONE);
        }
        else{
            chat.setVisibility(View.GONE);
            participating.setVisibility(View.VISIBLE);
        }

        reference3 = FirebaseDatabase.getInstance().getReference("DeliveryChatList").child(postid);
        reference3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ChatListItem item = snapshot.getValue(ChatListItem.class);
                ArrayList<String> tmp = item.getChatters_list();
                for(String s : tmp){
                    if(s.equals(userid)){
                        chat.setVisibility(View.GONE);
                        participating.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        remake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference8 = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
                reference8.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        PostItem item = snapshot.getValue(PostItem.class);
                        Intent intent1 = new Intent(DeliveryBoardDetailActivity.this, WriteDeliveryBoardActivity.class);
                        intent1.putExtra("postid", postid);
                        intent1.putExtra("cur_people", item.getCur_people());
                        startActivity(intent1);
                        finish();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("???????????? ?????????????????????????");
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
                        builder1.setMessage("???????????? ?????? ????????? ?????? ???????????????.\n?????????????????????????");
                        builder1.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                image_delete(postid);
                            }
                        });
                        builder1.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder1.show();
                    }
                });
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("?????? ????????????");
                builder.setMessage("?????? ???????????????????");
                builder.setPositiveButton("??????",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reference8 = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
                                reference8.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        PostItem item = snapshot.getValue(PostItem.class);
                                        cur_people.setText(item.getCur_people());
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                int cp = Integer.parseInt(String.valueOf(cur_people.getText()));
                                int mp = Integer.parseInt(String.valueOf(max_people.getText()));
                                if(cp < mp) {
                                    cur_people_plus(postid);
                                    //ChatList ??? ???????????? ??????
                                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("DeliveryChatList").child(postid);
                                    reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            ChatListItem item = snapshot.getValue(ChatListItem.class);
                                            ArrayList<String> tmp = item.getChatters_list();
                                            tmp.add(userid);
                                            item.setChatters_list(tmp);
                                            reference1.setValue(item);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                                    //Chatting ??? new chatter ??????
                                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("DeliveryChat").child(postid).push();
                                    HashMap result = new HashMap<>();

                                    result.put("newchatter","y");
                                    result.put("newid",userid);
                                    result.put("outchatter","n");

                                    reference2.setValue(result);


                                    Intent intent1 = new Intent(DeliveryBoardDetailActivity.this,DeliveryChattingActivity.class);
                                    intent1.putExtra("postid", postid);
                                    startActivity(intent1);
                                    finish();
                                }
                                else{
                                    Toast.makeText(DeliveryBoardDetailActivity.this,"????????? ??????????????????",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                builder.setNegativeButton("??????",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostItem item = snapshot.getValue(PostItem.class);

                Long t = Long.parseLong(item.getWritetime());
                time.setText(formatTimeString(t));

                title.setText(item.getTitle());
                contents.setText(item.getContents());

                cur_people.setText(item.getCur_people());
                max_people.setText(item.getMax_people());

                list.clear();
                list.add(new BoardDetailItem(item.getImageurilist()));
                adapter = new BoardDetailAdapter(DeliveryBoardDetailActivity.this, list);
                adapter.setOnImageClickListener(new BoardDetailAdapter.OnImageClickListener() {
                    @Override
                    public void onImageClick(View v, int position) {
                        Intent intent = new Intent(DeliveryBoardDetailActivity.this, OnlyImageActivity.class);
                        intent.putExtra("position", position);
                        intent.putStringArrayListExtra("list", list.get(0).getImageurilist());
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(adapter);

                reference2 = FirebaseDatabase.getInstance().getReference("Users").child(item.getUserid());
                reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        UsersItem item1 = snapshot.getValue(UsersItem.class);

                        Glide.with(DeliveryBoardDetailActivity.this).load(item1.getProfileuri()).into(profile);
                        nick.setText(item1.getNickname());
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void cur_people_plus(String postid){
        reference4 = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
        reference4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                PostItem item = snapshot.getValue(PostItem.class);
                String ccp = item.getCur_people();
                int ccnt = Integer.parseInt(ccp) + 1;
                String ccp2 = ccnt + "";
                item.setCur_people(ccp2);
                reference4.setValue(item);

                cur_people.setText(item.getCur_people());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public String formatTimeString(long regTime) {
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;
        String msg = "";
        if (diffTime < DeliveryBoardAdapter.TIME_MAXIMUM.SEC) {
            msg = "?????? ???";
        } else if ((diffTime /= DeliveryBoardAdapter.TIME_MAXIMUM.SEC) < DeliveryBoardAdapter.TIME_MAXIMUM.MIN) {
            msg = diffTime + "??? ???";
        } else if ((diffTime /= DeliveryBoardAdapter.TIME_MAXIMUM.MIN) < DeliveryBoardAdapter.TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + "?????? ???";
        } else if ((diffTime /= DeliveryBoardAdapter.TIME_MAXIMUM.HOUR) < DeliveryBoardAdapter.TIME_MAXIMUM.DAY) {
            msg = (diffTime) + "??? ???";
        } else if ((diffTime /= DeliveryBoardAdapter.TIME_MAXIMUM.DAY) < DeliveryBoardAdapter.TIME_MAXIMUM.MONTH) {
            msg = (diffTime) + "??? ???";
        } else {
            msg = (diffTime) + "??? ???";
        }
        return msg;
    }

    private void image_delete(final String postid){
        reference5 = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
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
        reference6 = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
        reference6.removeValue();
        chatlist_delete(postid);
    }

    private void chatlist_delete(final String postid){
        reference7 = FirebaseDatabase.getInstance().getReference("DeliveryChatList").child(postid);
        reference7.removeValue();
        chat_delete(postid);
    }

    private void chat_delete(final String postid){
        reference8 = FirebaseDatabase.getInstance().getReference("DeliveryChat").child(postid);
        reference8.removeValue();
        finish();
    }
}