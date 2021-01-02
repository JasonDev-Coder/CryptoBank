package com.example.ewallet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import nl.joery.animatedbottombar.AnimatedBottomBar;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG =MainActivity.class.getSimpleName();
    public CardView btcCard;
    private static final String MARKET_UPDATES_URL = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=3ea268be-397d-4d62-8127-644e8c4f84d3";
    private static int JSON_INDEX=0;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private ProgressDialog progressDialog;
    private TextView txt,percentage;
    private ImageView logoPercentage;
    private AnimatedBottomBar bottom_bar;
    private Button home,send,recent,receive;
    FragmentManager fragmentManager;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

       // Intent intent =new Intent(getActivity(),HomeActivity.class);
        //startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.home_layout, container, false);
        btcCard = (CardView)v.findViewById(R.id.BitcoinInfo);
        bottom_bar=v.findViewById(R.id.bottom_bar);
        txt = (TextView)v.findViewById(R.id.txt);
        percentage=(TextView)v.findViewById(R.id.perc);
        logoPercentage = (ImageView)v.findViewById(R.id.perc_logo);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("BPI Loading");
        progressDialog.setMessage("Wait...");
        loadPrice();
        loadPercentage();
        btcCard.setOnClickListener(this);
        return v;
    }
    private void loadPrice(){
        Request request = new Request.Builder().url(MARKET_UPDATES_URL).build();
        progressDialog.show();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(),"Error during BPI loading:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();

                getActivity().runOnUiThread(new Runnable() {
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
            JSONObject jsonObject = new JSONObject(body);
            JSONArray bpis = jsonObject.getJSONArray("data");
            JSONObject btc_info = bpis.getJSONObject(0);
            double btc_price = btc_info.getJSONObject("quote").getJSONObject("USD").getDouble("price");
            NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(new Locale("en","US"));
            txt.setText("US" + defaultFormat.format(btc_price));
        } catch (Exception e) {

        }
    }

    private void loadPercentage(){
        Request request = new Request.Builder().url(MARKET_UPDATES_URL).build();
        progressDialog.show();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(),"Error during BPI loading:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();

               getActivity().runOnUiThread(new Runnable() {
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
            JSONObject btc_info = bpis.getJSONObject(JSON_INDEX);
            double btc_percentage = btc_info.getJSONObject("quote").getJSONObject("USD").getDouble("percent_change_1h");
            String btc_perc = Double.toString(btc_percentage);
            Double btc_perc_double= Double.parseDouble(btc_perc);
            btc_perc_double=((double)Math.round(btc_perc_double*100))/100.0;
            percentage.setText(btc_perc_double + "%");

            if(btc_perc_double>=0){
                percentage.setTextColor(Color.GREEN);
                logoPercentage.setImageResource(R.drawable.increase);
                logoPercentage.invalidate();
            }else {
                percentage.setTextColor(Color.RED);
                logoPercentage.setImageResource(R.drawable.decrease);
                logoPercentage.invalidate();

            }
        } catch (Exception e) {}
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.BitcoinInfo:
                Bitcoin.CryptoIndex=0;
                i = new Intent(getActivity(),Bitcoin.class);
                startActivity(i);
                break;

        }
    }
}
