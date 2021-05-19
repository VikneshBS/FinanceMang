package com.example.financemang.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemang.R;
import com.example.financemang.model.entity.TransactionModel;

import java.util.List;


public class TransListAdapter extends RecyclerView.Adapter<TransListAdapter.TransListHolder> {
    private List<TransactionModel> translist;

    public class TransListHolder extends RecyclerView.ViewHolder {
        private TextView desc;
        private TextView category;
        private TextView date;
        private TextView amount;
        private TextView balance;
        public TransListHolder(@NonNull View itemView) {
            super(itemView);
            desc = itemView.findViewById(R.id.tv_item_desc);
            category = itemView.findViewById(R.id.tv_item_category);
            amount = itemView.findViewById(R.id.tv_item_amount);
            balance = itemView.findViewById(R.id.tv_item_bal);
            date = itemView.findViewById(R.id.tv_item_date);
        }
    }

    public TransListAdapter(List<TransactionModel> translist){
        this.translist = translist;
    }
    @NonNull
    @Override
    public TransListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trans_list,parent,false);
        return new TransListHolder(view);
    }

    @Override
    public void onBindViewHolder(TransListAdapter.TransListHolder holder, int position) {
        TransactionModel currentTrans = translist.get(position);
        holder.desc.setText(currentTrans.getDescription());
        holder.category.setText(currentTrans.getCategory());
        holder.amount.setText(String.format(Float.toString(currentTrans.getAmount())));
        holder.date.setText(currentTrans.getDate());
        holder.balance.setText(String.format(Float.toString(currentTrans.getBalance())));
    }

    @Override
    public int getItemCount() {
        return translist.size();
    }
}
