package com.example.financemang.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemang.Adapter.TransListAdapter;
import com.example.financemang.FinanceMang;
import com.example.financemang.R;
import com.example.financemang.model.entity.TransactionModel;
import com.example.financemang.ui.CreateTransactionActivity;
import com.example.financemang.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.app.Activity.RESULT_OK;

public class TransactionFragment extends Fragment implements View.OnClickListener {

    private TransactionViewModel transactionViewModel;
    private RecyclerView recyclerView;
    private TransListAdapter transListAdapter;
    private MaterialButton show_toggle_btn;
    private FrameLayout frameLayout;
    private TextView tv_noTans;
    private float balance;
    private FinanceMang financeMang;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_transaction, container, false);
        financeMang = (FinanceMang)getActivity().getApplication();
        balance = financeMang.getBalance();
        //toggle button
        frameLayout = root.findViewById(R.id.frame_l);
        tv_noTans = root.findViewById(R.id.tv_noTrans);
        show_toggle_btn = root.findViewById(R.id.show_toggle_btn);
        show_toggle_btn.setOnClickListener(v -> {
            if(frameLayout.getVisibility()==View.INVISIBLE){
                show_toggle_btn.setText(R.string.hide_the_transaction);
                show_toggle_btn.setIconResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                frameLayout.setVisibility(View.VISIBLE);
            }
            else{
                show_toggle_btn.setText(R.string.show_the_transaction);
                show_toggle_btn.setIconResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                frameLayout.setVisibility(View.INVISIBLE);
            }
        });
        //floating add button
        FloatingActionButton floatingActionButton = root.findViewById(R.id.fab_add_trans);
        floatingActionButton.setOnClickListener(this);
        //getting access to view model class
        transactionViewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        //add observer to for view model getTransList() (LiveData)
        transactionViewModel.getTrans_list().observe(getViewLifecycleOwner(), transModels -> {
            transListAdapter = new TransListAdapter(transModels,financeMang.getCurrency_sym());
            //Check for No Transaction
            tv_noTans.setVisibility(transModels.isEmpty()?View.VISIBLE:View.INVISIBLE);
            //configuring recycler view
            recyclerView = root.findViewById(R.id.rv_trans_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext(),LinearLayoutManager.VERTICAL,false));
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(transListAdapter);
        });
        //prepareTransData();
        return root;
    }

    /*private void prepareTransData() {
        TransactionModel transactionModel = new TransactionModel("02 Mar, 2021","Hotel","Food", (float) 220.00,0,10000);
        transactionModels.add(transactionModel);

        transactionModel = new TransactionModel("03 Mar, 2021", "Shopping", "Cloths", (float) 500.00,0,9000);
        transactionModels.add(transactionModel);

        transactionModel = new TransactionModel("04 Mar, 2021", "Salary","Income", 25000.00f,1,35000);
        transactionModels.add(transactionModel);

        transactionModel = new TransactionModel("05 Mar. 2021", "Festivel Bonus","Bonus",5000.00f,1,40000);
        transactionModels.add(transactionModel);

        transListAdapter.notifyDataSetChanged();
    }*/

    @Override
    public void onClick(View v){
        Intent intent = new Intent(getActivity(), CreateTransactionActivity.class);
        startActivityForResult(intent, Constants.ADD_TODO);
    }

    //override method to show result after inserting list
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        //Check if request code is for inserting new list then perform insertion
        if(requestCode==Constants.ADD_TODO && resultCode==RESULT_OK){
            String desc = data.getStringExtra(Constants.EXTRA_DESC);
            String catogory = data.getStringExtra(Constants.EXTRA_CATEGORY);
            String date = data.getStringExtra(Constants.EXTRA_DATE);
            float amount = data.getFloatExtra(Constants.EXTRA_AMOUNT,0);
            int acc = data.getIntExtra(Constants.EXTRA_ACC,5);
            balance = acc==0 ? (balance-amount): (balance+amount);
            financeMang.setBalance(balance);
            TransactionModel transactionModel = new TransactionModel(date,desc,catogory,amount,acc,balance);
            transactionViewModel.insert(transactionModel);
            Toast.makeText(getContext(),"Trans Inserted Successfully",Toast.LENGTH_SHORT).show();
        }
    }
}