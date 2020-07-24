package com.lgt.twowink.Interface;

import com.lgt.twowink.Extras.MyApi;
import com.lgt.twowink.Model.MyResponse;
import com.lgt.twowink.Model.PaymentModel;
import com.lgt.twowink.Model.Sender;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    String serverKey="AAAA6wcXjr8:APA91bGrI5W3D3v8JKBcLBolncKGTgxNsGcTYoYoFGQdzSqbj1-W05bzeHXBPYjOEryYOfyZ7xOgcfxjc_K8Fz1GkU95NIxcE1nZobcnSILNuyuXjUpipJAPvHKgcPgdiNY-4L4BD5TF";

    @Headers({
            "Content-Type:application/json",
            "Authorization:key="+serverKey
    })


    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

    @POST("https://www.instamojo.com/api/1.1/payment-requests/")
    Call<PaymentModel> makePayment(@Header("X_Api_Key") String apiKey,@Header("X_Auth_Token") String Auth_Token, @Body() Map<String,String> makePayment);

}
