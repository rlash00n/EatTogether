package com.knu.eattogether.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.knu.eattogether.DeliveryChattingActivity;
import com.knu.eattogether.EatOutChattingActivity;
import com.knu.eattogether.R;
import com.knu.eattogether.UsersItem;

import java.util.Map;

public class FirebaseNotificationService extends FirebaseMessagingService {

    String contents;
    String postid;
    String senderid;
    String where;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public FirebaseNotificationService() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("Service", "ksh");
        Map<String, String> data_notify = remoteMessage.getData();

        //알림 제어
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser", "none");

        if(data_notify != null){
            Log.e("FCMService","received");
            contents = data_notify.get("contents");
            postid = data_notify.get("postid");
            senderid = data_notify.get("senderid");
            where = data_notify.get("where");
            final String uid = mAuth.getCurrentUser().getUid();

            if(!currentUser.equals(uid)){
                if(where.equals("1")) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(senderid);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UsersItem item = snapshot.getValue(UsersItem.class);
                            String title = item.getNickname();
                            Intent intent = new Intent(getBaseContext(), DeliveryChattingActivity.class);
                            intent.putExtra("postid", postid);
                            intent.setAction(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            String channelId = "mychannel";

                            Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder =
                                    new NotificationCompat.Builder(getApplicationContext(), channelId)
                                            .setSmallIcon(R.mipmap.ic_launcher_my)
                                            .setContentTitle(title)
                                            .setContentText(contents)
                                            .setAutoCancel(true)
                                            .setSound(defaultUri)
                                            .setContentIntent(pendingIntent);

                            NotificationManager notificationManger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                String channelName = "channelName";
                                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                                notificationManger.createNotificationChannel(channel);
                            }
                            notificationManger.notify(0, notificationBuilder.build());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else if(where.equals("2")){
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(senderid);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UsersItem item = snapshot.getValue(UsersItem.class);
                            String title = item.getNickname();
                            Intent intent = new Intent(getBaseContext(), EatOutChattingActivity.class);
                            intent.putExtra("postid", postid);
                            intent.setAction(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            String channelId = "mychannel";

                            Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder =
                                    new NotificationCompat.Builder(getApplicationContext(), channelId)
                                            .setSmallIcon(R.mipmap.ic_launcher_my)
                                            .setContentTitle(title)
                                            .setContentText(contents)
                                            .setAutoCancel(true)
                                            .setSound(defaultUri)
                                            .setContentIntent(pendingIntent);

                            NotificationManager notificationManger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                String channelName = "channelName";
                                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                                notificationManger.createNotificationChannel(channel);
                            }
                            notificationManger.notify(0, notificationBuilder.build());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}