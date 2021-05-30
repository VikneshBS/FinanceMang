package com.example.financemang.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemang.R;
import com.example.financemang.model.entity.TransactionModel;

import java.text.DecimalFormat;
import java.util.List;


public class TransListAdapter extends RecyclerView.Adapter<TransListAdapter.TransListHolder> {
    private final List<TransactionModel> trans_list;
    private final String currency_sym;
    private final DecimalFormat df = new DecimalFormat("#,##,##,##0.00");

    public static class TransListHolder extends RecyclerView.ViewHolder {
        private final TextView desc;
        private final TextView category;
        private final TextView date;
        private final TextView amount;
        private final TextView balance;
        public TransListHolder(@NonNull View itemView) {
            super(itemView);
            desc = itemView.findViewById(R.id.tv_item_desc);
            category = itemView.findViewById(R.id.tv_item_category);
            amount = itemView.findViewById(R.id.tv_item_amount);
            balance = itemView.findViewById(R.id.tv_item_bal);
            date = itemView.findViewById(R.id.tv_item_date);
        }
    }

    public TransListAdapter(List<TransactionModel> trans_list, String currency_sym){
        this.trans_list = trans_list;
        this.currency_sym = currency_sym;
    }

    @NonNull
    @Override
    public TransListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trans_list,parent,false);
        return new TransListHolder(view);
    }

    @Override
    public void onBindViewHolder(TransListAdapter.TransListHolder holder, int position) {
        TransactionModel currentTrans = trans_list.get(position);
        holder.desc.setText(String.format("Desc: %s", currentTrans.getDescription()));
        holder.category.setText(String.format("Category: %s", currentTrans.getCategory()));
        holder.amount.setText(String.format("%s%s %s", currency_sym, df.format(currentTrans.getAmount()), currentTrans.getAcc() == 0 ? "Dr" : "Cr"));
        if (currentTrans.getAcc() == 0) holder.amount.setTextColor(Color.RED);
        holder.date.setText(currentTrans.getDate());
        holder.balance.setText(String.format("%s: %s%s", "Bal", currency_sym, df.format(currentTrans.getBalance())));
    }

    @Override
    public int getItemCount() {
        return trans_list.size();
    }

    public void removeItem(int position) {
        trans_list.remove(position);
        notifyItemRemoved(position);
    }

    public void notifyNoChange() {
         notifyDataSetChanged();
    }

    public List<TransactionModel> getData() {
        return trans_list;
    }
}
