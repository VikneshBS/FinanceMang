package com.example.financemang.model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.financemang.model.dao.TransactionDao;
import com.example.financemang.model.database.TransactionDatabase;
import com.example.financemang.model.entity.TransactionModel;

import java.util.List;

public class TransactionRepository {
    private final TransactionDao transactionDao;
    private final LiveData<List<TransactionModel>> trans_list;

    //Transaction Repository Constructor With Argument Application
    public TransactionRepository(Application application){
        TransactionDatabase transactionDatabase = TransactionDatabase.getInstance(application);
        transactionDao = transactionDatabase.transactionDao();
        trans_list = transactionDao.getTransactions();
    }
    //our viewmodel will access these methods
    public void insert(TransactionModel transaction){
        new InsertTransactionAsyncTask(transactionDao).execute(transaction);
    }

    public void delete(TransactionModel transaction){
        new DeleteTransactionAsyncTask(transactionDao).execute(transaction);
    }

    public void resetAll(){
        new ResetTranasctionAsyncTask(transactionDao).execute();
    }

    public static class ResetTranasctionAsyncTask extends AsyncTask<Void,Void,Void>{
        final private TransactionDao transactionDao;
        private ResetTranasctionAsyncTask(TransactionDao transactionDao){
            this.transactionDao = transactionDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            transactionDao.resetTable();
            return null;
        }
    }

    public LiveData<List<TransactionModel>> getTransactions(){
        return trans_list;
    }

    //all asyncronous tasks to run in background thread
    private static class InsertTransactionAsyncTask extends AsyncTask<TransactionModel, Void, Void>{
        final private TransactionDao transactionDao;
        private InsertTransactionAsyncTask(TransactionDao transactionDao){
            this.transactionDao = transactionDao;
        }
        @Override
        protected  Void doInBackground(TransactionModel... transactionModels){
            transactionDao.insertTrans(transactionModels[0]);
            return null;
        }
    }
    private static class DeleteTransactionAsyncTask extends AsyncTask<TransactionModel, Void, Void> {
        final private TransactionDao transactionDao;
        private DeleteTransactionAsyncTask(TransactionDao transactionDao) {
            this.transactionDao=transactionDao;
        }
        @Override
        protected Void doInBackground(TransactionModel... transactionModels) {
            transactionDao.deleteTrans(transactionModels[0]);
            return null;
        }
    }
}
