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
    static int CryptoIndex = -1;
    private LineChart myChart;
    private static final String HIST_BPI_LINK = "https://api.coindesk.com/v1/bpi/historical/close.json";
    private static final String MARKET_UPDATES_URL = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=3ea268be-397d-4d62-8127-644e8c4f84d3";
    private OkHttpClient okHttpClient = new OkHttpClient();
    private ProgressDialog progressDialog;
    private static String apiKeyCMarket = "3ea268be-397d-4d62-8127-644e8c4f84d3";
    TextView max_market_value;
    TextView circ_sup_view;
    TextView market_cap_val;
    TextView About_View;
    TextView About_Info;
    TextView crypto_name;
    ImageView crypto_logo;
    public ImageView backimg;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitcoin);
        backimg = (ImageView) findViewById(R.id.backimg);
        max_market_value = findViewById(R.id.max_sup_val);
        circ_sup_view = findViewById(R.id.circ_sup_val);
        market_cap_val = findViewById(R.id.market_cap_val);
        About_View = findViewById(R.id.aboutview);
        About_Info = findViewById(R.id.desc_crypto);
        crypto_name=findViewById(R.id.cryptotxt);
        crypto_logo=findViewById(R.id.cryptoLogo);
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
        switch (CryptoIndex) {
            case 0:
                About_View.setText("About Bitcoin");
                crypto_name.setText("Bitcoin");
                crypto_logo.setImageResource(R.drawable.bitcoin);
                About_Info.setText(R.string.btc_info);
                break;
            case 1:
                About_View.setText("About Etherum");
                crypto_name.setText("Etherum");
                crypto_logo.setImageResource(R.drawable.ethereum);
                About_Info.setText(R.string.eth_info);
                break;
            case 2:
                About_View.setText("About Tether");
                crypto_name.setText("USD-T");
                crypto_logo.setImageResource(R.drawable.tether);
                About_Info.setText(R.string.usdt_info);
                break;
            case 3:
                About_View.setText("About XRP");
                crypto_name.setText("XRP");
                crypto_logo.setImageResource(R.drawable.xrp);
                About_Info.setText(R.string.xrp_info);
                break;
            case 4:
                About_View.setText("About Litecoin");
                crypto_name.setText("Litecoin");
                crypto_logo.setImageResource(R.drawable.litecoin);
                About_Info.setText(R.string.ltc_info);
                break;

        }
    }

    public void load_data() {
        Request request = new Request.Builder().url(HIST_BPI_LINK).build();
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


    private void load_chart(ArrayList<Entry> yValues) {
        String cryp_name;
        switch (CryptoIndex) {
            case 0:
                cryp_name = "Bitcoin";
                break;
            case 1:
                cryp_name = "Etherum";
                break;
            case 2:
                cryp_name = "Tether";
                break;
            case 3:
                cryp_name = "Ripple";
                break;
            case 4:
                cryp_name = "Litecoin";
                break;
            default:
                cryp_name = null;
                break;
        }
        myChart = findViewById(R.id.linechart);
        //myChart.setOnChartGestureListener(MoreBTCActivity.this);
        //myChart.setOnChartValueSelectedListener(MoreBTCActivity.this);
        myChart.getDescription().setEnabled(false);
        myChart.getAxisLeft().setTextColor(Color.rgb(255, 189, 0));
        myChart.getAxisRight().setTextColor(Color.rgb(255, 189, 0));
        myChart.getXAxis().setTextColor(Color.rgb(255, 189, 0));
        myChart.setBackgroundColor(Color.rgb(44, 44, 83));
        myChart.getLegend().setTextColor(Color.WHITE);
        myChart.setDragEnabled(false);
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

    public void load_market_stats() {
        Request request = new Request.Builder().url(MARKET_UPDATES_URL).build();
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
        String curr_name;
        switch (CryptoIndex) {
            case 0:
                curr_name = "Bitcoin";
                break;
            case 1:
                curr_name = "Etherum";
                break;
            case 2:
                curr_name = "USD-T";
                break;
            case 3:
                curr_name = "XRP";
                break;
            case 4:
                curr_name = "Litecoin";
                break;
            default:
                curr_name = null;
        }
        try {
            JSONObject jsonObject = new JSONObject(body);
            JSONArray bpis = jsonObject.getJSONArray("data");
            JSONObject crypto_info = bpis.getJSONObject(CryptoIndex);
            double circ_cup_int = crypto_info.getDouble("circulating_supply");
            double crypto_price = crypto_info.getJSONObject("quote").getJSONObject("USD").getDouble("price");
            Double market_cap_double = circ_cup_int * crypto_price;
            String max_market_val = crypto_info.getString("max_supply");
            Log.d("MAX_MARKET", String.valueOf(max_market_val));
            if (max_market_val == "null")
                max_market_value.setText("No Cap");
            else
                max_market_value.setText(truncateNumber((float) crypto_info.getDouble("max_supply")) + " " + curr_name);
            circ_sup_view.setText(truncateNumber((float) circ_cup_int) + " " + curr_name);
            NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
            market_cap_val.setText("US" + defaultFormat.format(market_cap_double));
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            Log.d("CIRCSUPP", e.toString());
        }
    }

    public String truncateNumber(float floatNumber) {
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