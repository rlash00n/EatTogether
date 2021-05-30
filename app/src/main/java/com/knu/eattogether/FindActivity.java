package com.knu.eattogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.knu.eattogether.Notification.Token;

import org.jetbrains.annotations.NotNull;

public class FindActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;     //파이어베이스 인증
    private EditText mEtPwdEmail, mEtVerifyEmail, mEtVerifyPwd;
    private Button mBtnPwdFind, mBtnVerifyFind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mEtVerifyEmail = findViewById(R.id.et_verify_email);
        mEtVerifyPwd = findViewById(R.id.et_verify_pwd);
        mBtnVerifyFind = findViewById(R.id.btn_verify_find);
        mBtnVerifyFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = mEtVerifyEmail.getText().toString().trim();
                String strPwd = mEtVerifyPwd.getText().toString();

                if(TextUtils.isEmpty(strEmail)){
                    mEtVerifyEmail.setError("이메일을 입력해주세요!");
                    mEtVerifyEmail.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(strPwd)){
                    mEtVerifyPwd.setError("암호를 입력해주세요!");
                    mEtVerifyPwd.requestFocus();
                    return;
                }

                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(FindActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(mFirebaseAuth.getCurrentUser().isEmailVerified()){
                                Toast.makeText(FindActivity.this, "이미 인증을 완료하였습니다. 로그인해주세요!", Toast.LENGTH_LONG).show();
                                finish();
                            }else{
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(FindActivity.this, "이메일을 보냈습니다. 확인해주세요!", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(FindActivity.this, "실패하였습니다!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        } else{
                            //로그인 실패
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                            switch (errorCode) {

                                case "ERROR_INVALID_EMAIL":
                                    mEtVerifyEmail.setError("이메일 형식에 맞게 다시 입력해주세요!");
                                    mEtVerifyEmail.requestFocus();
                                    break;

                                case "ERROR_WRONG_PASSWORD":
                                    mEtVerifyPwd.setError("암호가 틀렸습니다.");
                                    mEtVerifyPwd.requestFocus();
                                    mEtVerifyPwd.setText("");
                                    break;

                                case "ERROR_USER_DISABLED":
                                    Toast.makeText(FindActivity.this, "관리자에 의해 이용 중지된 계정입니다.\n관리자에게 문의하세요!", Toast.LENGTH_LONG).show();
                                    break;

                                case "ERROR_USER_NOT_FOUND":
                                    mEtVerifyEmail.setError("등록되지 않은 이메일입니다.");
                                    mEtVerifyEmail.requestFocus();
                                    break;

                                default:
                                    Toast.makeText(FindActivity.this, "실패하였습니다!\n" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    }
                });
            }
        });


        mEtPwdEmail = findViewById(R.id.et_pwd_email);
        mBtnPwdFind = findViewById(R.id.btn_pwd_find);
        mBtnPwdFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이메일 보내기
                String strEmail = mEtPwdEmail.getText().toString().trim();
                if(TextUtils.isEmpty(strEmail)){
                    mEtPwdEmail.setError("이메일을 입력해주세요!");
                    mEtPwdEmail.requestFocus();
                    return;
                }
                mFirebaseAuth.sendPasswordResetEmail(strEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(FindActivity.this, "이메일을 보냈습니다. 확인해주세요!", Toast.LENGTH_LONG).show();
                            finish();
                        }else {
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            switch (errorCode) {

                                case "ERROR_INVALID_EMAIL":
                                    mEtPwdEmail.setError("이메일 형식에 맞게 다시 입력해주세요!");
                                    mEtPwdEmail.requestFocus();
                                    break;

                                case "ERROR_USER_NOT_FOUND":
                                    mEtPwdEmail.setError("등록되지 않은 이메일입니다.");
                                    mEtPwdEmail.requestFocus();
                                    break;

                                default:
                                    Toast.makeText(FindActivity.this, "메일 보내기 실패!\n" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    }
                });
            }
        });
    }
}