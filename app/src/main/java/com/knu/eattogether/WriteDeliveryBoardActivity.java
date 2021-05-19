package com.knu.eattogether;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class WriteDeliveryBoardActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
    private DatabaseReference databaseReference4;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage;

    EditText et_title;
    EditText et_contents;

    SeekBar seekBar;
    TextView tv_max_people;

    ImageView iv_cancel;
    Button btn_complete;

    RecyclerView recyclerView;
    WriteBoardAdapter adapter;

    private ArrayList<WriteBoardItem> list = new ArrayList<>();

    private ArrayList<String> list2 = new ArrayList<>(); //imageurilist

    private ArrayList<String> list3 = new ArrayList<>(); //imagenamelist

    private ArrayList<String> remainlist = new ArrayList<>();

    private ArrayList<Integer> removelist = new ArrayList<>();

    private int cnt = 0; //업로드된 사진 갯수

    long time;
    long imagename;

    private static final int PICK_FROM_CAMEFA=0;
    private static final int PICK_FROM_ALBUM=1;
    private static final int CROP_FROM_CAMEFA=2;
    private static final int CROP_FROM_ALBUM=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_delivery_board);

        Intent intent = getIntent();
        final String postid = intent.getStringExtra("postid");
        final String cur_people = intent.getStringExtra("cur_people");

        et_title = findViewById(R.id.write_title);
        et_contents = findViewById(R.id.write_contents);

        seekBar = findViewById(R.id.seekBar);
        tv_max_people = findViewById(R.id.seekbar_text);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    tv_max_people.setText("0명");
                } else {
                    tv_max_people.setText(String.valueOf(progress) + "명");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if(postid != null){
            databaseReference = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    PostItem item = snapshot.getValue(PostItem.class);

                    seekBar.setProgress(Integer.parseInt(item.getMax_people().replace("명","")));
                    tv_max_people.setText(String.valueOf(seekBar.getProgress())+"명");
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

        }

        iv_cancel = findViewById(R.id.write_cancel);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.write_recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);

        btn_complete = findViewById(R.id.write_complete_btn);
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cnt = 0;
                list2.clear();
                list3.clear();

                if (postid == null) { //글쓰기
                    if (list.size() >= 2) {
                        storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://eattogether-1647c.appspot.com");

                        final ProgressDialog progressDialog = new ProgressDialog(WriteDeliveryBoardActivity.this);
                        progressDialog.setTitle("이미지 업로드 중...");
                        progressDialog.show();

                        for (int i = 0; i < list.size() - 1; i++) {
                            imagename = System.currentTimeMillis();
                            list3.add(imagename + "");
                            Uri file = list.get(i).getImageuri();
                            final StorageReference riversRef = storageRef.child("images/" + imagename);
                            UploadTask uploadTask = riversRef.putFile(file);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(WriteDeliveryBoardActivity.this, "업로드 실패!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    progressDialog.setMessage("업로드 중...");
                                }
                            });

                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    return riversRef.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {

                                        Uri downloadUri = task.getResult();
                                        list2.add(downloadUri.toString());
                                        cnt++;

                                        if (cnt == list.size() - 1) { //이미지파일 업로드 완료 후 동작
                                            progressDialog.dismiss();
                                            postimage2();
                                        }
                                    } else {
                                        //실패
                                    }
                                }
                            });
                        }
                    } else {
                        postimage3(postid,cur_people);
                    }
                }
                else{ //수정하기
                    remainlist.clear();
                    removelist.clear();
                    databaseReference4 = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
                    databaseReference4.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final PostItem item = snapshot.getValue(PostItem.class);
                            for (int k = 0; k < item.getImagenamelist().size(); k++) {
                                remainlist.add(item.getImagenamelist().get(k));
                            }
                            if (list.size() >= 2) {   //이미지 파일 있을 때
                                storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReferenceFromUrl("gs://eattogether-1647c.appspot.com");

                                final ProgressDialog progressDialog = new ProgressDialog(WriteDeliveryBoardActivity.this);
                                progressDialog.setTitle("이미지 업로드");
                                progressDialog.show();

                                for (int i = 0; i < list.size() - 1; i++) {
                                    imagename = System.currentTimeMillis();

                                    if (list.get(i).getImageuri().toString().contains("http")) {  //기존 이미지
                                        int index = 0;
                                        for (int j = 0; j < item.getImageurilist().size(); j++) {
                                            if (list.get(i).getImageuri().toString().equals(item.getImageurilist().get(j))) {
                                                index = j;
                                                removelist.add(index);
                                                break;
                                            }
                                        }

                                        list3.add(item.getImagenamelist().get(index));
                                        list2.add(item.getImageurilist().get(index));
                                        cnt++;

                                        if (cnt == list.size() - 1) { //이미지파일 업로드 완료 후 동작
                                            progressDialog.dismiss();
                                            postimage(postid, item.getCur_people());
                                        }

                                    } else {   //새로운 이미지
                                        list3.add(imagename + "");
                                        Uri file = list.get(i).getImageuri();
                                        //StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
                                        final StorageReference riversRef = storageRef.child("images/" + imagename);
                                        UploadTask uploadTask = riversRef.putFile(file);
                                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(WriteDeliveryBoardActivity.this, "업로드 실패!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                progressDialog.setMessage("업로드 중...");
                                            }
                                        });

                                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                            @Override
                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                if (!task.isSuccessful()) {
                                                    throw task.getException();
                                                }

                                                return riversRef.getDownloadUrl();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {

                                                    Uri downloadUri = task.getResult();
                                                    list2.add(downloadUri.toString());
                                                    cnt++;

                                                    if (cnt == list.size() - 1) { //이미지파일 업로드 완료 후 동작
                                                        progressDialog.dismiss();
                                                        postimage(postid, item.getCur_people());
                                                    }
                                                } else {
                                                    //실패
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                            else{
                                postimage3(postid,cur_people);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        if (postid == null) {
            list.add(new WriteBoardItem(null, Code.ViewType.SECOND));
            adapter = new WriteBoardAdapter(this, list);
            adapter.setOnPlusClickListener(new WriteBoardAdapter.OnPlusClickListener() {
                @Override
                public void onPlusClick(View v, int position) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), 0);
                }
            });
            adapter.setOnOtherClickListener(new WriteBoardAdapter.OnOtherClickListener() {
                @Override
                public void onOtherClick(View v, int position) {
                    list.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            });

            recyclerView.setAdapter(adapter);
        } else {   //수정
            databaseReference2 = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
            databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    PostItem item = snapshot.getValue(PostItem.class);

                    et_title.setText(item.getTitle());
                    et_contents.setText(item.getContents());

                    for (int i = 0; i < item.getImageurilist().size(); i++) {
                        Uri uri = Uri.parse(item.getImageurilist().get(i));
                        list.add(new WriteBoardItem(uri, Code.ViewType.FIRST));
                    }

                    list.add(new WriteBoardItem(null, Code.ViewType.SECOND));

                    adapter = new WriteBoardAdapter(getApplicationContext(), list);
                    adapter.setOnPlusClickListener(new WriteBoardAdapter.OnPlusClickListener() {
                        @Override
                        public void onPlusClick(View v, int position) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), 0);
                        }
                    });
                    adapter.setOnOtherClickListener(new WriteBoardAdapter.OnOtherClickListener() {
                        @Override
                        public void onOtherClick(View v, int position) {
                            list.remove(position);
                            adapter.notifyItemRemoved(position);
                        }
                    });

                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //이미지 선택 시 실행
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data.getClipData() == null && data.getData() != null) {   //이미지 1개 선택
                int lastindex = list.size() - 1;
                if (lastindex != -1) {
                    list.remove(lastindex);
                }
                list.add(new WriteBoardItem(data.getData(), Code.ViewType.FIRST));
                list.add(new WriteBoardItem(null, Code.ViewType.SECOND));
                adapter = new WriteBoardAdapter(this, list);
                adapter.setOnPlusClickListener(new WriteBoardAdapter.OnPlusClickListener() {
                    @Override
                    public void onPlusClick(View v, int position) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), 0);
                    }
                });
                adapter.setOnOtherClickListener(new WriteBoardAdapter.OnOtherClickListener() {
                    @Override
                    public void onOtherClick(View v, int position) {
                        list.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                });
                recyclerView.setAdapter(adapter);

            } else {   //이미지 2개이상 선택
                int lastindex = list.size() - 1;
                if (lastindex != -1) {
                    list.remove(lastindex);
                }
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    list.add(new WriteBoardItem(clipData.getItemAt(i).getUri(), Code.ViewType.FIRST));
                }
                list.add(new WriteBoardItem(null, Code.ViewType.SECOND));
                adapter = new WriteBoardAdapter(this, list);
                adapter.setOnPlusClickListener(new WriteBoardAdapter.OnPlusClickListener() {
                    @Override
                    public void onPlusClick(View v, int position) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), 0);
                    }
                });
                adapter.setOnOtherClickListener(new WriteBoardAdapter.OnOtherClickListener() {
                    @Override
                    public void onOtherClick(View v, int position) {
                        list.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                });
                recyclerView.setAdapter(adapter);
            }

        }
    }

    private void postimage3(String postid, String cur_people) {
        final String userid = mAuth.getCurrentUser().getUid();

        database = FirebaseDatabase.getInstance();

        databaseReference2 = database.getReference("Users").child(userid);
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UsersItem item = snapshot.getValue(UsersItem.class);

                if(postid==null) {
                    databaseReference = database.getReference("DeliveryPost").push();
                    String postid2 = databaseReference.getKey();

                    time = System.currentTimeMillis();

                    HashMap result = new HashMap<>();

                    result.put("title", et_title.getText().toString());
                    result.put("contents", et_contents.getText().toString());
                    result.put("writetime", System.currentTimeMillis() + "");
                    result.put("cur_people", "1");
                    result.put("postid", postid2);
                    result.put("userid", userid);
                    String mp = tv_max_people.getText().toString().replace("명","");
                    result.put("max_people", mp);
                    result.put("imageexist", "0");
                    result.put("imageurilist", list2);
                    result.put("imagenamelist", list3);

                    databaseReference.setValue(result);
                    finish();
                }
                else {
                    databaseReference = database.getReference("DeliveryPost").child(postid);

                    time = System.currentTimeMillis();

                    HashMap result = new HashMap<>();

                    result.put("title", et_title.getText().toString());
                    result.put("contents", et_contents.getText().toString());
                    result.put("writetime", System.currentTimeMillis()+"");
                    result.put("cur_people", cur_people);
                    result.put("postid", postid);
                    result.put("userid", userid);
                    String mp = tv_max_people.getText().toString().replace("명","");
                    result.put("max_people", mp);
                    result.put("imageexist", "0");
                    result.put("imageurilist", list2);
                    result.put("imagenamelist", list3);

                    databaseReference.setValue(result);
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void postimage2() {
        final String userid = mAuth.getCurrentUser().getUid();

        database = FirebaseDatabase.getInstance();

        databaseReference2 = database.getReference("Users").child(userid);
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UsersItem item = snapshot.getValue(UsersItem.class);

                databaseReference = database.getReference("DeliveryPost").push();
                String postid2 = databaseReference.getKey();

                time = System.currentTimeMillis();

                HashMap result = new HashMap<>();

                result.put("title", et_title.getText().toString());
                result.put("contents", et_contents.getText().toString());
                result.put("writetime", System.currentTimeMillis()+"");
                result.put("cur_people", "1");
                result.put("postid", postid2);
                result.put("userid", userid);
                String mp = tv_max_people.getText().toString().replace("명","");
                result.put("max_people", mp);
                result.put("imageexist", "1");
                result.put("imageurilist", list2);
                result.put("imagenamelist", list3);

                databaseReference.setValue(result);
                finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void postimage(String postid, String cur_people) {
        final String userid = mAuth.getCurrentUser().getUid();

        database = FirebaseDatabase.getInstance();

        if (postid == null) {
            databaseReference2 = database.getReference("Users").child(userid);
            databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UsersItem item = snapshot.getValue(UsersItem.class);

                    databaseReference = database.getReference("DeliveryPost").push();
                    String postid2 = databaseReference.getKey();

                    time = System.currentTimeMillis();

                    HashMap result = new HashMap<>();

                    result.put("title", et_title.getText().toString());
                    result.put("contents", et_contents.getText().toString());
                    result.put("writetime", System.currentTimeMillis()+"");
                    result.put("cur_people", "1");
                    result.put("postid", postid2);
                    result.put("userid", userid);
                    String mp = tv_max_people.getText().toString().replace("명","");
                    result.put("max_people", mp);
                    result.put("imageexist", "1");
                    result.put("imageurilist", list2);
                    result.put("imagenamelist", list3);


                    databaseReference.setValue(result);
                    finish();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            databaseReference = database.getReference("DeliveryPost").child(postid);

            time = System.currentTimeMillis();

            HashMap result = new HashMap<>();

            result.put("title", et_title.getText().toString());
            result.put("contents", et_contents.getText().toString());
            result.put("writetime", System.currentTimeMillis()+"");
            result.put("cur_people", cur_people);
            result.put("postid", postid);
            result.put("userid", userid);
            String mp = tv_max_people.getText().toString().replace("명","");
            result.put("max_people", mp);
            result.put("imageexist", "1");
            result.put("imageurilist", list2);
            result.put("imagenamelist", list3);

            databaseReference.setValue(result);
            finish();

        }

        for (int i = 0; i < remainlist.size(); i++) {
            int check = -1;
            for (int j = 0; j < removelist.size(); j++) {
                if (i == removelist.get(j)) {
                    check = 1;
                    break;
                }
            }
            if (check == -1) {
                imagedelete2(remainlist.get(i));
            }
        }


    }

    private void imagedelete2(String imagename) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://eattogether-1647c.appspot.com").child("images/" + imagename);
        storageRef.delete();
    }
}