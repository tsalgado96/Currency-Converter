package com.tjsalgado.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

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
    private final ArrayList<String> currencyArray = new ArrayList<>();
    private final DecimalFormat decimalFormatter = new DecimalFormat("#,###.##");
    private double exchangeRate = 0;
    private ImageButton swapButton;
    private String dateUpdated;
    private TextView dateUpdatedTextView;
    private RequestQueue mQueue;

    // Top input variables
    private Spinner topSpinner;
    private TextWatcher topTextWatcher;
    private EditText topEditText;
    private String topCurrency;
    private double topValue;

    // Bottom input variables
    private Spinner bottomSpinner;
    private TextView bottomTextView;
    private String bottomCurrency;
    private double bottomValue;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        FillCurrencyArray.fillCurrencyArray(currencyArray);
        mQueue = Volley.newRequestQueue(this);

        // Initialize the EditText object
        topEditText = findViewById(R.id.topInputText);
        topValue = Double.parseDouble(topEditText.getText().toString());

        // Initialize the TextView object
        bottomTextView = findViewById(R.id.resultTextView);

        // Initialize the Button
        swapButton = findViewById(R.id.swapButton);
        swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapCurrency();
            }
        });

        // Initialize the Spinner values
        setSpinnerContent();
        setSpinnerListener();
        topCurrency = topSpinner.getItemAtPosition(0).toString();
        bottomCurrency = bottomSpinner.getItemAtPosition(0).toString();

        setTextChangeListener();
        topEditText.addTextChangedListener(topTextWatcher);

        setExchangeRate();
        dateUpdatedTextView = findViewById(R.id.dateUpdated);
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
                    dateUpdated = response.getString("date");
                    dateUpdatedTextView.setText("Current as of " + dateUpdated);
                    Log.e("DATE", dateUpdated);
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
                bottomCurrency = parent.getItemAtPosition(position).toString();
                setExchangeRate();
                calculateExchange();
                setText();
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
        topTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(""))
                    topValue = 0.0;
                else
                    topValue = Double.parseDouble(s.toString());
                calculateExchange();
                setText();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    /*
    * setText() changes the text box values
    */
    private void setText(){
        bottomTextView.setText(decimalFormatter.format(bottomValue));
    }

    /*
    * calculateExchange() calculates the value of the inputted amount
    * after the exchange
    */
    private void calculateExchange(){
        bottomValue = exchangeRate * topValue;
    }

    private void swapCurrency(){

    }
}
