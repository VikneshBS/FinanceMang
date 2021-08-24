package com.example.financemang.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

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

    //Selecting last 5 transaction
    @Query("SELECT * FROM transactions ORDER BY trans_Id DESC LIMIT 5")
    LiveData<List<TransactionModel>> getTransactions();

    //selecting distinct category
    @Query("SELECT DISTINCT Category FROM TRANSACTIONS order by Category")
    LiveData<List<String>> getDistinctCat();

    @Query("SELECT acc, COUNT(trans_Id) as count,SUM(amount) as total from transactions group by acc")
    LiveData<List<AccSum>> getSummaryByAcc();

    @Query("SELECT Category,acc,SUM(amount) as total from transactions group by Category,acc order by total desc")
    LiveData<List<CatSum>> getCatSum();

    @Query("SELECT acc, COUNT(trans_Id) as count,SUM(amount) as total from transactions WHERE Category like '%SAVINGS%' group by acc ")
    LiveData<List<AccSum>> getSaveSummaryByAcc();

    /*@Query("SELECT Category,acc,SUM(amount) as total from transactions WHERE Category like '%SAVINGS%' group by Category,acc order by total desc")
    LiveData<List<CatSum>> getSaveCatSum();*/

    class AccSum{
        private final Integer acc;
        private final Integer count;
        private final Float total;

        public AccSum(Integer acc, Integer count, Float total){
            this.acc = acc;
            this.total = total;
            this.count = count;
        }

        public Integer getAcc() {
            return acc;
        }

        public Integer getCount() {
            return count;
        }

        public Float getTotal() {
            return total;
        }
    }

    class CatSum{
        private final String Category;
        private final Integer acc;
        private final Float total;

        public CatSum(String Category,Integer acc, Float total){
            this.Category = Category;
            this.acc = acc;
            this.total = total;
        }

        public String getName() {
            return Category;
        }

        public Integer getAcc() {
            return acc;
        }

        public Float getAmount() {
            return total;
        }
    }

    //Get all the transactions
    @Query("SELECT * FROM transactions")
    LiveData<List<TransactionModel>> getAllTransaction();

    // Truncating the table - reset function
    @Query("DELETE from transactions")
    void resetTable();
}
