package com.example.financemang.ui.transaction;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.financemang.model.TransactionRepository;
import com.example.financemang.model.entity.TransactionModel;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel{
    final private TransactionRepository repository;
    final private LiveData<List<TransactionModel>> trans_list;

    //constructor of transactionViewModel
    public TransactionViewModel(@NonNull Application application){
        super(application);
        repository = new TransactionRepository(application);
        trans_list = repository.getTransactions();
    }

    //our activities will use these methods
    public void insert(TransactionModel transactionModel){
        repository.insert(transactionModel);
    }

    public void delete(TransactionModel transactionModel){
        repository.delete(transactionModel);
    }

    public LiveData<List<TransactionModel>> getTrans_list(){
        return trans_list;
    }

    public void resetAll(){
        repository.resetAll();
    }
}