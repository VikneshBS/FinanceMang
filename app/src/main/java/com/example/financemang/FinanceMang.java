package com.example.financemang;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.financemang.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class FinanceMang extends Application {
    private float balance;
    private String currency_sym;
    SharedPreferences pref;
    HashMap<String, Float> Savings_Map;

    public void setPref(Context context){
        pref = context.getSharedPreferences(Constants.PACKAGE_NAME, MODE_PRIVATE);
    }

    public float getBalance() {
        balance = pref.getFloat(Constants.BALANCE, 0);
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
        pref.edit().putFloat(Constants.BALANCE, balance).apply();
    }

    public String getCurrency_sym() {
        currency_sym = pref.getString(Constants.CURRENCY_SYMBOL, "₹");
        return currency_sym;
    }

    public void setCurrency_sym(String currency_sym) {
        pref.edit().putString(Constants.CURRENCY_SYMBOL, currency_sym).apply();
        this.currency_sym = currency_sym;
    }

    public void setSavings_Map(HashMap<String, Float> Savings_Map){
        this.Savings_Map = Savings_Map;
        Gson gson = new Gson();
        String json = gson.toJson(Savings_Map);
        pref.edit().putString(Constants.SAVINGS_MAP,json).apply();
    }

    public HashMap<String, Float> getSavings_Map(){
        Gson gson = new Gson();
        String json = pref.getString(Constants.SAVINGS_MAP,"");
        Log.d(TAG, "getSavings_Map: json "+ json);
        java.lang.reflect.Type type = new TypeToken<HashMap<String,Float>>(){}.getType();
        Log.d(TAG, "getSavings_Map: type "+ type);
        HashMap<String, Float> obj = gson.fromJson(json, type);
        Savings_Map = obj;
        return obj;
    }
}
