package com.suresh.currencyconverter;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Suresh on 27/09/17.
 */

public class Api {

    private static Api instance;
    private ApiListener listener;

    private Api(){
        final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(180, TimeUnit.SECONDS)
                .connectTimeout(180, TimeUnit.SECONDS)
                .build();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        listener = retrofit.create(ApiListener.class);
    }

    public static Api instance(){
        if (instance == null) {
            instance = new Api();
        }
        return instance;
    }

    public Call<ResponseCurrency> convert(String amount, String from, String to){
        return listener.convert(amount, from, to);
    }
}
