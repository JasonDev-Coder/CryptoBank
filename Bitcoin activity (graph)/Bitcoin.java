package com.example.ewalletx2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.Loader;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

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


public class Bitcoin extends AppCompatActivity {
    static int CryptoIndex = 0;
    private LineChart myChart;
    private static final String HIST_BPI_LINK = "https://api.coindesk.com/v1/bpi/historical/close.json";
    private static final String MARKET_UPDATES_URL = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=3ea268be-397d-4d62-8127-644e8c4f84d3";
    private OkHttpClient okHttpClient = new OkHttpClient();
    private ProgressDialog progressDialog;
    private static String apiKeyCMarket = "3ea268be-397d-4d62-8127-644e8c4f84d3";
    TextView max_market_value;
    TextView circ_sup_view;
    TextView market_cap_val;

    public ImageView backimg;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitcoin);
        backimg = (ImageView)findViewById(R.id.backimg);
        max_market_value = findViewById(R.id.max_sup_val);
        circ_sup_view = findViewById(R.id.circ_sup_val);
        market_cap_val = findViewById(R.id.market_cap_val);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("BPI Loading");
        progressDialog.setMessage("Please wait");
        load_data();
        load_market_stats();

        backimg.setOnClickListener((v)->{backHome();});

    }

    private void backHome() {
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
    }

    private void load_chart(ArrayList<Entry> yValues) {
        myChart = findViewById(R.id.linechart);
        //myChart.setOnChartGestureListener(MoreBTCActivity.this);
        //myChart.setOnChartValueSelectedListener(MoreBTCActivity.this);
        myChart.getDescription().setEnabled(false);
        myChart.getAxisLeft().setTextColor(Color.rgb(255, 189, 0));
        myChart.getAxisRight().setTextColor(Color.rgb(255, 189, 0));
        myChart.getXAxis().setTextColor(Color.rgb(255,189,0));
        myChart.setBackgroundColor(Color.rgb(44,44,83));
        myChart.getLegend().setTextColor(Color.WHITE);
        myChart.setDragEnabled(false);
        myChart.setScaleEnabled(false);
        LineDataSet set1 = new LineDataSet(yValues, "Bitcoin variation");
        set1.setColor(Color.rgb(255, 189, 0));
        set1.setCircleRadius(2);
        set1.setCircleColor(Color.rgb(44,44,83));
        set1.setLineWidth(2);
        set1.setFillAlpha(110);
        set1.setValueTextColor(Color.WHITE);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);
        myChart.setData(data);
        myChart.invalidate();
    }

    private void parseHistResponse(String body) {
        String s;
        int x = 1;
        ArrayList<Entry> yValues = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(body);
            JSONObject bpis = jsonObject.getJSONObject("bpi");
            Iterator<String> it = bpis.keys();
            while (it.hasNext()) {
                s = it.next();
                double yvalue = bpis.getDouble(s);
                yValues.add(new Entry(x++, (float) yvalue));
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (Exception e) {

        }
        load_chart(yValues);
    }

    public void load_data() {
        Request request = new Request.Builder().url(HIST_BPI_LINK).build();
        progressDialog.show();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(Bitcoin.this, "Error during BPI loading: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        parseHistResponse(body);
                    }
                });
            }
        });
    }

    public void load_market_stats() {
        Request request = new Request.Builder().url(MARKET_UPDATES_URL).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(Bitcoin.this, "Error during BPI loading: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        try {
            JSONObject jsonObject = new JSONObject(body);
            JSONArray bpis = jsonObject.getJSONArray("data");
            Log.d("ARRR", bpis.toString());
            JSONObject btc_info = bpis.getJSONObject(CryptoIndex);
            Log.d("OB1", btc_info.toString());
            int circ_cup_int = btc_info.getInt("circulating_supply");
            double btc_price = btc_info.getJSONObject("quote").getJSONObject("USD").getDouble("price");
            String market_cap = Double.toString(btc_price*circ_cup_int);
            Double market_cap_double=Double.parseDouble(market_cap);
            max_market_value.setText(Integer.toString(btc_info.getInt("max_supply")));
            circ_sup_view.setText(Integer.toString(circ_cup_int));
            NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();
            market_cap_val.setText("US" + defaultFormat.format(market_cap_double));


        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            Log.d("CIRCSUPP", e.toString());
        }
    }
}