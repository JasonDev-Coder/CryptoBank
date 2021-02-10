package com.example.ewallet;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class CryptoInfo extends AppCompatActivity {
    private LineChart myChart;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private ProgressDialog progressDialog;
    TextView max_market_value;
    TextView circ_sup_view;
    TextView market_cap_val;
    TextView About_View;
    TextView About_Info;
    TextView crypto_name;
    ImageView crypto_logo;
    Bundle extras;
    CurrencyType currencyType;
    public ImageView backimg;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitcoin);
        extras=this.getIntent().getExtras();
        //get the currency on which we clicked
        currencyType=HomeFragment.SupportedCurrencies.get(extras.getInt("index_crypto"));
        //get the widget objects from xml file
        backimg = (ImageView) findViewById(R.id.backimg);
        max_market_value = findViewById(R.id.max_sup_val);
        circ_sup_view = findViewById(R.id.circ_sup_val);
        market_cap_val = findViewById(R.id.market_cap_val);
        About_View = findViewById(R.id.aboutview);
        About_Info = findViewById(R.id.desc_crypto);
        crypto_name=findViewById(R.id.cryptotxt);
        crypto_logo=findViewById(R.id.cryptoLogo);
        //create a process dialog to use when making a request
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("BPI Loading");
        progressDialog.setMessage("Please wait");
        load_crypto_about();
        load_data();
        load_market_stats();
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CryptoInfo.this.backHome();
            }
        });

    }

    private void backHome() {
        finish();
    }

    private void load_crypto_about() {
        //put the name,logo and description of the chosen crypto
        About_View.setText("About "+currencyType.getCurrencyName());
        crypto_name.setText(currencyType.getCurrencyName());
        crypto_logo.setImageBitmap(currencyType.getImage());
        About_Info.setText(currencyType.getDescription());
    }

    public void load_data() {
        //make a request to get the data for the chosen crypto to load it in the graph
        Request request = new Request.Builder().url(CONSTANTS.getHistUrlFor(currencyType.getCurrencySymbol())).build();
        progressDialog.show();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(CryptoInfo.this, "Error during BPI loading: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseHistResponse(body);
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void parseHistResponse(String body) {
        int x = 1;
        ArrayList<Entry> yValues = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(body);//get the json response
            JSONArray hist_data = jsonObject.getJSONArray("result");
            for(int i=0;i<hist_data.length();i++){
                JSONArray values=hist_data.getJSONArray(i);
                double yvalue = values.getDouble(2);//get the price from the json array
                yValues.add(new Entry(x++, (float) yvalue));//add a new entry for the yValues
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (Exception e) {

        }
        load_chart(yValues);
    }
    private void load_chart(ArrayList<Entry> yValues) {
        String cryp_name;
        cryp_name=currencyType.getCurrencyName();
        myChart = findViewById(R.id.linechart);
        myChart.getDescription().setEnabled(false);
        myChart.getAxisLeft().setTextColor(Color.rgb(255, 189, 0));//color the axis
        myChart.getAxisRight().setTextColor(Color.rgb(255, 189, 0));
        myChart.getXAxis().setTextColor(Color.rgb(255, 189, 0));
        myChart.setBackgroundColor(Color.rgb(44, 44, 83));
        myChart.getLegend().setTextColor(Color.WHITE);
        myChart.setDragEnabled(false);//cant drag or scale on the graph
        myChart.setScaleEnabled(false);
        LineDataSet set1 = new LineDataSet(yValues, cryp_name+" variation");
        set1.setColor(Color.rgb(255, 189, 0));
        set1.setCircleRadius(2);
        set1.setCircleColor(Color.rgb(44, 44, 83));
        set1.setLineWidth(2);
        set1.setFillAlpha(110);
        set1.setValueTextColor(Color.WHITE);
        set1.setDrawValues(false);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);
        myChart.setData(data);
        myChart.invalidate();
    }

    public void load_market_stats() {
        //load data about the crypto such as market cap...
        Request request = new Request.Builder().url(CONSTANTS.MARKET_UPDATES_URL2).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(CryptoInfo.this, "Error during BPI loading: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseMarketResponse(body);
                    }
                });
            }
        });

    }

    private void parseMarketResponse(String body) {
        String curr_name=currencyType.getCurrencyName();
        try {
            JSONObject jsonObject = new JSONObject(body);
            JSONArray bpis = jsonObject.getJSONArray("data");//we get the array which contains the data we need labeled by 'data'
            JSONObject crypto_info;
            for(int i=0;i<bpis.length();i++) {//loop inside this array to find the data of crypto we need
                if (bpis.getJSONObject(i).getString("symbol").equals(currencyType.getCurrencySymbol())){//when we find our crypto
                    //get the required info to display from the json object
                    crypto_info=bpis.getJSONObject(i);
                    double circ_cup_int = crypto_info.getDouble("circulating_supply");
                    double crypto_price = crypto_info.getJSONObject("quote").getJSONObject("USD").getDouble("price");
                    Double market_cap_double = circ_cup_int * crypto_price;
                    String max_market_val = crypto_info.getString("max_supply");
                    if (max_market_val.equals("null"))
                        max_market_value.setText("No Cap");
                    else
                        max_market_value.setText(truncateNumber((float) crypto_info.getDouble("max_supply")) + " " + curr_name);
                    circ_sup_view.setText(truncateNumber((float) circ_cup_int) + " " + curr_name);
                    NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));//format the market cap to display it in a currency formatted way
                    market_cap_val.setText("US" + defaultFormat.format(market_cap_double));
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            Log.d("CIRCSUPP", e.toString());
        }
    }

    public String truncateNumber(float floatNumber) {//a function to turn any big number into a string containing symbols M or B or K for million billion thousand
        long million = 1000000L;
        long billion = 1000000000L;
        long trillion = 1000000000000L;
        long number = Math.round(floatNumber);
        if ((number >= million) && (number < billion)) {
            float fraction = calculateFraction(number, million);
            return Float.toString(fraction) + "M";
        } else if ((number >= billion) && (number < trillion)) {
            float fraction = calculateFraction(number, billion);
            return Float.toString(fraction) + "B";
        }
        return Long.toString(number);
    }

    public float calculateFraction(long number, long divisor) {
        long truncate = (number * 10L + (divisor / 2L)) / divisor;
        float fraction = (float) truncate * 0.10F;
        return fraction;

    }
}