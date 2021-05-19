package com.example.financemang.model.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.financemang.model.dao.TransactionDao;
import com.example.financemang.model.entity.TransactionModel;

//annotation for getting model class, setting version and exportSchema
@Database(entities = TransactionModel.class, version = 1, exportSchema = false)
public abstract class TransactionDatabase extends RoomDatabase {
    //instance of given class
    private static TransactionDatabase instance;

    //define an abstract method of return type TransactionDao
    public abstract TransactionDao transactionDao();

    //method to create instance of database named trans_list
    public static synchronized TransactionDatabase getInstance(Context context){
        if(instance==null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    TransactionDatabase.class,"trans_list")
                    .fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
