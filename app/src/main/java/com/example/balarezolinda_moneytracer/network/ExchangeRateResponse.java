package com.example.balarezolinda_moneytracer.network;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class ExchangeRateResponse {

    @SerializedName("base")
    private String base;

    @SerializedName("date")
    private String date;

    @SerializedName("time_last_updated")
    private long lastUpdated;

    @SerializedName("rates")
    private Map<String, Double> rates;

    public String getBase() {
        return base;
    }

    public String getDate() {
        return date;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public double getRateOf(String currency) {
        if (rates != null && rates.containsKey(currency)) {
            return rates.get(currency);
        }
        return 0.0;
    }
}
