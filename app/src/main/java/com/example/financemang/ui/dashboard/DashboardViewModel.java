package com.example.financemang.ui.dashboard;

import androidx.lifecycle.ViewModel;

import com.example.financemang.model.dao.TransactionDao;

import java.util.List;

public class DashboardViewModel extends ViewModel {
    private List<TransactionDao.CatSum> catSumList;

    public DashboardViewModel(){

    }

    public List<TransactionDao.CatSum> getCatSumList(){
        return catSumList;
    }
}