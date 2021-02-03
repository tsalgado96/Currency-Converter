package com.tjsalgado.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class MainActivity extends AppCompatActivity {
    private RequestQueue mQueue;
    private final ArrayList<String> currencyArray = new ArrayList<>();
    private final DecimalFormat decimalFormatter = new DecimalFormat(".##");
    private double exchangeRate = 0;

    // Top input variables
    private Spinner topSpinner;
    private EditText userInputTextTop;
    private String topCurrency;
    private double topValue;

    // Bottom input variables
    private Spinner bottomSpinner;
    private EditText userInputTextBottom;
    private String bottomCurrency;
    private double bottomValue;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        FillCurrencyArray.fillCurrencyArray(currencyArray);
        mQueue = Volley.newRequestQueue(this);

        // Initialize the EditText object
        userInputTextTop = findViewById(R.id.topInputText);
        topValue = Double.parseDouble(userInputTextTop.getText().toString());

        userInputTextBottom = findViewById(R.id.bottomInputText);
        bottomValue = Double.parseDouble(userInputTextBottom.getText().toString());

        // Initialize the Spinner values
        setSpinnerContent();
        setSpinnerListener();
        topCurrency = topSpinner.getItemAtPosition(0).toString();
        bottomCurrency = bottomSpinner.getItemAtPosition(0).toString();

        setTextChangeListener();
        setExchangeRate();
    }

    /*
    * setExchangeRate() makes a GET request to the api with the topCurrency as the base currency
    * Assigns the corresponding exchange rate value to exchangeRate
    */
    private void setExchangeRate(){
        String url = "https://api.exchangeratesapi.io/latest?base=" + topCurrency;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject rates = response.getJSONObject("rates");
                    exchangeRate = rates.getDouble(bottomCurrency);
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

    /*
    * setSpinnerContent() takes the items from currencyArray and initializes them to the Spinners
    */
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

    /*
    * setSpinnerListener() sets a listener to both Spinners and
    * calls other methods when a Spinner item is selected
    */
    private void setSpinnerListener(){
        topSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                topCurrency = parent.getItemAtPosition(position).toString();
                setExchangeRate();
                calculateExchange("top-to-bottom");
                setText("bottom");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bottomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bottomCurrency = parent.getItemAtPosition(position).toString();
                setExchangeRate();
                calculateExchange("top-to-bottom");
                setText("bottom");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*
    * setTextChangeListener() sets a listener on the text boxes and
    * calls other methods when the text is changed
    */
    private void setTextChangeListener() {
        userInputTextTop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(""))
                    topValue = 0.0;
                else
                    topValue = Double.parseDouble(s.toString());
                calculateExchange("top-to-bottom");
                setText("bottom");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        userInputTextBottom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(""))
                    bottomValue = 0.0;
                else
                    bottomValue = Double.parseDouble(s.toString());
                calculateExchange("bottom-to-top");
                //setText("top");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /*
    * setText() changes the text box values
    */
    private void setText(String setTextBox){
        if (setTextBox.equals("bottom")){
            userInputTextBottom.setText(decimalFormatter.format(bottomValue));
        }
        else if (setTextBox.equals("top")){
            userInputTextTop.setText(decimalFormatter.format(topValue));
        }
    }

    /*
    * calculateExchange() calculates the value of the inputted amount
    * after the exchange
    */
    private void calculateExchange(String direction){
        if (direction.equals("top-to-bottom")){
            bottomValue = exchangeRate * topValue;
        }
        else if (direction.equals("bottom-to-top")){
            topValue = bottomValue / exchangeRate;
        }
    }
}
