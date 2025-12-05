package com.example.balarezolinda_moneytracer.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    // https://api.exchangerate-api.com/v4/latest/USD
    @GET("v4/latest/{currency}")
    Call<ExchangeRateResponse> getExchangeRates(@Path("currency") String baseCurrency);

}
