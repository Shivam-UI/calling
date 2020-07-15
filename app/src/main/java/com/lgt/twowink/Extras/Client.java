package com.lgt.twowink.Extras;

import com.lgt.twowink.Interface.APIService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    public static Retrofit retrofit=null;

    public static Retrofit getClient(String url){
        if (retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit;
    }
    public static APIService getAPIServices(){
        return  retrofit.create(APIService.class);
    }

}
