package com.example.financemang.ui.dashboard;

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

import com.example.financemang.Adapter.CatSumListAdapter;
import com.example.financemang.FinanceMang;
import com.example.financemang.R;
import com.example.financemang.model.dao.TransactionDao;
import com.example.financemang.model.database.TransactionDatabase;

import java.text.DecimalFormat;

public class DashboardFragment extends Fragment {

    DashboardViewModel dashboardViewModel;
    private final DecimalFormat df = new DecimalFormat("#,##,##,##0.00");
    private CatSumListAdapter catSumListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        FinanceMang financeMang = (FinanceMang) requireActivity().getApplication();
        financeMang.setPref(requireContext());

        ScrollView scrollView = root.findViewById(R.id.scrollView);
        RecyclerView recyclerView = root.findViewById(R.id.rv_cat_summary);
        final TextView clickView = root.findViewById(R.id.clickView);
        final TextView balance_price = root.findViewById(R.id.tvBalPrice);
        final TextView tv_total_credit = root.findViewById(R.id.tv_total_credit);
        final TextView tv_total_debit = root.findViewById(R.id.tv_total_debit);
        final TextView tv_count_debit = root.findViewById(R.id.tv_count_debit);
        final TextView tv_count_credit = root.findViewById(R.id.tv_count_credit);
        final TextView tv_noCats = root.findViewById(R.id.tv_noCats);

        clickView.setOnClickListener(v -> {
            scrollView.setVisibility(View.VISIBLE);
            clickView.setVisibility(View.INVISIBLE);
        });

        TransactionDatabase.getInstance(requireActivity()).transactionDao().getCatSum().observe(getViewLifecycleOwner(), catSums -> {
            catSumListAdapter = new CatSumListAdapter(catSums,financeMang.getCurrency_sym());
            tv_noCats.setVisibility(catSums.isEmpty()?View.VISIBLE:View.INVISIBLE);

            //configuring recycleview
            recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext(),LinearLayoutManager.VERTICAL,false));
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            if(!catSums.isEmpty()) { recyclerView.setAdapter(catSumListAdapter);}
        });

        TransactionDatabase.getInstance(requireActivity()).transactionDao().getSummaryByAcc().observe(getViewLifecycleOwner(), AccSumList->{
            for (TransactionDao.AccSum as:AccSumList) {
                if(as.getAcc()==0){
                    tv_count_debit.setText(String.format("%s",as.getCount()));
                    tv_total_debit.setText(String.format("- %s%s", financeMang.getCurrency_sym(), df.format(as.getTotal())));
                }
                else {
                    tv_count_credit.setText(String.format("%s",as.getCount()));
                    tv_total_credit.setText(String.format("+ %s%s", financeMang.getCurrency_sym(), df.format(as.getTotal())));
                }
            }
        });
        balance_price.setText(String.format("%s%s", financeMang.getCurrency_sym(), df.format(financeMang.getBalance())));

        return root;
    }
}