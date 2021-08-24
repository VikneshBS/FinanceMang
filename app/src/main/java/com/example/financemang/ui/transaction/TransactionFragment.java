package com.example.financemang.ui.transaction;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemang.Adapter.SavingsSum;
import com.example.financemang.Adapter.TransListAdapter;
import com.example.financemang.FinanceMang;
import com.example.financemang.R;
import com.example.financemang.SwipeToDeleteCallback;
import com.example.financemang.model.database.TransactionDatabase;
import com.example.financemang.model.entity.TransactionModel;
import com.example.financemang.ui.CreateTransactionActivity;
import com.example.financemang.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.opencsv.CSVWriter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

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
    File path;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_transaction, container, false);
        financeMang = (FinanceMang) requireActivity().getApplication();
        //path = getContext().getExternalFilesDir(null);
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        frameLayout = root.findViewById(R.id.frame_l);
        tv_noTans = root.findViewById(R.id.tv_noTrans);
        show_toggle_btn = root.findViewById(R.id.show_toggle_btn);

        //toggle button
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
            enableSwipeToDeleteAndUndo();
        });

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater){
        MenuInflater menuInflater = requireActivity().getMenuInflater();
        menuInflater.inflate(R.menu.settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.export_icon:
                new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme_Center)
                        .setTitle("Export Options")
                        .setIcon(R.drawable.export_icon)
                        .setMessage("Please select any Export option")
                        .setPositiveButton("Export Only",(dialog, which)->{
                            exportDB();
                            dialog.dismiss();
                        })
                        .setNegativeButton("Export & Reset",(dialog,which)-> new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme_Center)
                            .setTitle("Confirm Export & Clear All data")
                                .setMessage("This operation will export all the transactions, then remove all the transaction and reset Balance, Savings Balance")
                            .setIcon(R.drawable.export_icon)
                            .setPositiveButton("Confirm",(dialog1, which1) -> {
                                exportDB();
                                clear_all();
                                dialog1.dismiss();
                                dialog.dismiss();})
                            .setNegativeButton("Cancel",((dialog1, which1) -> dialog1.dismiss()))
                            .show())
                        .show();
                break;
            case R.id.clear_all:
                //Toast.makeText(requireContext(),"Clear All is clicked",Toast.LENGTH_SHORT);
                new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme_Center)
                        .setTitle("Confirm Clear All data")
                        .setMessage("This operation will remove all the transaction and reset Balance, Savings Balance")
                        .setIcon(R.drawable.outline_delete_white_24dp)
                        .setBackgroundInsetTop(Color.RED)
                        .setPositiveButton("Confirm",(dialog, which1) -> {
                            clear_all();
                            dialog.dismiss();})
                        .setNegativeButton("Cancel",((dialog, which1) -> dialog.dismiss()))
                        .show();
                break;
            case R.id.setCurrencySymbol:
                View custom_dialog = getLayoutInflater().inflate(R.layout.custom_dialog,null);
                InputFilter filter = (source, start, end, dest, dstart, dend) -> {
                    for (int i = start; i < end; ++i)
                    {
                        if (!Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZ$Դ¥₹£₺€₡]*").matcher(String.valueOf(source.charAt(i))).matches())
                        {
                            return "";
                        }
                    }
                    return null;
                };
                TextInputEditText newSymbol = custom_dialog.findViewById(R.id.newSymbol);
                newSymbol.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(3)});
                new MaterialAlertDialogBuilder(requireContext())
                        .setView(custom_dialog)
                        .setPositiveButton("Set",(dialog, which)->{
                            financeMang.setCurrency_sym(newSymbol.getText().toString());
                            dialog.dismiss();
                        })
                        .setNegativeButton("Cancel",(dialog,which)-> dialog.dismiss())
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clear_all(){
        financeMang.setBalance(0);
        financeMang.setSavings_Map(new HashMap());
        truncate();
        financeMang.setCurrency_sym("₹");
        new MaterialAlertDialogBuilder(requireContext(),R.style.AlertDialogTheme)
                .setTitle("Reset Successful")
                .setMessage("Removed all the transaction and balance, savings balance reset.")
                .show();
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final TransactionModel item = transListAdapter.getData().get(position);
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Confirmation")
                        .setMessage("Are sure you want to delete?")
                        .setIcon(R.drawable.outline_info_red_400_24dp)
                        .setPositiveButton("Delete", (dialog, which) -> {
                            balance = financeMang.getBalance();
                            balance = item.getAcc()==0 ? balance + item.getAmount() : balance - item.getAmount();
                            String catogory = item.getCategory();
                            if(catogory.contains("SAVINGS")){
                                String notifyMsg = "Transaction Failed to Delete: ";
                                HashMap SavingsMap = financeMang.getSavings_Map();
                                if(!SavingsMap.isEmpty()){
                                    float savings_value = SavingsMap.containsKey(catogory) ? (float) SavingsMap.get(catogory) : (float) 0;
                                    float newtemp = item.getAcc()==0 ?  savings_value - item.getAmount() : savings_value + item.getAmount();
                                    if(newtemp<0){
                                        SavingsMap.remove(catogory);
                                        notifyMsg += "Savings Balance is less <= amount";
                                        negativeBalAlert(notifyMsg);
                                        transListAdapter.notifyNoChange();
                                        return;
                                    }
                                    else if(newtemp==0){
                                        SavingsMap.remove(catogory);
                                        financeMang.setSavings_Map(SavingsMap);
                                    }
                                    else{
                                        SavingsMap.put(catogory, newtemp);
                                        financeMang.setSavings_Map(SavingsMap);
                                    }
                                }
                            }
                            if(balance >= 0) {
                                transListAdapter.removeItem(position);
                                transactionViewModel.delete(item);
                                financeMang.setBalance(balance);
                            }
                            else {
                                negativeBalAlert("Delete Operation Failed");
                                transListAdapter.notifyNoChange();
                            }
                            dialog.dismiss();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            transListAdapter.notifyNoChange();
                            dialog.dismiss();
                        })
                        .setOnCancelListener((dialog)->{
                            transListAdapter.notifyNoChange();
                            dialog.dismiss();
                        })
                        .show();
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

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
            balance = financeMang.getBalance();
            balance = acc==0 ? (balance-amount): (balance+amount);
            HashMap<String, Float> SavingsMap;
            SavingsMap = financeMang.getSavings_Map();
            String notifyMsg = "Transaction Failed to add: ";
            boolean savingsFlag = true;
            if(catogory.contains("SAVINGS")){
                savingsFlag = false;
                float temp = (float) 0;
                if(SavingsMap!=null) {
                    temp = SavingsMap.containsKey(catogory) ? SavingsMap.get(catogory): (float) 0;
                }
                else{
                    SavingsMap = new HashMap<>();
                }
                    float newtemp = acc==0 ? temp+amount : temp-amount;
                    if(newtemp<0){
                        SavingsMap.remove(catogory);
                        notifyMsg += "Savings Balance is less <= amount";
                        negativeBalAlert(notifyMsg);
                        return;
                    }
                    else if(newtemp==0){
                        SavingsMap.remove(catogory);
                        financeMang.setSavings_Map(SavingsMap);
                        savingsFlag = true;
                    }
                    else{
                        SavingsMap.put(catogory, newtemp);
                        financeMang.setSavings_Map(SavingsMap);
                        savingsFlag = true;
                    }
                }
            if(balance >= 0 && savingsFlag) {
                TransactionModel transactionModel = new TransactionModel(date,desc,catogory,amount,acc,balance);
                transactionViewModel.insert(transactionModel);
                financeMang.setBalance(balance);
            }else {
                negativeBalAlert(notifyMsg);
            }
        }
    }

    private void negativeBalAlert(String alertTitle) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(alertTitle)
                .setMessage("Balance should be greater than or equal to 0")
                .show();
    }

    private void truncate(){
        transactionViewModel.resetAll();
    }

    private void exportDB() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy", Locale.US);
        Date today = new Date();
        String filename = "FM_TransactionsExport-"+sdf.format(today)+".csv";
        File file = new File(path, filename);
        try
        {
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            String[] arrHead;
            float savings_balance = 0;
            csvWrite.writeNext(new String[] {"Savings Category"});
            HashMap<String, Float>  savingsMap = financeMang.getSavings_Map();
            if(savingsMap!=null) {
                for (Map.Entry<String, Float> entry : savingsMap.entrySet()) {
                    SavingsSum savingsSum = new SavingsSum(entry.getKey(), entry.getValue());
                    savings_balance += entry.getValue();
                    csvWrite.writeNext(new String[]{entry.getKey(),String.valueOf(entry.getValue())});
                }
            }
            csvWrite.writeNext(new String[]{"","",""});
            csvWrite.writeNext(new String[]{"Overall Balance",String.valueOf(financeMang.getBalance()+ savings_balance)});
            csvWrite.writeNext(new String[]{"Current Balance",String.valueOf(financeMang.getBalance())});
            csvWrite.writeNext(new String[]{"Savings Balance",String.valueOf(savings_balance)});
            csvWrite.writeNext(new String[]{"","",""});
            arrHead = new String[]{"Trans ID","Trans Date","Description","Category","Amount","Credit/Debit","Balance"};
            csvWrite.writeNext(arrHead);
            TransactionDatabase.getInstance(requireContext()).transactionDao().getAllTransaction().observe(getViewLifecycleOwner(),transactionModelList -> {
                for (TransactionModel transactionModel:transactionModelList) {
                    String[] arrStr = transactionModel.getCSVFormat();
                    csvWrite.writeNext(arrStr);
                }
                try {
                    csvWrite.close();
                    new MaterialAlertDialogBuilder(requireContext(),R.style.AlertDialogTheme)
                            .setTitle("Exported Successfully")
                            .setMessage("Click 'Open in Explorer' see in File Explorer")
                            .setPositiveButton("Open in Explorer",(dialog,which)-> startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)))
                            .show();
                    Toast.makeText(requireContext(),"Exported as "+filename,Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        catch(Exception sqlEx)
        {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }
}