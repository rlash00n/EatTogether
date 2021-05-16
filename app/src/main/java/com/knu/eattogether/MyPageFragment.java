package com.knu.eattogether;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MyPageFragment extends Fragment {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Button btn_logout;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        context = container.getContext();

        btn_logout = view.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
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
        return view;
    }

}