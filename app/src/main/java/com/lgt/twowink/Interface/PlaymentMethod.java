package com.lgt.twowink.Interface;

import com.lgt.twowink.Model.GatewayOrderStatus;
import com.lgt.twowink.Model.GetOrderIDRequest;
import com.lgt.twowink.Model.GetOrderIDResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PlaymentMethod {

    void makePayment(int position,String u_id,String pack_name,String Number,String price,String u_name,String u_email);


/*    @POST("/order")
    Call<GetOrderIDResponse> createOrder(@Body GetOrderIDRequest request);

    @GET("/status")
    Call<GatewayOrderStatus> orderStatus(@Query("env") String env, @Query("order_id") String orderID,
                                         @Query("transaction_id") String transactionID);*/
}
