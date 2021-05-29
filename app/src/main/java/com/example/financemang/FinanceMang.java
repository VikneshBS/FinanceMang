package com.example.financemang;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.financemang.utils.Constants;

import java.text.DecimalFormat;

public class FinanceMang extends Application {
    private float balance;
    private String currency_sym;
    SharedPreferences pref;

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
}
