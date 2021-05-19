package com.example.financemang.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//@Entity annotation for giving table name and declaring it
@Entity(tableName = "transactions")
public class TransactionModel {

    //@Primary Key to set trans_id as primary key
    // and making autoincrement for each new list
    @PrimaryKey(autoGenerate = true)
    private int trans_Id;

    //@ColumnInfo for giving column name trans_Desc for entity desc
    // and this name will be used in all database queries
    @ColumnInfo(name = "trans_Date")
    private String date;

    @ColumnInfo(name = "trans_Desc")
    private String description;

    @ColumnInfo(name = "Category")
    private String category;

    @ColumnInfo(name = "amount")
    private float amount;

    @ColumnInfo(name = "acc")
    private int acc;

    @ColumnInfo(name = "balance")
    private float balance;

    public TransactionModel(String date, String description, String category, float amount, int acc, float balance) {
        this.date = date;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.acc = acc;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "TransactionModel{" +
                "trans_Id=" + trans_Id +
                ", date='" + date + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", acc=" + acc +
                ", balance=" + balance +
                '}';
    }

    public int getTrans_Id() {
        return trans_Id;
    }

    public void setTrans_Id(int trans_Id) {
        this.trans_Id = trans_Id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAcc() {
        return acc;
    }

    public void setAcc(int acc) {
        this.acc = acc;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
