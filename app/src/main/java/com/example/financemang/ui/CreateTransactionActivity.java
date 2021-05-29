package com.example.financemang.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.financemang.R;
import com.example.financemang.utils.Constants;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateTransactionActivity extends AppCompatActivity {

    private TextInputEditText et_trans_desc, et_trans_category, et_amount, et_trans_date;
    private SwitchMaterial switchAccBtn;
    private static Date s_date;
    private Button clear_btn;
    private String trans_date;
    private MenuItem saveMenuItem;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);

        et_trans_desc = findViewById(R.id.et_trans_desc);
        et_trans_category = findViewById(R.id.et_trans_category);
        et_amount = findViewById(R.id.et_amount);
        et_trans_date = findViewById(R.id.et_trans_date);
        switchAccBtn = findViewById(R.id.switch_acc_btn);
        clear_btn = findViewById(R.id.clear_btn);

        //set it as current date.
        String date_n = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(new Date());
        et_trans_date.setText(date_n);
        //to disable editing in date section
        et_trans_date.setKeyListener(null);
        et_trans_date.setOnClickListener(v -> getDate());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        setTitle("Add Transaction");
        //set on setOnKeyListener for TextInputEditText
        et_trans_desc.addTextChangedListener(saveTextWatcher);
        et_trans_category.addTextChangedListener(saveTextWatcher);
        et_amount.addTextChangedListener(saveTextWatcher);
        //clear button func
        clear_btn.setOnClickListener(v -> {
            et_amount.setText("");
            et_amount.clearFocus();
            et_trans_desc.setText("");
            et_trans_desc.clearFocus();
            et_trans_category.setText("");
            et_trans_category.clearFocus();
            et_trans_date.setText(date_n);
            et_trans_date.clearFocus();
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        saveMenuItem = menu.findItem(R.id.save_trans);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.save_trans:
                saveTrans();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //method to save transaction
    private void saveTrans(){
            String trans_desc = et_trans_desc.getText().toString();
            String trans_category = et_trans_category.getText().toString();
            String trans_date = et_trans_date.getText().toString();
            int acc = switchAccBtn.isChecked() ? 1 : 0;
            float amount = Float.parseFloat(et_amount.getText().toString());
            Intent data = new Intent();
            data.putExtra(Constants.EXTRA_DESC, trans_desc);
            data.putExtra(Constants.EXTRA_CATEGORY, trans_category);
            data.putExtra(Constants.EXTRA_ACC, acc);
            data.putExtra(Constants.EXTRA_AMOUNT, amount);
            data.putExtra(Constants.EXTRA_DATE, trans_date);
            setResult(RESULT_OK, data);
            finish();
    }
    //method to get date
    private void getDate(){
        calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            //create a date string
            String myFormat = "dd MMM, yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
            try{
                s_date = sdf.parse(sdf.format(calendar.getTime()));
                trans_date = sdf.format(calendar.getTime());
                et_trans_date.setText(trans_date);
            }
            catch (ParseException e){
                e.printStackTrace();
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTransactionActivity.this,date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private TextWatcher saveTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String trans_desc = et_trans_desc.getText().toString().trim();
            String trans_category = et_trans_category.getText().toString().trim();
            String trans_amount = et_amount.getText().toString().trim();

            saveMenuItem.setEnabled(!trans_desc.isEmpty() && !trans_amount.isEmpty() && !trans_category.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}