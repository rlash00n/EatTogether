package com.knu.eattogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;

public class FindActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;     //파이어베이스 인증
    private EditText mEtEmail;
    private Button mBtnFind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mEtEmail = findViewById(R.id.et_email);
        mBtnFind = findViewById(R.id.btn_find);
        mBtnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이메일 보내기
                String strEmail = mEtEmail.getText().toString().trim();
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
                                    mEtEmail.setError("이메일 형식에 맞게 다시 입력해주세요!");
                                    mEtEmail.requestFocus();
                                    break;

                                case "ERROR_USER_NOT_FOUND":
                                    mEtEmail.setError("등록되지 않은 이메일입니다.");
                                    mEtEmail.requestFocus();
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