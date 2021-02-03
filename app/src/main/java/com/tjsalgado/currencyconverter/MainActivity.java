package com.tjsalgado.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;

public class MainActivity extends AppCompatActivity {
    private RequestQueue mQueue;
    private final ArrayList<String> currencyArray = new ArrayList<>();
    private double userInputDouble;
    private Spinner topSpinner;
    private Spinner bottomSpinner;
    private String baseCurrency;
    private String endCurrency;
    private double endAmount;
    private double exchangeRate = 0;
    private EditText userInputTextTop;
    private EditText userInputTextBottom;
    private final DecimalFormat decimalFormatter = new DecimalFormat("#,###.##");

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        userInputTextTop = findViewById(R.id.topInputText);
        userInputDouble = Double.parseDouble(userInputTextTop.getText().toString());
        userInputTextBottom = findViewById(R.id.bottomInputText);

        fillCurrencyArray();
        setSpinnerContent();
        setSpinnerListener();
        setTextChangeListener();
        setExchangeRate();

        baseCurrency = topSpinner.getItemAtPosition(0).toString();
        endCurrency = bottomSpinner.getItemAtPosition(0).toString();
    }


    private void setExchangeRate(){
        String url = "https://api.exchangeratesapi.io/latest?base=" + baseCurrency;
        mQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject rates = response.getJSONObject("rates");
                    exchangeRate = rates.getDouble(endCurrency);
                    Log.e("EXCHANGE RATE", String.valueOf(exchangeRate));
                    //endAmount = userInputDouble * rates.getDouble(endCurrency);
                    //mTextViewResult.append(String.valueOf(endAmount));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }








    private void calculateExchange(){
        endAmount = exchangeRate * userInputDouble;
    }








    private void setSpinnerContent(){
        topSpinner = findViewById(R.id.topDropdown);
        bottomSpinner = findViewById(R.id.bottomDropdown);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, currencyArray);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        topSpinner.setAdapter(adapter);
        bottomSpinner.setAdapter(adapter);
    }








    private void setSpinnerListener(){
        topSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                baseCurrency = parent.getItemAtPosition(position).toString();
                setExchangeRate();
                calculateExchange();
                setText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bottomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endCurrency = parent.getItemAtPosition(position).toString();
                setExchangeRate();
                calculateExchange();
                setText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }








    private void setTextChangeListener() {
        userInputTextTop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(""))
                    userInputDouble = 0.0;
                else
                    userInputDouble = Double.parseDouble(s.toString());
                calculateExchange();
                setText();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }



    private void setText(){
        String str = "";
        str += decimalFormatter.format(endAmount);
        //userInputTextBottom.setText(currencyFormatter.format(endAmount));
        userInputTextBottom.setText(str);
    }




    private void fillCurrencyArray(){
        currencyArray.add("CAD");
        currencyArray.add("HKD");
        currencyArray.add("ISK");
        currencyArray.add("PHP");
        currencyArray.add("DKK");
        currencyArray.add("HUF");
        currencyArray.add("CZK");
        currencyArray.add("GBP");
        currencyArray.add("RON");
        currencyArray.add("SEK");
        currencyArray.add("IDR");
        currencyArray.add("INR");
        currencyArray.add("BRL");
        currencyArray.add("RUB");
        currencyArray.add("HRK");
        currencyArray.add("JPY");
        currencyArray.add("THB");
        currencyArray.add("CHF");
        currencyArray.add("EUR");
        currencyArray.add("MYR");
        currencyArray.add("BGN");
        currencyArray.add("TRY");
        currencyArray.add("CNY");
        currencyArray.add("NOK");
        currencyArray.add("NZD");
        currencyArray.add("ZAR");
        currencyArray.add("USD");
        currencyArray.add("MXN");
        currencyArray.add("SGD");
        currencyArray.add("AUD");
        currencyArray.add("ILS");
        currencyArray.add("KRW");
        currencyArray.add("PLN");
        Collections.sort(currencyArray);
    }
}
