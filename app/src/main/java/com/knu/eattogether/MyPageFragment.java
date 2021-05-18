package com.knu.eattogether;

import android.app.AlertDialog;
import android.app.backup.FullBackupDataOutput;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class MyPageFragment extends Fragment {

    DatabaseReference reference;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Context context;

    LinearLayout myposts;
    LinearLayout mychats;
    LinearLayout change_nick;

    LinearLayout logout;
    LinearLayout taltwe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        context = container.getContext();

        myposts = view.findViewById(R.id.mypage_myposts);
        myposts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mychats = view.findViewById(R.id.mypage_mychats);
        mychats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        change_nick = view.findViewById(R.id.mypage_changenick);
        change_nick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("닉네임 변경"); //제목
                builder.setMessage("변경하실 닉네임을 작성해주세요"); // 메시지

                final EditText nick = new EditText(context);
                builder.setView(nick);

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
                        MySharedPreferences.setPref(((LoginActivity)LoginActivity.context_login),"","",false);
                        mAuth.signOut();
                        startActivity(new Intent(context,LoginActivity.class)
                                .setAction(Intent.ACTION_MAIN)
                                .addCategory(Intent.CATEGORY_LAUNCHER)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        System.exit(0);
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


            }
        });

        return view;
    }

}