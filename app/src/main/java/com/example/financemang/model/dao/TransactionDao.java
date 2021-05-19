package com.example.financemang.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.financemang.model.entity.TransactionModel;

import java.util.List;

@Dao
public interface TransactionDao {
    //annotation for inserting
    @Insert
    void insertTrans(TransactionModel transactionModel);

    //annotation for deleting
    @Delete
    void deleteTrans(TransactionModel transactionModel);

    //annotation for updating
    @Update
    void updateTrans(TransactionModel transactionModel);

    //Selecting last 5 transaction
    @Query("SELECT * FROM transactions ORDER BY trans_Id DESC LIMIT 5")
    LiveData<List<TransactionModel>> getTransactions();

    // Truncating the table - reset function
    //@Query()
    //void resetTable();
}
