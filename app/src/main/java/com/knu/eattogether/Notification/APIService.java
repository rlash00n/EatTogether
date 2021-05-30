package com.knu.eattogether.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAd9Tp2BE:APA91bHrYtZOgy3tziPlZXucv1tSPIn-zcAKxsKmTTlfXky8-HB1qCDm8yGhPvCdiLaj_lQ2Sh3gTqNUR9R-YO1Pmadt35XKPgOm_wxyaphqDXs-tkqpjBaavqz0kX_356In117ChfLB"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationData body);
}
