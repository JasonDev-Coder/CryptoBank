package com.example.ewallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageButton button_scan;
    static EditText send_address;
    private Spinner spinner_choice;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText cryptoInput, usdInput;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Button sendButton;

    public SendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SendFragment newInstance(String param1, String param2) {
        SendFragment fragment = new SendFragment();
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
        View v = inflater.inflate(R.layout.send, container, false);
        spinner_choice = v.findViewById(R.id.currrency_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.currencies_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_choice.setAdapter(adapter);
        spinner_choice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ((TextView) parentView.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) parentView.getChildAt(0)).setTextSize(25);
                Convert(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        send_address = v.findViewById(R.id.address_edit_field);
        button_scan = (ImageButton) v.findViewById(R.id.qr_button);
        cryptoInput = v.findViewById(R.id.input_amount_crypto);
        usdInput = v.findViewById(R.id.input_amount_usd);
        cryptoInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!usdInput.isFocused())
                    Convert(true);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        usdInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!cryptoInput.isFocused())
                    Convert(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        button_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), QrScanner.class);
                startActivity(i);
            }

        });
        sendButton = v.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMoney();
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getActivity();
        if (resultCode == Activity.RESULT_OK) {
            if (data.hasExtra("QRaddress")) {
                send_address.setText(data.getExtras().getString("QRaddress"));
            }
        }
    }

    private void sendMoney() {
        String walletName = (String) spinner_choice.getSelectedItem();//depending on the wallet name we will query the wallet type
        Log.v("WALLETNAME", walletName);
        String amount_send;
        try {
            amount_send = Double.toString(Double.parseDouble(cryptoInput.getText().toString()));//get the send amount parse it tp double to make sure only numbers are entered then parse it string to send it in post
        } catch (NumberFormatException e1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Error in send amount");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        String sendAddress = send_address.getText().toString();//get the address to send to
        if (sendAddress.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Input an address");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else
            new AsyncSend().execute(walletName, amount_send, sendAddress);
    }

    private void ChangePrice(final int index, final boolean CrypToUs) {
        Request request = new Request.Builder().url(CONSTANTS.MARKET_UPDATES_URL).build();
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
                        parseBpiResponse(body, index, CrypToUs);
                    }
                });
            }
        });
    }

    private void parseBpiResponse(String body, int currencyIndex, boolean CrypToUs) {
        try {
            JSONObject jsonObject = new JSONObject(body);
            JSONArray cryptos = jsonObject.getJSONArray("data");
            JSONObject crypto_info = cryptos.getJSONObject(currencyIndex);
            double crypto_price = crypto_info.getJSONObject("quote").getJSONObject("USD").getDouble("price");
            double value = 0;
            if (CrypToUs) {
                try {
                    value = Double.parseDouble(cryptoInput.getText().toString());
                } catch (Exception e) {
                    usdInput.setText("");
                    return;
                }
                double usd = crypto_price * value;
                usdInput.setText(Double.toString(usd));
            } else {
                try {
                    value = Double.parseDouble(usdInput.getText().toString());
                } catch (Exception e) {
                    cryptoInput.setText("");
                    return;
                }
                double crypto = value / crypto_price;
                DecimalFormat df = new DecimalFormat("#");
                df.setMaximumFractionDigits(8);
                cryptoInput.setText(String.format("%.8f", crypto));
            }
        } catch (Exception e) {
        }
    }

    private void Convert(boolean cryoToUs) {
        String Cryp_String_spinner = (String) spinner_choice.getSelectedItem();
        switch (Cryp_String_spinner) {
            case "Bitcoin":
                ChangePrice(CONSTANTS.BITCOIN_INDEX_JSON, cryoToUs);
                break;
            case "Etherum":
                ChangePrice(CONSTANTS.ETHERUM_INDEX_JSON, cryoToUs);
                break;
            case "USD-T":
                ChangePrice(CONSTANTS.TETHER_INDEX_JSON, cryoToUs);
                break;
            case "XRP":
                ChangePrice(CONSTANTS.XRP_INDEX_JSON, cryoToUs);
            case "Litecoin":
                ChangePrice(CONSTANTS.LITECOIN_INDEX_JSON, cryoToUs);
                break;
        }
    }


    private class AsyncSend extends AsyncTask<String, String, String> {
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
                url = new URL("http://10.0.2.2/cryptoBank/public/WalletController/sendMoney");
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
                        .appendQueryParameter("wallet_name", params[0])//params[0] is the wallet name from AsyncLogin().execute();
                        .appendQueryParameter("amount", params[1])//amount to send
                        .appendQueryParameter("recv_addr", params[2])//receiver address
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
                        JSONObject jsonResponse = new JSONObject(result.toString());
                        return jsonResponse.getString("error_type");//result will be used in onPostExecute method
                    }catch (JSONException j1){
                        Log.d("JSONresponse",Arrays.toString(j1.getStackTrace()));
                        return "false";
                    }
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
                builder.setMessage("Transaction is on the way!");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                send_address.setText("");
                cryptoInput.setText("");
            } else if (result.equalsIgnoreCase("Insufficent balance")) {
                builder.setMessage("Insufficient Balance");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            } else if (result.equalsIgnoreCase("You don't have the current wallet")) {
                builder.setMessage("You don't have the current wallet");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (result.equalsIgnoreCase("Receiver Wallet doesn't exist")) {
                builder.setMessage("Receiver Wallet doesn't exist");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else{
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
}
