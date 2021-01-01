package com.example.ewalletx2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.TextLinks;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public CardView btcCard;
    public static final String BPI_ENDPOINT = "https://api.coindesk.com/v1/bpi/currentprice.json";
    private static final String MARKET_UPDATES_URL = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=3ea268be-397d-4d62-8127-644e8c4f84d3";
    private OkHttpClient okHttpClient = new OkHttpClient();
    private ProgressDialog progressDialog;
    private TextView txt,percentage;
    private ImageView logoPercentage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btcCard = (CardView)findViewById(R.id.BitcoinInfo);

        txt = (TextView)findViewById(R.id.txt);
        percentage=(TextView)findViewById(R.id.perc);
        ImageView logoPercentage = (ImageView)findViewById(R.id.perc_logo);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("BPI Loading");
        progressDialog.setMessage("Wait ...");
        loadPrice();
        loadPercentage();

        btcCard.setOnClickListener(this);
    }

    private void loadPrice(){
        Request request = new Request.Builder().url(BPI_ENDPOINT).build();
        progressDialog.show();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this,"Error during BPI loading:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        parseBpiResponse(body);
                    }
                });
            }
        });
    }
    private void parseBpiResponse(String body){
        try {
            StringBuilder builder1 = new StringBuilder();

            JSONObject jsonObject = new JSONObject(body);

            JSONObject bpiObject = jsonObject.getJSONObject("bpi");
            JSONObject usdObject = bpiObject.getJSONObject("USD");
            builder1.append(usdObject.getString("rate")).append("$").append("\n");

            txt.setText(builder1.toString());
        } catch (Exception e) {

        }
    }

    private void loadPercentage(){
        Request request = new Request.Builder().url(MARKET_UPDATES_URL).build();
        progressDialog.show();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this,"Error during BPI loading:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        parseBpiPercResponse(body);
                    }
                });
            }
        });
    }



    private void parseBpiPercResponse(String body){
        try {
            JSONObject jsonObject = new JSONObject(body);
            JSONArray bpis = jsonObject.getJSONArray("data");
            JSONObject btc_info = bpis.getJSONObject(0);
            double btc_percentage = btc_info.getJSONObject("quote").getJSONObject("USD").getDouble("percent_change_1h");
            String btc_perc = Double.toString(btc_percentage);
            Double btc_perc_double= Double.parseDouble(btc_perc);

            percentage.setText(btc_perc_double + "%");

            if(btc_perc_double>=0){
                percentage.setTextColor(Color.GREEN);
                logoPercentage.setImageResource(R.drawable.increase);
            }else {
                percentage.setTextColor(Color.RED);
                logoPercentage.setImageResource(R.drawable.decrease);
            }
        } catch (Exception e) {}
    }


    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.BitcoinInfo:
                i = new Intent (this,Bitcoin.class);
                startActivity(i);
                break;
        }
    }
}