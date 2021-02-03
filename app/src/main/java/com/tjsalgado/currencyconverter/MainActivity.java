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
import java.util.ArrayList;
import java.util.Collections;

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

        // Initialize the EditText object
        userInputTextTop = findViewById(R.id.topInputText);
        userInputTextBottom = findViewById(R.id.bottomInputText);
        userInputDouble = Double.parseDouble(userInputTextTop.getText().toString());
        mQueue = Volley.newRequestQueue(this);

        FillCurrencyArray.fillCurrencyArray(currencyArray);
        // Initialize the Spinner values
        setSpinnerContent();
        setSpinnerListener();
        baseCurrency = topSpinner.getItemAtPosition(0).toString();
        endCurrency = bottomSpinner.getItemAtPosition(0).toString();

        setTextChangeListener();
        setExchangeRate();

    }

    private void setExchangeRate(){
        String url = "https://api.exchangeratesapi.io/latest?base=" + baseCurrency;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject rates = response.getJSONObject("rates");
                    exchangeRate = rates.getDouble(endCurrency);
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

    private void calculateExchange(){
        endAmount = exchangeRate * userInputDouble;
    }
}
