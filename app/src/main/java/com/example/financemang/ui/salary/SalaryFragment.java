package com.example.financemang.ui.salary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.financemang.R;

public class SalaryFragment extends Fragment {

    private SalaryViewModel salaryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        salaryViewModel =
                new ViewModelProvider(this).get(SalaryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_salary, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        salaryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}