package com.suresh.currencyconverter;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Suresh on 27/09/17.
 */

public interface ApiListener {

    @GET("currency/commercial/exchange/{amount}-{from}/{to}/latest")
    Call<ResponseCurrency> convert(@Path("amount") String amount, @Path("from") String from, @Path("to") String to);
}
