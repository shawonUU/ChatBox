package com.example.chatbox.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAXPN_RwY:APA91bERclVlzvGNpvpB1pXFlYEcQ3RhlYVkPjv1AKwEYRbe6GAjfklaPL3XdzvYHY7pz6bbFNPUez5ECHJa5PQ29SDKS6EZ0NuwkGgmgynOGC5-i_BVA80bU2hpclaQswZka3cuhgNW"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
