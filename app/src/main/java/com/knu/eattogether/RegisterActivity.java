package com.knu.eattogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import org.jetbrains.annotations.NotNull;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;     //파이어베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private EditText mEtEmail, mEtPwd, mEtNick;
    private Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mEtNick = findViewById(R.id.et_nick);

        mBtnRegister = findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 처리 시작
                String strEmail = mEtEmail.getText().toString().trim();
                String strPwd = mEtPwd.getText().toString();
                String strNick = mEtNick.getText().toString();
                if(TextUtils.isEmpty(strEmail)){
                    mEtEmail.setError("이메일을 입력해주세요!");
                    mEtEmail.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(strPwd)){
                    mEtPwd.setError("암호를 입력해주세요!");
                    mEtPwd.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(strNick)){
                    mEtNick.setError("닉네임을 입력해주세요!");
                    mEtNick.requestFocus();
                    return;
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        int flag = 0;
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            UsersItem item1 = snapshot1.getValue(UsersItem.class);
                            String nick = item1.getNickname();
                            if(strNick.equals(nick)){
                                mEtNick.setError("중복되는 닉네임입니다.");
                                mEtNick.requestFocus();
                                flag = 1;
                                break;
                            }
                        }
                        if(flag==1){
                            return;
                        }
                        else{
                            //Firebase Auth 진행
                            mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    UsersItem item = new UsersItem();
                                                    item.setIdToken(firebaseUser.getUid());
                                                    item.setEmail(firebaseUser.getEmail());
                                                    item.setPassword(strPwd);
                                                    item.setNickname(strNick);
                                                    item.setProfileimagename(null);
                                                    item.setProfileuri("https://firebasestorage.googleapis.com/v0/b/eattogether-1647c.appspot.com/o/profile%2Fdefault_profile.png?alt=media&token=e114db4a-8593-4403-a706-3313c74b8562");
                                                    item.setAlram("1");

                                                    //  setValue : database에 insert (삽입) 행위
                                                    mDatabaseRef.child(firebaseUser.getUid()).setValue(item);

                                                    Toast.makeText(RegisterActivity.this, "인증 이메일이 전송되었습니다.\n확인 후 로그인 해주세요!", Toast.LENGTH_LONG).show();
                                                    finish();
                                                }
                                                else{
                                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else{
                                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                        switch (errorCode) {

                                            case "ERROR_INVALID_EMAIL":
                                                mEtEmail.setError("이메일 형식에 맞게 다시 입력해주세요!");
                                                mEtEmail.requestFocus();
                                                break;

                                            case "ERROR_EMAIL_ALREADY_IN_USE":
                                                mEtEmail.setError("이미 사용중인 이메일입니다!");
                                                mEtEmail.requestFocus();
                                                break;

                                            case "ERROR_WEAK_PASSWORD":
                                                mEtPwd.setError("비밀번호를 6자리 이상 설정해주세요!");
                                                mEtPwd.requestFocus();
                                                break;

                                            default:
                                                Toast.makeText(RegisterActivity.this, "실패하였습니다. 다시 입력해주세요!\n" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                break;
                                        }
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            }
        });
    }
}