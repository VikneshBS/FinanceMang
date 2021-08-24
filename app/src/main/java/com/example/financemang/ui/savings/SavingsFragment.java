package com.example.financemang.ui.savings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemang.Adapter.SavingsSum;
import com.example.financemang.Adapter.SavingsSumListAdapter;
import com.example.financemang.FinanceMang;
import com.example.financemang.R;
import com.example.financemang.model.dao.TransactionDao;
import com.example.financemang.model.database.TransactionDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavingsFragment extends Fragment {

    private FinanceMang financeMang;
    private final List<SavingsSum> savingsSumList  = new ArrayList<>();
    private float savings_balance = 0;
    private final DecimalFormat df = new DecimalFormat("#,##,##,##0.00");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_savings, container, false);
        SavingsViewModel savingsViewModel = new ViewModelProvider(this).get(SavingsViewModel.class);
        financeMang = (FinanceMang)requireActivity().getApplication();
        financeMang.setPref(requireContext());
        //converting HashMap to List<SavingsSum>

        ScrollView scrollView = root.findViewById(R.id.scrollView2);
        RecyclerView recyclerView = root.findViewById(R.id.rv_cat_summary);
        final TextView clickView = root.findViewById(R.id.clickView2);
        TextView overallBalance_tv = root.findViewById(R.id.OAB_amount);
        TextView savings_tv = root.findViewById(R.id.savingsAmount);
        TextView tv_noCats = root.findViewById(R.id.tv_noCats);
        final TextView tv_total_credit = root.findViewById(R.id.tv_total_credit);
        final TextView tv_total_debit = root.findViewById(R.id.tv_total_debit);
        final TextView tv_count_debit = root.findViewById(R.id.tv_count_debit);
        final TextView tv_count_credit = root.findViewById(R.id.tv_count_credit);

        clickView.setOnClickListener(v -> {
            scrollView.setVisibility(View.VISIBLE);
            clickView.setVisibility(View.INVISIBLE);
        });

        HashMap<String, Float> savingsMap = financeMang.getSavings_Map();
        float current_balance = financeMang.getBalance();
        if(savingsMap!=null) {
            for (Map.Entry<String, Float> entry : savingsMap.entrySet()) {
                SavingsSum savingsSum = new SavingsSum(entry.getKey(), entry.getValue());
                savings_balance += entry.getValue();
                savingsSumList.add(savingsSum);
            }
        }

        TransactionDatabase.getInstance(requireActivity()).transactionDao().getSaveSummaryByAcc().observe(getViewLifecycleOwner(), AccSumList->{
            for (TransactionDao.AccSum as:AccSumList) {
                if(as.getAcc()==1){
                    tv_count_debit.setText(String.format("%s",as.getCount()));
                    tv_total_debit.setText(String.format("- %s%s", financeMang.getCurrency_sym(), df.format(as.getTotal())));
                }
                else {
                    tv_count_credit.setText(String.format("%s",as.getCount()));
                    tv_total_credit.setText(String.format("+ %s%s", financeMang.getCurrency_sym(), df.format(as.getTotal())));
                }
            }
        });

        tv_noCats.setVisibility(savingsSumList.isEmpty()?View.VISIBLE:View.INVISIBLE);
        //setting values in the TextView
        overallBalance_tv.setText(String.format("%s%s", financeMang.getCurrency_sym(), df.format(savings_balance+ current_balance)));
        savings_tv.setText(String.format("%s%s", financeMang.getCurrency_sym(), df.format(savings_balance)));

        //recycleview config
        SavingsSumListAdapter savingsSumListAdapter = new SavingsSumListAdapter(savingsSumList, financeMang.getCurrency_sym());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if(!savingsSumList.isEmpty()) {recyclerView.setAdapter(savingsSumListAdapter);}

        return root;
    }
}