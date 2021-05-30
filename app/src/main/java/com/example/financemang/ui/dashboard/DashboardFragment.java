package com.example.financemang.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.financemang.FinanceMang;
import com.example.financemang.R;

import java.text.DecimalFormat;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private final DecimalFormat df = new DecimalFormat("#,##,##,##0.00");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        final CardView cv_balance = root.findViewById(R.id.cvBalance);
        final TextView clickView = root.findViewById(R.id.clickView);
        final TextView balance_price = root.findViewById(R.id.tvBalPrice);
        FinanceMang financeMang = (FinanceMang) requireActivity().getApplication();
        financeMang.setPref(requireContext());

        clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv_balance.setVisibility(View.VISIBLE);
                clickView.setVisibility(View.INVISIBLE);
            }
        });

        balance_price.setText(String.format("%s%s", financeMang.getCurrency_sym(), df.format(financeMang.getBalance())));

        return root;
    }
}