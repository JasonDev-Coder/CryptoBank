package com.example.ewallet;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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

    // private View walletView;
    private LinearLayout walletList;
    //ImageView removeView;//wallet delete


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
    ArrayList<HashMap<String, String>> walletListMaps = new ArrayList<>();
    FragmentManager fragmentManager;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static boolean addWalletBoolean = false;

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
        walletList = (LinearLayout) v.findViewById(R.id.wallets);
        ImageView addWalletMenu = (ImageView) v.findViewById(R.id.addWallet_menu);
        addWalletMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), v);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_ethereum:
                                addWallet("Etherum", R.drawable.ethereum, "ETH", "0.01");
                                return true;
                            case R.id.menu_tether:
                                addWallet("Tether", R.drawable.tether, "USD-T", "0.01");
                                return true;
                            case R.id.menu_xrp:
                                addWallet("Ripple", R.drawable.xrp, "XRP", "0.01");
                                return true;
                            case R.id.menu_litecoin:
                                addWallet("Litecoin", R.drawable.litecoin, "LTC", "0.01");
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
        swipeRefreshLayout = v.findViewById(R.id.refresh_layout_home);
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
            public void onRefresh() {//reload everything that contains live values
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
                loadWallets();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3 * 1000);
            }
        });
        btcCard.setOnClickListener(this);
        ethCard.setOnClickListener(this);
        ltcCard.setOnClickListener(this);
        usdtCard.setOnClickListener(this);
        xrpCard.setOnClickListener(this);

        loadWallets();
        return v;
    }

    private void addWallet(final String CurrencyName, final int image, final String currency_type, final String balance) {
        try {
            new AsyncAddWallet().execute(CurrencyName).get();/*here i put .get() just because i want the thread to wait until AsyncAddWallet finishes execution
                  so we don't enter the if statment before the boolean changes its value*/
        } catch (Exception e) {
        }
        if (addWalletBoolean) {//if addWalletBoolean was true meaning the user doesnt have the wallet and everything is okay we can add the wallet
            addWalletView(CurrencyName, image, currency_type, balance);
            addWalletBoolean = false;//after adding the wallet we have to reset the boolean
        }

    }

    private void addWalletView(String CurrencyName, int image, String currency_type, String balance) {   //customize and add wallet to layout

        final View walletView = getLayoutInflater().inflate(R.layout.wallet, null, false);//inflate the xml layout which represents the wallet
        ImageView wallet_logo = (ImageView) walletView.findViewById(R.id.wallet_logo);                      //change logo according to currency chosen
        ImageView removeView = (ImageView) walletView.findViewById(R.id.removeWallet);
        removeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeWalletView(walletView);                                                                //remove view from the layout
            }
        });
        TextView wallet_balance = (TextView) walletView.findViewById(R.id.wallet_balance);
        TextView wallet_balance_text = (TextView) walletView.findViewById(R.id.wallet_currency_name);
        wallet_logo.setImageResource(image);
        wallet_balance.setText(balance);
        wallet_balance_text.setText(currency_type);
        walletList.addView(walletView);
    }

    private void loadWallets() {
        try {
            new LoadWallets().execute().get();
        } catch (Exception e) {
        }
        if (!walletListMaps.isEmpty()) {//walletListMaps will contain hash maps  containing the user's wallets
            for (HashMap<String, String> wallet : walletListMaps) {
                String wallet_name = wallet.get("type_name");
                String wallet_balance = wallet.get("balance");
                String wallet_type_symbol = wallet.get("type_symbol");
                switch (wallet_name) {//depending on wallet name add an xml layout of the wallet
                    case "Bitcoin":
                        addWalletView(wallet_name, R.drawable.bitcoin, wallet_type_symbol, wallet_balance);
                        break;
                    case "Etherum":
                        addWalletView(wallet_name, R.drawable.ethereum, wallet_type_symbol, wallet_balance);
                        break;
                    case "Tether":
                        addWalletView(wallet_name, R.drawable.tether, wallet_type_symbol, wallet_balance);
                        break;
                    case "Ripple":
                        addWalletView(wallet_name, R.drawable.xrp, wallet_type_symbol, wallet_balance);
                        break;
                    case "Litecoin":
                        addWalletView(wallet_name, R.drawable.litecoin, wallet_type_symbol, wallet_balance);
                        break;
                    default:
                }
            }
        }
    }

    private void removeWalletView(final View v) {   //remove wallet from layout

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Deleting your wallet will result in the loss of your balance!\nWould you like to proceed?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                walletList.removeView(v);
                dialog.cancel();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadPrice(final CardView cv) {
        Request request = new Request.Builder().url(CONSTANTS.MARKET_UPDATES_URL).build();
        progressDialog.show();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "Error during BPI loading:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();
                if (getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        switch (cv.getId()) {
                            case R.id.BitcoinInfo:
                                parseBpiResponse(body, CONSTANTS.BITCOIN_INDEX_JSON, BtcPrice);
                                break;
                            case R.id.EthereumInfo:
                                parseBpiResponse(body, CONSTANTS.ETHERUM_INDEX_JSON, EthPrice);
                                break;
                            case R.id.TetherInfo:
                                parseBpiResponse(body, CONSTANTS.TETHER_INDEX_JSON, UsdtPrice);
                                break;
                            case R.id.XrpInfo:
                                parseBpiResponse(body, CONSTANTS.XRP_INDEX_JSON, XrpPrice);
                                break;
                            case R.id.LitecoinInfo:
                                parseBpiResponse(body, CONSTANTS.LITECOIN_INDEX_JSON, LtcPrice);
                                break;
                        }
                    }
                });
            }
        });
    }

    private void parseBpiResponse(String body, int currencyIndex, TextView price) {
        try {
            JSONObject jsonObject = new JSONObject(body);                           //get the JSON body
            JSONArray bpis = jsonObject.getJSONArray("data");                //get the array data which contains the currencies
            JSONObject crypto_info = bpis.getJSONObject(currencyIndex);
            double crypto_price = crypto_info.getJSONObject("quote").getJSONObject("USD").getDouble("price");
            NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
            price.setText("US" + defaultFormat.format(crypto_price));
        } catch (Exception e) {
        }
    }

    private void loadPercentage(final CardView cv) {
        Request request = new Request.Builder().url(CONSTANTS.MARKET_UPDATES_URL).build();
        progressDialog.show();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "Error during BPI loading:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();
                if (getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        switch (cv.getId()) {
                            case R.id.BitcoinInfo:
                                parseBpiPercResponse(body, CONSTANTS.BITCOIN_INDEX_JSON, BtcPercentage1hr, BtcPercentage1day, BtcPercentage1week,
                                        BtcLogoPercentage1hr, BtcLogoPercentage1day, BtcLogoPercentage1week);
                                break;
                            case R.id.EthereumInfo:
                                parseBpiPercResponse(body, CONSTANTS.ETHERUM_INDEX_JSON, EthPercentage1hr, EthPercentage1day, EthPercentage1week,
                                        EthLogoPercentage1hr, EthLogoPercentage1day, EthLogoPercentage1week);
                                break;
                            case R.id.TetherInfo:
                                parseBpiPercResponse(body, CONSTANTS.TETHER_INDEX_JSON, USDTPercentage1hr, USDTPercentage1day, USDTPercentage1week,
                                        USDTLogoPercentage1hr, USDTLogoPercentage1day, USDTLogoPercentage1week);
                                break;
                            case R.id.XrpInfo:
                                parseBpiPercResponse(body, CONSTANTS.XRP_INDEX_JSON, XRPPercentage1hr, XRPPercentage1day, XRPPercentage1week,
                                        XRPLogoPercentage1hr, XRPLogoPercentage1day, XRPLogoPercentage1week);
                                break;
                            case R.id.LitecoinInfo:
                                parseBpiPercResponse(body, CONSTANTS.LITECOIN_INDEX_JSON, LTCPercentage1hr, LTCPercentage1day, LTCPercentage1week,
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
                CryptoInfo.CryptoIndex = CONSTANTS.BITCOIN_INDEX_JSON;
                break;
            case R.id.EthereumInfo:
                CryptoInfo.CryptoIndex = CONSTANTS.ETHERUM_INDEX_JSON;
                break;
            case R.id.TetherInfo:
                CryptoInfo.CryptoIndex = CONSTANTS.TETHER_INDEX_JSON;
                break;
            case R.id.XrpInfo:
                CryptoInfo.CryptoIndex = CONSTANTS.XRP_INDEX_JSON;
                break;
            case R.id.LitecoinInfo:
                CryptoInfo.CryptoIndex = CONSTANTS.LITECOIN_INDEX_JSON;
                break;
            default:
                CryptoInfo.CryptoIndex = -1;
        }
        startActivity(i);
    }


    private class AsyncAddWallet extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        HttpURLConnection conn;
        URL url = null;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // this method runs on same UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL("http://10.0.2.2/cryptoBank/views/insertWallet.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("CONNECTPHP", "error in connection1");
                return "exception1";
            }
            try {
                //here we will setup HttpURLConnection to connect with php scripts to send and receive data
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");//the request method must correspond with the written php code

                conn.setDoInput(true);
                conn.setDoOutput(true);
                String session_id = null;
                if (getActivity() != null) {//We get the stored session id to use the current session
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    session_id = prefs.getString("session_id", null);
                    Log.d("sessionid_id", session_id);
                }
                //append parameters to url so that the script uses them
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("cryptoName", params[0])//params[0] is the message from AsyncAddWallet().execute(wallet name);
                        .appendQueryParameter("session_id", session_id);
                String query = builder.build().getEncodedQuery();
                OutputStream os;
                os = conn.getOutputStream();//Open connection
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                if (query != null)
                    writer.write(query);//write the formed query to the output stream
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                Log.d("CONNECTPHP", Arrays.toString(e1.getStackTrace()));
                return "exception2";
            }

            try {
                int response_code = conn.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);//the result here will be what the echo from the php script
                    }
                    if (result.toString().contains("true")) {
                        addWalletBoolean = true;/*When we get the result from php telling us if the user wallet was added to the DB
                         then we change addWalletBoolean to true meaning we can inflate now an xml layout containing the wallet view*/
                        /*i didnt put this in post execute because then when ths method finishes the thread continues and the if statement in addWallet will not be changed*/
                    }
                    return result.toString();//result will be used in onPostExecute method
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Connection Failed");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    Log.d("CONNECTPHP", "error in connection3");
                    return "unsuccesful";
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("CONNECTPHP", "error in connection4");
                return "exception3";
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //this method will be running on UI thread
            pdLoading.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            if (result.equalsIgnoreCase("true")) {

            } else if (result.equalsIgnoreCase("Wallet Exist")) {
                builder.setMessage("Wallet Already Exist");
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                builder.setMessage("Unknown error");
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        }

    }


    private class LoadWallets extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        HttpURLConnection conn;
        URL url = null;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... args) {
            try {
                url = new URL("http://10.0.2.2/cryptoBank/views/getWallets.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("CONNECTPHP", "error in connection1");
                return "exception1";
            }
            try {
                //here we will setup HttpURLConnection to connect with php scripts to send and receive data
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");//the request method must correspond with the written php code

                conn.setDoInput(true);
                conn.setDoOutput(true);
                String session_id = null;
                if (getActivity() != null) {//We get the stores session id to use the current session
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    session_id = prefs.getString("session_id", null);
                    Log.v("sessionid_id", session_id);
                }
                //append parameters to url so that the script uses them
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("session_id", session_id);
                String query = builder.build().getEncodedQuery();
                OutputStream os;
                os = conn.getOutputStream();//Open connection
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                if (query != null)
                    writer.write(query);//write the formed query to the output stream
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                Log.d("CONNECTPHP", Arrays.toString(e1.getStackTrace()));
                return "exception2";
            }

            try {
                int response_code = conn.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);//the result here will be what the echo from the php script
                    }
                    try {
                        JSONObject json = new JSONObject(result.toString());
                        int success = json.getInt("success");
                        if (success == 1) {
                            walletListMaps.clear();
                            JSONArray walletsArray = json.getJSONArray("wallets");
                            Log.d("wallets", walletsArray.toString());
                            for (int i = 0; i < walletsArray.length(); i++) {
                                JSONObject jsonWallet = walletsArray.getJSONObject(i);
                                HashMap<String, String> walletMap = new HashMap<>();
                                Iterator<String> walletIterator = jsonWallet.keys();
                                while (walletIterator.hasNext()) {
                                    String key = walletIterator.next();
                                    walletMap.put(key, jsonWallet.getString(key));
                                }
                                walletListMaps.add(walletMap);
                            }
                        }

                    } catch (JSONException e1) {
                        Log.d("JSonError", Arrays.toString(e1.getStackTrace()));
                    }
                    return result.toString();//result will be used in onPostExecute method
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Connection Failed");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    Log.d("CONNECTPHP", "error in connection3");
                    return "unsuccesful";
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("CONNECTPHP", "error in connection4");
                return "exception3";
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //this method will be running on UI thread

        }

    }
}
