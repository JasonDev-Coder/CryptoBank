package com.example.ewallet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
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
public class HomeFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = MainActivity.class.getSimpleName();
    public CardView btcCard, ethCard, usdtCard, xrpCard, ltcCard;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final String MARKET_UPDATES_URL = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=3ea268be-397d-4d62-8127-644e8c4f84d3";
    private OkHttpClient okHttpClient = new OkHttpClient();
    private ProgressDialog progressDialog;

    private TextView BtcPrice, BtcPercentage1hr, BtcPercentage1day, BtcPercentage1week;
    private TextView EthPrice, EthPercentage1hr, EthPercentage1day, EthPercentage1week;
    private TextView UsdtPrice, USDTPercentage1hr, USDTPercentage1day, USDTPercentage1week;
    private TextView XrpPrice, XRPPercentage1hr, XRPPercentage1day, XRPPercentage1week;
    private TextView LtcPrice, LTCPercentage1hr, LTCPercentage1day, LTCPercentage1week;

    private ImageView BtcLogoPercentage1hr, BtcLogoPercentage1day, BtcLogoPercentage1week;
    private ImageView EthLogoPercentage1hr, EthLogoPercentage1day, EthLogoPercentage1week;
    private ImageView XRPLogoPercentage1hr, XRPLogoPercentage1day, XRPLogoPercentage1week;
    private ImageView USDTLogoPercentage1hr, USDTLogoPercentage1day, USDTLogoPercentage1week;
    private ImageView LTCLogoPercentage1hr, LTCLogoPercentage1day, LTCLogoPercentage1week;

    private AnimatedBottomBar bottom_bar;

    private Button home, send, recent, receive;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.home_layout, container, false);
        swipeRefreshLayout=v.findViewById(R.id.refresh_layout_home);
        btcCard = (CardView) v.findViewById(R.id.BitcoinInfo);
        ethCard = (CardView) v.findViewById(R.id.EthereumInfo);
        usdtCard = (CardView) v.findViewById(R.id.TetherInfo);
        xrpCard = (CardView) v.findViewById(R.id.XrpInfo);
        ltcCard = (CardView) v.findViewById(R.id.LitecoinInfo);


        bottom_bar = v.findViewById(R.id.bottom_bar);

        BtcPrice = (TextView) v.findViewById(R.id.BtcPrice);
        EthPrice = (TextView) v.findViewById(R.id.EthPrice);
        UsdtPrice = (TextView) v.findViewById(R.id.UsdtPrice);
        XrpPrice = (TextView) v.findViewById(R.id.XrpPrice);
        LtcPrice = (TextView) v.findViewById(R.id.LtcPrice);

        BtcPercentage1hr = (TextView) v.findViewById(R.id.btc_perc_text_1hour);
        BtcPercentage1day = (TextView) v.findViewById(R.id.btc_perc_text_1day);
        BtcPercentage1week = (TextView) v.findViewById(R.id.btc_perc_text_1week);


        BtcLogoPercentage1hr = (ImageView) v.findViewById(R.id.btc_perc_logo_1hour);
        BtcLogoPercentage1day = (ImageView) v.findViewById(R.id.btc_perc_logo_1day);
        BtcLogoPercentage1week = (ImageView) v.findViewById(R.id.btc_perc_logo_1week);

        EthPercentage1hr = (TextView) v.findViewById(R.id.eth_perc_text_1hour);
        EthPercentage1day = (TextView) v.findViewById(R.id.eth_perc_text_1day);
        EthPercentage1week = (TextView) v.findViewById(R.id.eth_perc_text_1week);


        EthLogoPercentage1hr = (ImageView) v.findViewById(R.id.eth_perc_logo_1hour);
        EthLogoPercentage1day = (ImageView) v.findViewById(R.id.eth_perc_logo_1day);
        EthLogoPercentage1week = (ImageView) v.findViewById(R.id.eth_perc_logo_1week);

        USDTPercentage1hr = (TextView) v.findViewById(R.id.usdt_perc_text_1hour);
        USDTPercentage1day = (TextView) v.findViewById(R.id.usdt_perc_text_1day);
        USDTPercentage1week = (TextView) v.findViewById(R.id.usdt_perc_text_1week);


        USDTLogoPercentage1hr = (ImageView) v.findViewById(R.id.usdt_perc_logo_1hour);
        USDTLogoPercentage1day = (ImageView) v.findViewById(R.id.usdt_perc_logo_1day);
        USDTLogoPercentage1week = (ImageView) v.findViewById(R.id.usdt_perc_logo_1week);

        XRPPercentage1hr = (TextView) v.findViewById(R.id.xrp_perc_text_1hour);
        XRPPercentage1day = (TextView) v.findViewById(R.id.xrp_perc_text_1day);
        XRPPercentage1week = (TextView) v.findViewById(R.id.xrp_perc_text_1week);


        XRPLogoPercentage1hr = (ImageView) v.findViewById(R.id.xrp_perc_logo_1hour);
        XRPLogoPercentage1day = (ImageView) v.findViewById(R.id.xrp_perc_logo_1day);
        XRPLogoPercentage1week = (ImageView) v.findViewById(R.id.xrp_perc_logo_1week);

        LTCPercentage1hr = (TextView) v.findViewById(R.id.ltc_perc_text_1hour);
        LTCPercentage1day = (TextView) v.findViewById(R.id.ltc_perc_text_1day);
        LTCPercentage1week = (TextView) v.findViewById(R.id.ltc_perc_text_1week);


        LTCLogoPercentage1hr = (ImageView) v.findViewById(R.id.ltc_perc_logo_1hour);
        LTCLogoPercentage1day = (ImageView) v.findViewById(R.id.ltc_perc_logo_1day);
        LTCLogoPercentage1week = (ImageView) v.findViewById(R.id.ltc_perc_logo_1week);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("BPI Loading");
        progressDialog.setMessage("Wait...");
        loadPrice(btcCard);
        loadPercentage(btcCard);

        loadPrice(ethCard);
        loadPercentage(ethCard);

        loadPrice(usdtCard);
        loadPercentage(usdtCard);

        loadPrice(xrpCard);
        loadPercentage(xrpCard);

        loadPrice(ltcCard);
        loadPercentage(ltcCard);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPrice(btcCard);
                loadPercentage(btcCard);

                loadPrice(ethCard);
                loadPercentage(ethCard);

                loadPrice(usdtCard);
                loadPercentage(usdtCard);

                loadPrice(xrpCard);
                loadPercentage(xrpCard);

                loadPrice(ltcCard);
                loadPercentage(ltcCard);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },3*1000);
            }
        });
        btcCard.setOnClickListener(this);
        ethCard.setOnClickListener(this);
        ltcCard.setOnClickListener(this);
        usdtCard.setOnClickListener(this);
        xrpCard.setOnClickListener(this);

        return v;
    }

    private void loadPrice(final CardView cv) {
        Request request = new Request.Builder().url(MARKET_UPDATES_URL).build();
        progressDialog.show();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "Error during BPI loading:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        switch (cv.getId()) {
                            case R.id.BitcoinInfo:
                                parseBpiResponse(body, 0, BtcPrice);
                                break;
                            case R.id.EthereumInfo:
                                parseBpiResponse(body, 1, EthPrice);
                                break;
                            case R.id.TetherInfo:
                                parseBpiResponse(body, 2, UsdtPrice);
                                break;
                            case R.id.XrpInfo:
                                parseBpiResponse(body, 3, XrpPrice);
                                break;
                            case R.id.LitecoinInfo:
                                parseBpiResponse(body, 4, LtcPrice);
                                break;
                        }
                    }
                });
            }
        });
    }

    private void parseBpiResponse(String body, int currencyIndex, TextView price) {
        try {
            JSONObject jsonObject = new JSONObject(body);
            JSONArray bpis = jsonObject.getJSONArray("data");
            JSONObject crypto_info = bpis.getJSONObject(currencyIndex);
            double crypto_price = crypto_info.getJSONObject("quote").getJSONObject("USD").getDouble("price");
            NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
            price.setText("US" + defaultFormat.format(crypto_price));
        } catch (Exception e) {
        }
    }

    private void loadPercentage(final CardView cv) {
        Request request = new Request.Builder().url(MARKET_UPDATES_URL).build();
        progressDialog.show();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "Error during BPI loading:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        switch (cv.getId()) {
                            case R.id.BitcoinInfo:
                                parseBpiPercResponse(body, 0, BtcPercentage1hr, BtcPercentage1day, BtcPercentage1week,
                                        BtcLogoPercentage1hr, BtcLogoPercentage1day, BtcLogoPercentage1week);
                                break;
                            case R.id.EthereumInfo:
                                parseBpiPercResponse(body, 1, EthPercentage1hr, EthPercentage1day, EthPercentage1week,
                                        EthLogoPercentage1hr, EthLogoPercentage1day, EthLogoPercentage1week);
                                break;
                            case R.id.TetherInfo:
                                parseBpiPercResponse(body, 2, USDTPercentage1hr, USDTPercentage1day, USDTPercentage1week,
                                        USDTLogoPercentage1hr, USDTLogoPercentage1day, USDTLogoPercentage1week);
                                break;
                            case R.id.XrpInfo:
                                parseBpiPercResponse(body, 3, XRPPercentage1hr, XRPPercentage1day, XRPPercentage1week,
                                        XRPLogoPercentage1hr, XRPLogoPercentage1day, XRPLogoPercentage1week);
                                break;
                            case R.id.LitecoinInfo:
                                parseBpiPercResponse(body, 4, LTCPercentage1hr, LTCPercentage1day, LTCPercentage1week,
                                        LTCLogoPercentage1hr, LTCLogoPercentage1day, LTCLogoPercentage1week);
                                break;
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void parseBpiPercResponse(String body, int currencyIndex, TextView percentage1hr,
                                      TextView percentage1day, TextView percentage1week,
                                      ImageView logo1hr, ImageView logo1day, ImageView logo1week) {
        try {
            JSONObject jsonObject = new JSONObject(body);
            JSONArray bpis = jsonObject.getJSONArray("data");
            JSONObject crypto_info = bpis.getJSONObject(currencyIndex);
            double curr_percentage_1hr = crypto_info.getJSONObject("quote").getJSONObject("USD").getDouble("percent_change_1h");
            double curr_percentage_1day = crypto_info.getJSONObject("quote").getJSONObject("USD").getDouble("percent_change_24h");
            double curr_percentage_1week = crypto_info.getJSONObject("quote").getJSONObject("USD").getDouble("percent_change_7d");
            String curr_perc_1hr = Double.toString(curr_percentage_1hr);
            String curr_perc_1day = Double.toString(curr_percentage_1day);
            String curr_perc_1week = Double.toString(curr_percentage_1week);

            Double curr_perc_1hr_double = Double.parseDouble(curr_perc_1hr);
            Double curr_perc_1day_double = Double.parseDouble(curr_perc_1day);
            Double curr_perc_1week_double = Double.parseDouble(curr_perc_1week);

            curr_perc_1hr_double = ((double) Math.round(curr_perc_1hr_double * 100)) / 100.0;
            curr_perc_1day_double = ((double) Math.round(curr_perc_1day_double * 100)) / 100.0;
            curr_perc_1week_double = ((double) Math.round(curr_perc_1week_double * 100)) / 100.0;

            percentage1hr.setText(curr_perc_1hr_double + "%");
            percentage1day.setText(curr_perc_1day_double + "%");
            percentage1week.setText(curr_perc_1week_double + "%");

            if (curr_perc_1hr_double >= 0) {
                percentage1hr.setTextColor(Color.GREEN);
                logo1hr.setImageResource(R.drawable.increase);
                logo1hr.invalidate();
            } else {
                percentage1hr.setTextColor(Color.RED);
                logo1hr.setImageResource(R.drawable.decrease);
                logo1hr.invalidate();

            }
            if (curr_perc_1day_double >= 0) {
                percentage1day.setTextColor(Color.GREEN);
                logo1day.setImageResource(R.drawable.increase);
                logo1day.invalidate();
            } else {
                percentage1day.setTextColor(Color.RED);
                logo1day.setImageResource(R.drawable.decrease);
                logo1day.invalidate();
            }
            if (curr_perc_1week_double >= 0) {
                percentage1week.setTextColor(Color.GREEN);
                logo1week.setImageResource(R.drawable.increase);
                logo1week.invalidate();
            } else {
                percentage1week.setTextColor(Color.RED);
                logo1week.setImageResource(R.drawable.decrease);
                logo1week.invalidate();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        Intent i;
        i = new Intent(getActivity(), CryptoInfo.class);
        switch (v.getId()) {
            case R.id.BitcoinInfo:
                CryptoInfo.CryptoIndex = 0;
                break;
            case R.id.EthereumInfo:
                CryptoInfo.CryptoIndex = 1;
                break;
            case R.id.TetherInfo:
                CryptoInfo.CryptoIndex = 2;
                break;
            case R.id.XrpInfo:
                CryptoInfo.CryptoIndex = 3;
                break;
            case R.id.LitecoinInfo:
                CryptoInfo.CryptoIndex = 4;
                break;
            default:
                CryptoInfo.CryptoIndex = -1;
        }
        startActivity(i);
    }
}
