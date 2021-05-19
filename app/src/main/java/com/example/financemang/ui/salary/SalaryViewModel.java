package com.example.financemang.ui.salary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SalaryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SalaryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Salary fragment under Development");
    }

    public LiveData<String> getText() {
        return mText;
    }
}