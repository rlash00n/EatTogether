package com.knu.eattogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;     //파이어베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private EditText mEtEmail, mEtPwd;
    private CheckBox chk_auto;

    public static Context context_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context_login = this;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("fff");

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        chk_auto = findViewById(R.id.chk_auto);

        if(MySharedPreferences.getPrefChecked(LoginActivity.context_login)) {
            String strEmail = MySharedPreferences.getPrefEmail(LoginActivity.context_login);
            String strPwd = MySharedPreferences.getPrefPwd(LoginActivity.context_login);
            SignIn(strEmail, strPwd);
        }

        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그인 요청
                String strEmail="", strPwd="";
                strEmail = mEtEmail.getText().toString().trim();
                strPwd = mEtPwd.getText().toString();

                if(TextUtils.isEmpty(strEmail)){
                    Toast.makeText(LoginActivity.this, "이메일을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(strPwd)){
                    Toast.makeText(LoginActivity.this, "암호를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Login
                SignIn(strEmail, strPwd);
            }
        });

        TextView tv_register = findViewById(R.id.tv_register);
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  회원가입 화면으로 이동
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        TextView tv_findpwd = findViewById(R.id.tv_findpwd);
        tv_findpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FindActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void SignIn(String strEmail, String strPwd){
        mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(mFirebaseAuth.getCurrentUser().isEmailVerified()){
                        if(chk_auto.isChecked()){
                            MySharedPreferences.setPref(context_login, strEmail, strPwd,true);
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); //현재 액티비티 파괴
                    }else{
                        Toast.makeText(LoginActivity.this, "인증 메일을 확인해주세요!", Toast.LENGTH_LONG).show();
                    }
                } else{
                    //로그인 실패
                    String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                    switch (errorCode) {

                        case "ERROR_INVALID_EMAIL":
                            mEtEmail.setError("이메일 형식에 맞게 다시 입력해주세요!");
                            mEtEmail.requestFocus();
                            break;

                        case "ERROR_WRONG_PASSWORD":
                            mEtPwd.setError("암호가 틀렸습니다.");
                            mEtPwd.requestFocus();
                            mEtPwd.setText("");
                            break;

                        case "ERROR_USER_DISABLED":
                            Toast.makeText(LoginActivity.this, "관리자에 의해 이용 중지된 계정입니다.\n관리자에게 문의하세요!", Toast.LENGTH_LONG).show();
                            break;

                        case "ERROR_USER_NOT_FOUND":
                            mEtEmail.setError("등록되지 않은 이메일입니다.");
                            mEtEmail.requestFocus();
                            break;

                        default:
                            Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다.\n" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }
        });
    }
}