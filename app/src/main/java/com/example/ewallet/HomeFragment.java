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

import androidx.annotation.Nullable;
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
import android.widget.ProgressBar;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

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
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = MainActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout walletList;
    private LinearLayout cryptoCardsLayout;
    private AnimatedBottomBar bottom_bar;
    private Button home, send, recent, receive;
    private ArrayList<HashMap<String, String>> walletListMaps = new ArrayList<>();//store the wallets of the user here
    public static HashMap<String, Double> cryptoPrices = new HashMap<>();//save crypto prices here
    public static ArrayList<CurrencyType> SupportedCurrencies = new ArrayList<>();
    FragmentManager fragmentManager;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static boolean addWalletBoolean = false;
    private static boolean deleteWalletSuccess = false;
    private ProgressDialog dialog;

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
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
        dialog.setTitle("Loading Wallets");
        dialog.show();
        cryptoCardsLayout = v.findViewById(R.id.cards_layout);
        walletList = (LinearLayout) v.findViewById(R.id.wallets);

        new GetCurrencies().execute();
        ImageView addWalletMenu = (ImageView) v.findViewById(R.id.addWallet_menu);
        addWalletMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), v);
                for (CurrencyType currency : SupportedCurrencies) {
                    popup.getMenu().add(0, currency.getId(), 0, currency.getCurrencyName());
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case 1:
                                addWallet(SupportedCurrencies.get(item.getItemId() - 1).getCurrencyName(), R.drawable.bitcoin, SupportedCurrencies.get(item.getItemId() - 1).getCurrencySymbol(), "0.01");
                                return true;
                            case 2:
                                addWallet(SupportedCurrencies.get(item.getItemId() - 1).getCurrencyName(), R.drawable.ethereum, SupportedCurrencies.get(item.getItemId() - 1).getCurrencySymbol(), "0.01");
                                return true;
                            case 3:
                                addWallet(SupportedCurrencies.get(item.getItemId() - 1).getCurrencyName(), R.drawable.tether, SupportedCurrencies.get(item.getItemId() - 1).getCurrencySymbol(), "0.01");
                                return true;
                            case 4:
                                addWallet(SupportedCurrencies.get(item.getItemId() - 1).getCurrencyName(), R.drawable.xrp, SupportedCurrencies.get(item.getItemId() - 1).getCurrencySymbol(), "0.01");
                                return true;
                            case 5:
                                addWallet(SupportedCurrencies.get(item.getItemId() - 1).getCurrencyName(), R.drawable.litecoin, SupportedCurrencies.get(item.getItemId() - 1).getCurrencySymbol(), "0.01");
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
        bottom_bar = v.findViewById(R.id.bottom_bar);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {//reload everything that contains live values
                new LoadWallets().execute();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3 * 1000);
            }
        });
        return v;
    }

    private void addWallet(final String CurrencyName, final int image, final String currency_type, final String balance) {
        try {
            new AsyncAddWallet().execute(CurrencyName).get();/*here i put .get() just because i want the thread to wait until AsyncAddWallet finishes execution
                  so we don't enter the if statement before the boolean changes its value*/
        } catch (Exception e) {
        }
        if (addWalletBoolean) {//if addWalletBoolean was true meaning the user doesnt have the wallet and everything is okay we can add the wallet
            addWalletView(CurrencyName, image, currency_type, balance);
            addWalletBoolean = false;//after adding the wallet we have to reset the boolean
        }

    }

    private void addWalletView(final String CurrencyName, int image, String currency_type, String balance) {   //customize and add wallet to layout

        final View walletView = getLayoutInflater().inflate(R.layout.wallet, null, false);//inflate the xml layout which represents the wallet
        ImageView wallet_logo = (ImageView) walletView.findViewById(R.id.wallet_logo);                      //change logo according to currency chosen
        ImageView removeView = (ImageView) walletView.findViewById(R.id.removeWallet);
        removeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeWalletView(walletView, CurrencyName);                                                                //remove view from the layout
            }
        });
        TextView wallet_balance = (TextView) walletView.findViewById(R.id.wallet_balance);
        TextView wallet_balance_text = (TextView) walletView.findViewById(R.id.wallet_currency_name);
        wallet_logo.setImageResource(image);
        wallet_balance.setText(balance);
        wallet_balance_text.setText(currency_type);

        final TextView wallet_usd_balance = walletView.findViewById(R.id.wallet_balance_usd);
        final double current_crypto_balance = Double.parseDouble(balance);
        double current_usd_balance = 0;
        if (cryptoPrices.get(currency_type) != null)
            current_usd_balance = current_crypto_balance * cryptoPrices.get(currency_type);
        current_usd_balance = Math.round(current_usd_balance * 1000) / 1000.0;
        wallet_usd_balance.setText(Double.toString(current_usd_balance));
        walletList.addView(walletView);
    }

    private void loadWallets() {
    Log.v("WALLETPRICE",cryptoPrices.size()+"");
        if (!walletListMaps.isEmpty()) {//walletListMaps will contain hash maps  containing the user's wallets
            walletList.removeAllViews();
            for (HashMap<String, String> wallet : walletListMaps) {
                String wallet_name = wallet.get("type_name");
                String wallet_balance = wallet.get("balance");
                String wallet_type_symbol = wallet.get("type_symbol");
                switch (wallet_name) {//depending on wallet name add an xml layout of the wallet
                    case "Bitcoin":
                        addWalletView(wallet_name, R.drawable.bitcoin, wallet_type_symbol, wallet_balance);
                        break;
                    case "Ethereum":
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

    private void removeWalletView(final View v, final String CurrencyName) {   //remove wallet from layout

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Deleting your wallet will result in the loss of your balance!\nWould you like to proceed?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    new DeleteWallets().execute(CurrencyName).get();
                } catch (Exception e) {
                    Log.v("DeleteWallet", Arrays.toString(e.getStackTrace()));
                }
                if (deleteWalletSuccess) {
                    walletList.removeView(v);
                    deleteWalletSuccess = false;
                }

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
                url = new URL("http://10.0.2.2/cryptoBank/public/WalletController/addWallet");
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
            try {
                JSONObject jsonResponse = new JSONObject(result);
                Log.v("JSONresponsee", result);
                String error_type = jsonResponse.getString("error_type");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                if (error_type.equalsIgnoreCase("true")) {

                } else if (error_type.equalsIgnoreCase("Wallet Exist")) {
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
            } catch (JSONException j1) {
                Log.d("JsonResponse", Arrays.toString(j1.getStackTrace()));
            }
        }

    }

    private class LoadWallets extends AsyncTask<String, String, String> {
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
                url = new URL("http://10.0.2.2/cryptoBank/public/WalletController/getWallets");
            } catch (MalformedURLException e) {
                e.printStackTrace();
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

                    return "unsuccesful";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "exception3";
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject json = new JSONObject(result);
                Log.v("JSONresponse", result);
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
                    Log.v("WALLETTT",walletListMaps.size()+"");
                    loadWallets();
                }

            } catch (JSONException e1) {
                Log.v("JSonError", Arrays.toString(e1.getStackTrace()));
                e1.printStackTrace();
            }
        }

    }

    private class DeleteWallets extends AsyncTask<String, String, String> {
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
        protected String doInBackground(String... params) {
            try {
                url = new URL("http://10.0.2.2/cryptoBank/public/WalletController/DeleteWallet");
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
                        .appendQueryParameter("wallet_name", params[0])//wallet name in post
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
                        Log.v("JSONresponse", result.toString());
                        int success = json.getInt("success");
                        if (success == 1) {
                            deleteWalletSuccess = true;
                        }
                    } catch (JSONException e1) {
                        Log.v("JSonError", Arrays.toString(e1.getStackTrace()));
                        e1.printStackTrace();
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
    }

    private class GetCurrencies extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL("http://10.0.2.2/cryptoBank/public/WalletController/getSupportedWallets");
            } catch (MalformedURLException e) {
                e.printStackTrace();
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
                //append parameters to url so that the script uses them
                Uri.Builder builder = new Uri.Builder();
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
            try {
                JSONObject json = new JSONObject(result);
                Log.v("JSONresponse", result);
                int success = json.getInt("success");
                if (success == 1) {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        SupportedCurrencies.clear();
                        JSONArray currencies = jsonResponse.getJSONArray("supported_wallets");
                        for (int i = 0; i < currencies.length(); i++) {
                            JSONObject currency = currencies.getJSONObject(i);
                            String cur_name = currency.getString("type_name");
                            String cur_symbol = currency.getString("type_symbol");
                            int id = currency.getInt("type_id");
                            SupportedCurrencies.add(new CurrencyType(cur_name, cur_symbol, id));
                        }
                    } catch (JSONException j) {

                    }
                }
            } catch (JSONException e1) {
                Log.v("JSonError", Arrays.toString(e1.getStackTrace()));
                e1.printStackTrace();
            }
            new loadCryptoPrices().execute();
        }
    }

    private class loadCryptoPrices extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            URL url = null;
            URLConnection urlConnection = null;
            try {
                for (int i = 0; i < SupportedCurrencies.size(); i++) {
                    if (SupportedCurrencies.get(i).getCurrencyName().equals("Tether")) {
                        cryptoPrices.put("USDT", 1.0);
                        continue;
                    }
                    try {
                        url = new URL(CONSTANTS.getUrlFor(SupportedCurrencies.get(i).getCurrencySymbol()));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    urlConnection = url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);//the result here will be what the echo from the php script
                    }
                    try {
                        JSONObject json = new JSONObject(result.toString());
                        cryptoPrices.put(SupportedCurrencies.get(i).getCurrencySymbol(), json.getDouble("price"));
                    } catch (JSONException e1) {
                        Log.v("JSonError", Arrays.toString(e1.getStackTrace()));
                        e1.printStackTrace();
                    }
                }
                return null;

            } catch (IOException e) {
                Log.d("CONNECTPHP4", "error in connection4");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new LoadWallets().execute();
            new loadCryptoPercentage().execute();
        }
    }

    private class loadCryptoPercentage extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            URL url = null;
            try {
                url = new URL(CONSTANTS.MARKET_UPDATES_URL2);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            URLConnection urlConnection = null;
            try {
                urlConnection = url.openConnection();
                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);//the result here will be what the echo from the php script
                }
                return result.toString();//result will be used in onPostExecute meth

            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Log.v("SUPPORTEDD",SupportedCurrencies.size()+"");
            Log.v("SUPPORTEDD",cryptoPrices.size()+"");
            loadCryptoCards(SupportedCurrencies,s);
        }
    }

    private void loadCryptoCards(ArrayList<CurrencyType> currencyTypes,String jsonResponse) {
        cryptoCardsLayout.removeAllViews();
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            jsonArray = jsonObject.getJSONArray("data");
        } catch (JSONException e) {
            return;
        }
        for (final CurrencyType cur : currencyTypes) {
            final View cardView = getLayoutInflater().inflate(R.layout.crypto_card, null, false);
            TextView CryptoName = cardView.findViewById(R.id.crypto_name);
            TextView CryptoSymbol = cardView.findViewById(R.id.crypto_symbol);
            TextView CryptoPrice = cardView.findViewById(R.id.crypto_price);
            CryptoName.setText(cur.getCurrencyName());
            CryptoSymbol.setText(cur.getCurrencySymbol());
            NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
            CryptoPrice.setText("US" + defaultFormat.format(cryptoPrices.get(cur.getCurrencySymbol())));
            ImageView CryptoLogo = cardView.findViewById(R.id.logo);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i;
                    i = new Intent(getActivity(), CryptoInfo.class);
                    switch (cur.getCurrencySymbol()) {
                        case "BTC":
                            CryptoInfo.CryptoIndex = CONSTANTS.BITCOIN_INDEX_JSON;
                            break;
                        case "ETH":
                            CryptoInfo.CryptoIndex = CONSTANTS.ETHERUM_INDEX_JSON;
                            break;
                        case "USDT":
                            CryptoInfo.CryptoIndex = CONSTANTS.TETHER_INDEX_JSON;
                            break;
                        case "XRP":
                            CryptoInfo.CryptoIndex = CONSTANTS.XRP_INDEX_JSON;
                            break;
                        case "LTC":
                            CryptoInfo.CryptoIndex = CONSTANTS.LITECOIN_INDEX_JSON;
                            break;
                        default:
                            CryptoInfo.CryptoIndex = -1;
                    }
                    startActivity(i);
                }
            });
            switch (cur.getCurrencySymbol()) {
                case "BTC":
                    CryptoLogo.setImageResource(R.drawable.bitcoin);
                    break;
                case "ETH":
                    CryptoLogo.setImageResource(R.drawable.ethereum);
                    break;
                case "USDT":
                    CryptoLogo.setImageResource(R.drawable.tether);
                    break;
                case "XRP":
                    CryptoLogo.setImageResource(R.drawable.xrp);
                    break;
                case "LTC":
                    CryptoLogo.setImageResource(R.drawable.litecoin);
                    break;
            }
            Double curr_perc_1hr_double = 0.0;
            Double curr_perc_1day_double = 0.0;
            Double curr_perc_1week_double = 0.0;
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    if (jsonArray.getJSONObject(i).getString("symbol").equals(cur.getCurrencySymbol())) {
                        JSONObject usd_object = jsonArray.getJSONObject(i).getJSONObject("quote").getJSONObject("USD");
                        curr_perc_1hr_double = usd_object.getDouble("percent_change_1h");
                        curr_perc_1day_double = usd_object.getDouble("percent_change_24h");
                        curr_perc_1week_double = usd_object.getDouble("percent_change_7d");
                        break;
                    }
                } catch (JSONException j) {

                }
            }
            curr_perc_1hr_double = ((double) Math.round(curr_perc_1hr_double * 100)) / 100.0;
            curr_perc_1day_double = ((double) Math.round(curr_perc_1day_double * 100)) / 100.0;
            curr_perc_1week_double = ((double) Math.round(curr_perc_1week_double * 100)) / 100.0;


            TextView CryptoOneHour = cardView.findViewById(R.id.crypto_perc_text_1hour);
            TextView CryptoOneDay = cardView.findViewById(R.id.crypto_perc_text_1day);
            TextView CryptoOneWeek = cardView.findViewById(R.id.crypto_perc_text_1week);

            CryptoOneHour.setText(Double.toString(curr_perc_1hr_double));
            CryptoOneDay.setText(Double.toString(curr_perc_1day_double));
            CryptoOneWeek.setText(Double.toString(curr_perc_1week_double));

            ImageView CryptoOneHourLogo = cardView.findViewById(R.id.crypto_perc_logo_1hour);
            ImageView CryptoOneDayLogo = cardView.findViewById(R.id.crypto_perc_logo_1day);
            ImageView CryptoOneWeekLogo = cardView.findViewById(R.id.crypto_perc_logo_1week);
            if (curr_perc_1hr_double >= 0) {
                CryptoOneHour.setTextColor(Color.GREEN);
                CryptoOneHourLogo.setImageResource(R.drawable.increase);
                CryptoOneHourLogo.invalidate();
            } else {
                CryptoOneHour.setTextColor(Color.RED);
                CryptoOneHourLogo.setImageResource(R.drawable.decrease);
                CryptoOneHourLogo.invalidate();

            }
            if (curr_perc_1day_double >= 0) {
                CryptoOneDay.setTextColor(Color.GREEN);
                CryptoOneDayLogo.setImageResource(R.drawable.increase);
                CryptoOneDayLogo.invalidate();
            } else {
                CryptoOneDay.setTextColor(Color.RED);
                CryptoOneDayLogo.setImageResource(R.drawable.decrease);
                CryptoOneDayLogo.invalidate();
            }
            if (curr_perc_1week_double >= 0) {
                CryptoOneWeek.setTextColor(Color.GREEN);
                CryptoOneWeekLogo.setImageResource(R.drawable.increase);
                CryptoOneWeekLogo.invalidate();
            } else {
                CryptoOneWeek.setTextColor(Color.RED);
                CryptoOneWeekLogo.setImageResource(R.drawable.decrease);
                CryptoOneWeekLogo.invalidate();
            }
            cryptoCardsLayout.addView(cardView);
        }
        final View cardViewEmpty = getLayoutInflater().inflate(R.layout.empty_card, null, false);
        cryptoCardsLayout.addView(cardViewEmpty);
        dialog.dismiss();
    }

}
