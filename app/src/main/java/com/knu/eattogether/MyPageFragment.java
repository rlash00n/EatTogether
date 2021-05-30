package com.knu.eattogether;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.knu.eattogether.Notification.Token;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class MyPageFragment extends Fragment {

    DatabaseReference ref;
    DatabaseReference reference;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Context context;
    String userid;

    CircleImageView profile;
    TextView nickname;

    LinearLayout myposts;
    LinearLayout mychats;
    LinearLayout change_nick;

    Switch aSwitch;
    LinearLayout logout;
    LinearLayout taltwe;

    ValueEventListener listener;

    private FirebaseStorage storage;
    private Uri imageuri;
    private static final int IMAGE_REQUEST = 1;
    int flag = -1;
    private StorageTask uploadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        context = container.getContext();

        profile = view.findViewById(R.id.mypage_profile);
        nickname = view.findViewById(R.id.mypage_nickname);

        userid = mAuth.getUid();
        ref = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    UsersItem item = snapshot.getValue(UsersItem.class);
                    Activity activity = (Activity) view.getContext();
                    if (activity.isFinishing()) {
                        return;
                    }
                    Glide.with(view.getContext()).load(item.getProfileuri()).into(profile);
                    nickname.setText(item.getNickname());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBottomSheetDialog(v);
            }
        });

        myposts = view.findViewById(R.id.mypage_myposts);
        myposts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,MyPostsActivity.class);
                startActivity(intent);
            }
        });

        mychats = view.findViewById(R.id.mypage_mychats);
        mychats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyChatListActivity.class);
                startActivity(intent);
            }
        });

        change_nick = view.findViewById(R.id.mypage_changenick);
        change_nick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText nick = new EditText(context);
                //edittext margin 조정
                FrameLayout container = new FrameLayout(getContext());
                FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                nick.setLayoutParams(params);
                container.addView(nick);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("닉네임 변경"); //제목
                builder.setMessage("\n변경하실 닉네임을 작성해주세요"); // 메시지

                builder.setIcon(R.drawable.nick_edit).setView(container);

                builder.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String changenick = nick.getText().toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                Boolean wanttoclose = true;
                                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                    UsersItem item1 = snapshot1.getValue(UsersItem.class);
                                    String nn = item1.getNickname();
                                    if(changenick.equals(nn)){
                                        nick.setError("중복되는 닉네임입니다.");
                                        nick.requestFocus();
                                        wanttoclose = false;
                                        break;
                                    }
                                }
                                if(TextUtils.isEmpty(changenick)){
                                    nick.setError("닉네임을 입력해주세요.");
                                    nick.requestFocus();
                                    wanttoclose = false;
                                }
                                if(wanttoclose){
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    UsersItem item2 = snapshot.child(user.getUid()).getValue(UsersItem.class);
                                    item2.setNickname(changenick);
                                    reference.child(user.getUid()).setValue(item2);
                                    dialog.dismiss();
                                }
                                else {return;}
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    }
                });
            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UsersItem item = snapshot.getValue(UsersItem.class);
                if(item.getAlram().equals("1")){
                    aSwitch.setChecked(true);
                }
                else{
                    aSwitch.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        aSwitch = view.findViewById(R.id.alram_switch);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){  //체크된 상태 취소시 코드
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UsersItem item = snapshot.getValue(UsersItem.class);
                            item.setAlram("1");
                            reference.setValue(item);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else{   //체크된 상태로 만들시 코드
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UsersItem item = snapshot.getValue(UsersItem.class);
                            item.setAlram("0");
                            reference.setValue(item);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그아웃 기능
                AlertDialog.Builder dlg_logout = new AlertDialog.Builder(context);
                dlg_logout.setTitle("로그아웃"); //제목
                dlg_logout.setMessage("로그아웃 하시겠습니까?"); // 메시지

                dlg_logout.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<String> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(context, "토큰 생성 실패", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String token = task.getResult();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserTokenList").child(userid);
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        Token item = snapshot.getValue(Token.class);
                                        ArrayList<String> tmp = new ArrayList<>();
                                        for(String s : item.getTokenList()){
                                            if(!s.equals(token)){
                                                tmp.add(s);
                                            }
                                        }
                                        item.setTokenList(tmp);

                                        reference.setValue(item);

                                        MySharedPreferences.setPref(((LoginActivity)LoginActivity.context_login),"","",false);
                                        mAuth.signOut();

                                        ActivityCompat.finishAffinity(((MainActivity)MainActivity.context));
                                        startActivity(new Intent(context,LoginActivity.class));
                                        System.exit(0);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                    }
                });
                dlg_logout.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dlg_logout.show();
            }
        });

        taltwe = view.findViewById(R.id.taltwe);
        taltwe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //탈퇴 기능
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("탈퇴하기");
                builder.setMessage("회원 탈퇴 하시겠습니까?");

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
                        builder1.setMessage("게시글과 채팅방이 모두 삭제됩니다.\n탈퇴 하시겠습니까?");

                        builder1.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delivery_image_delete(userid);
                                //eatout_image_delete(userid);

                                usertokenlist_delete(userid);
                            }
                        });
                        builder1.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder1.show();
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

        return view;
    }

    private void setBottomSheetDialog(View view){

        View v;

        final BottomSheetDialog dialog = new BottomSheetDialog(view.getContext(),R.style.BottomSheetDialogTheme);
        v  = LayoutInflater.from(getContext()).inflate(R.layout.profile_bottom_sheet_item,
                (LinearLayout)view.findViewById(R.id.bottomSheetContainer));

        LinearLayout layout1 = v.findViewById(R.id.bottomsheet1);
        LinearLayout layout2 = v.findViewById(R.id.bottomsheet2);

        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
                dialog.dismiss();
            }
        });

        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //프로필 사진 삭제
                final DatabaseReference reference6 = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                reference6.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UsersItem item = snapshot.getValue(UsersItem.class);
                        if(!item.getProfileuri().equals("https://firebasestorage.googleapis.com/v0/b/eattogether-1647c.appspot.com/o/profile%2Fdefault_profile.png?alt=media&token=e114db4a-8593-4403-a706-3313c74b8562")){
                            String tmp = item.getProfileimagename();
                            StorageReference reference5 = FirebaseStorage.getInstance().getReferenceFromUrl("gs://eattogether-1647c.appspot.com").child("profile").child(tmp);
                            reference5.delete();

                            item.setProfileuri("https://firebasestorage.googleapis.com/v0/b/eattogether-1647c.appspot.com/o/profile%2Fdefault_profile.png?alt=media&token=e114db4a-8593-4403-a706-3313c74b8562");
                            item.setProfileimagename(null);
                            reference6.setValue(item);
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        dialog.setContentView(v);

        dialog.show();

    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("이미지 업로드");
        progressDialog.show();

        flag = -1;
        if(imageuri != null){
            storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://eattogether-1647c.appspot.com");

            final String profileimagename = System.currentTimeMillis()+"";

            Uri file = imageuri;
            final StorageReference riversRef = storageRef.child("profile/" + profileimagename);
            UploadTask uploadTask = riversRef.putFile(file);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    flag = 1;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
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
                    if(task.isSuccessful() && flag == 1){
                        Uri downloadUri = task.getResult();
                        profileupdate(downloadUri.toString(), profileimagename);
                        progressDialog.dismiss();
                    }
                }
            });

        }
        else{
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void profileupdate(final String downloadUri, final String profileimagename){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UsersItem item = snapshot.getValue(UsersItem.class);
                item.setProfileimagename(profileimagename);
                item.setProfileuri(downloadUri);
                reference.setValue(item);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageuri = data.getData();

            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            }
            else{
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UsersItem item = snapshot.getValue(UsersItem.class);
                        if(item.getProfileuri().equals("https://firebasestorage.googleapis.com/v0/b/eattogether-1647c.appspot.com/o/profile%2Fdefault_profile.png?alt=media&token=e114db4a-8593-4403-a706-3313c74b8562")){
                            uploadImage();
                        }
                        else{
                            StorageReference reference5 = FirebaseStorage.getInstance().getReferenceFromUrl("gs://eattogether-1647c.appspot.com").child("profile").child(item.getProfileimagename());
                            reference5.delete();
                            uploadImage();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

    }

    private void delivery_image_delete(String userid){
        ArrayList<String> postlist = new ArrayList<>();
        ArrayList<String> postlist1 = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DeliveryPost");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    PostItem item = snapshot1.getValue(PostItem.class);
                    if(userid.equals(item.getUserid())){
                        String s = item.getImageexist();
                        if(s.equals("1")){
                            for(int i=0;i<item.getImagenamelist().size();i++){
                                storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReferenceFromUrl("gs://eattogether-1647c.appspot.com").child("images/"+item.getImagenamelist().get(i));
                                storageRef.delete();
                            }
                        }
                        postlist.add(item.getPostid());
                    }
                }
                delivery_post_delete(postlist);
                delivery_chatlist_delete(postlist);
                delivery_chat_delete(postlist);

                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("DeliveryChatList");
                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            ChatListItem item = snapshot1.getValue(ChatListItem.class);
                            for(String ss : item.getChatters_list()){
                                if(userid.equals(ss)){
                                    postlist1.add(item.getPostid());
                                    break;
                                }
                            }
                        }
                        delivery_chatlist_fix(userid,postlist1);
                        delivery_chat_fix(postlist1);
                        delivery_post_fix(postlist1);

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

    // 작성한 글,채팅방,채팅리스트 삭제
    private void delivery_post_delete(ArrayList<String> postlist){
        for(String postid : postlist){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
            reference.removeValue();
        }
    }

    private void delivery_chatlist_delete(ArrayList<String> postlist){
        for(String postid : postlist){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DeliveryChatList").child(postid);
            reference.removeValue();
        }
    }

    private void delivery_chat_delete(ArrayList<String> postlist){
        for(String postid : postlist){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DeliveryChat").child(postid);
            reference.removeValue();
        }
    }

    // 참여한 글,채팅방,채팅리스트 수정
    private void delivery_chatlist_fix(String userid, ArrayList<String> postlist){
        for(String postid : postlist){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DeliveryChatList").child(postid);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    reference.setValue(item);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    }

    private void delivery_chat_fix(ArrayList<String> postlist){
        for(String postid : postlist){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DeliveryChat").child(postid).push();
            HashMap result = new HashMap<>();

            result.put("newchatter","n");
            result.put("outchatter","y");
            result.put("outid",userid);

            reference.setValue(result);
        }
    }

    private void delivery_post_fix(ArrayList<String> postlist){
        for(String postid : postlist){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DeliveryPost").child(postid);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    PostItem item = snapshot.getValue(PostItem.class);
                    String ccp = item.getCur_people();
                    int ccnt = Integer.parseInt(ccp) - 1;
                    String ccp2 = ccnt + "";
                    item.setCur_people(ccp2);

                    reference.setValue(item);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    }

    private void usertokenlist_delete(String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserTokenList").child(userid);
        reference.removeValue();

        users_delete(userid);
    }

    private void users_delete(String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UsersItem item = snapshot.getValue(UsersItem.class);
                if(!item.getProfileuri().equals("https://firebasestorage.googleapis.com/v0/b/eattogether-1647c.appspot.com/o/profile%2Fdefault_profile.png?alt=media&token=e114db4a-8593-4403-a706-3313c74b8562")){
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://eattogether-1647c.appspot.com").child("profile/"+item.getProfileimagename());
                    storageRef.delete();

                    reference.removeValue();
                    last_delete();
                }
                else{
                    reference.removeValue();
                    last_delete();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void last_delete(){
        MySharedPreferences.setPref(((LoginActivity)LoginActivity.context_login),"","",false);
        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ActivityCompat.finishAffinity(((MainActivity)MainActivity.context));
                    System.exit(0);
                }
            }
        });
    }
}