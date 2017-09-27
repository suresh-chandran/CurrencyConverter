package com.suresh.currencyconverter;

import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private EditText amountToConvert;
    private Spinner currencyFrom;
    private Spinner currencyTo;
    private TextView result;
    private TextView balance;
    private FrameLayout progress;

    private Double totalBalance = 1000.0;
    private int conversionCount = 0;
    private double commision = 0.7;

    private String[] currencies;
    private HashMap<String, Double> currencyBalance = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        init();
    }

    private void init() {
        currencies = getResources().getStringArray(R.array.currencies);
        for (String currency : currencies) {
            currencyBalance.put(currency, 0.0);
        }
        currencyBalance.put(currencies[0], totalBalance);
        balance.setText("Available balance: "+currencyBalance.get(currencies[0])+" "+currencies[0]);
    }

    private void findViews() {
        amountToConvert = (EditText) findViewById(R.id.amountToConvert);
        currencyTo = (Spinner) findViewById(R.id.currencyTo);
        currencyFrom = (Spinner) findViewById(R.id.currencyFrom);
        result = (TextView) findViewById(R.id.result);
        progress = (FrameLayout) findViewById(R.id.progress);
        balance = (TextView) findViewById(R.id.balance);
        progress.setOnClickListener(null);
        currencyFrom.setEnabled(false);

    }

    public void convert(View view){
        final String amount = amountToConvert.getText().toString();
        final String fromCurrency = currencyFrom.getSelectedItem().toString();
        final String toCurrency = currencyTo.getSelectedItem().toString();
        if(amount.length() > 0){
            Double convertableAmount = Double.parseDouble(amount);
            if(currencyBalance.get(fromCurrency) > convertableAmount) {
                progress.setVisibility(View.VISIBLE);
                Api.instance().convert(amount, fromCurrency, toCurrency).enqueue(new Callback<ResponseCurrency>() {
                    @Override
                    public void onResponse(Call<ResponseCurrency> call, Response<ResponseCurrency> response) {
                        progress.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            ResponseCurrency responseCurrency = response.body();
                            String resultText = String.format("Amount %s %s is converted to %s %s.", amount, fromCurrency, responseCurrency.getAmount(), toCurrency);
                            Double percentage = 0.0;
                            conversionCount++;
                            if (conversionCount > 5) {
                                percentage = commision;
                            }
                            Double commissionFee = ((percentage/100) * Double.parseDouble(amount));
                            Double currentBalance = currencyBalance.get(fromCurrency);
                            currentBalance = currentBalance - (Double.parseDouble(amount) + commissionFee);
                            currencyBalance.put(fromCurrency, currentBalance);
                            Double currentOtherCurrencyBalnce = currencyBalance.get(toCurrency);
                            currentOtherCurrencyBalnce = currentOtherCurrencyBalnce + Double.parseDouble(responseCurrency.getAmount());
                            currencyBalance.put(toCurrency, currentOtherCurrencyBalnce);
                            resultText = resultText + "\nCommision fee: "+commissionFee+" "+fromCurrency+" ("+percentage+"% commission fee)";
                            resultText = resultText + "\nAvailable balance:";
                            for (String currency :
                                    currencies) {
                                resultText = resultText + "\n" + currency + ": " + currencyBalance.get(currency);
                            }
                            result.setText(resultText);
                            balance.setText("Available balance: " + currencyBalance.get(currencies[0]) + " " + currencies[0]);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseCurrency> call, Throwable t) {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Unable to convert. Try again.", Toast.LENGTH_LONG).show();
                    }
                });
            }else {
                Toast.makeText(getApplicationContext(), "You have insufficient balance to convert.", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Enter amount to convert.", Toast.LENGTH_LONG).show();
        }
    }
}
