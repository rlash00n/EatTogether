package com.knu.eattogether;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends PreferenceFragmentCompat {

    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    SharedPreferences pref;

    public Context ccttxx;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_preferences, rootKey);

        ccttxx = getContext();

    }

    @Override
    public boolean onPreferenceTreeClick(androidx.preference.Preference preference) {
        String key = preference.getKey();
        switch(key){
            case "logout":
                AlertDialog.Builder dlg = new AlertDialog.Builder(ccttxx);
                dlg.setTitle("계발에서 개발까지"); //제목
                dlg.setMessage("안녕하세요 계발에서 개발까지 입니다."); // 메시지
//                버튼 클릭시 동작
                dlg.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        //토스트 메시지
                        Toast.makeText(ccttxx,"확인을 눌르셨습니다.",Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
                break;
        }

        return super.onPreferenceTreeClick(preference);
    }

}
