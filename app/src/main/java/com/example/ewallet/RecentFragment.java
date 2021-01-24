package com.example.ewallet;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TreeSet;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView transaction_type;
    private TextView date, transaction_amount, transaction_amount_usd, transaction_date, transaction_time;
    private TreeSet<TransactionModel> Alltransactions = new TreeSet<>();
    private LinearLayout transactionsView;

    public RecentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecentFragment newInstance(String param1, String param2) {
        RecentFragment fragment = new RecentFragment();
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
        View v = inflater.inflate(R.layout.recent_layout, container, false);
        Date c = Calendar.getInstance().getTime();

        transactionsView = v.findViewById(R.id.transactions_layout);
        loadTransactions();
        return v;
    }

    public void loadTransactions() {
        try {
            new GetRecents().execute().get();
            Log.d("lenmap", Integer.toString(Alltransactions.size()));
            for (final TransactionModel model : Alltransactions) {
                final View cardView = getLayoutInflater().inflate(R.layout.transaction_card, null, false);
                ImageView transaction_logo = cardView.findViewById(R.id.transaction_type_img);
                if (model.getType() == TransactionModel.typeTransaction.SEND) {
                    transaction_logo.setImageResource(R.drawable.send);
                    transaction_logo.setColorFilter(getResources().getColor(R.color.green));
                } else transaction_logo.setImageResource(R.drawable.receive);
                TextView crypto_amount = cardView.findViewById(R.id.transaction_amount_value);
                model.setAmount_crypto(model.getAmount_crypto().setScale(2, BigDecimal.ROUND_DOWN));
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(10);

                df.setMinimumFractionDigits(3);

                df.setGroupingUsed(false);

                String result = df.format(model.getAmount_crypto());
                crypto_amount.setText(result);
                TextView crypto_Type = cardView.findViewById(R.id.transaction_type);
                crypto_Type.setText(model.getType_symbol());
                TextView usd_amount = cardView.findViewById(R.id.transaction_amount_usd);
                NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
                usd_amount.setText("US" + defaultFormat.format(model.getAmount_us()));
                TextView dateTransac = cardView.findViewById(R.id.transaction_date);
                Date date = model.getDate();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = dateFormat.format(date);
                String[] dateTime = strDate.split(" ");
                dateTransac.setText(dateTime[0]);
                TextView timeTransac = cardView.findViewById(R.id.transaction_time);
                timeTransac.setText(dateTime[1]);

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TransactionBottomSheet bottomSheet = new TransactionBottomSheet(model.getWallet_addr_sender(), model.getWallet_addr_receiver());
                        bottomSheet.show(getFragmentManager(), "transactionSheet");
                    }
                });
                transactionsView.addView(cardView);
            }

        } catch (Exception e) {
            Log.v("ExceptionLoad", Arrays.toString(e.getStackTrace()));
        }
    }

    private class GetRecents extends AsyncTask<String, String, String> {
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
                url = new URL("http://10.0.2.2/cryptoBank/public/TransactionController/getTransactions");
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
                    Log.d("JSONresponse", result.toString());
                    try {
                        Alltransactions.clear();
                        JSONObject jsonResponse = new JSONObject(result.toString());
                        JSONObject jsonSent = jsonResponse.getJSONObject("send");
                        JSONArray jsonSentTransactions = jsonSent.getJSONArray("transactions");
                        for (int i = 0; i < jsonSentTransactions.length(); i++) {
                            JSONObject sentTransac = jsonSentTransactions.getJSONObject(i);
                            TransactionModel model = new TransactionModel();
                            model.setWallet_addr_sender(sentTransac.getString("wallet_address_sender"));
                            model.setWallet_addr_receiver(sentTransac.getString("wallet_address_receiver"));
                            model.setAmount_crypto(new BigDecimal(sentTransac.getDouble("amount_crypto")));
                            model.setAmount_us(sentTransac.getDouble("amount_us"));
                            Log.v("dateJSon", sentTransac.getString("date"));
                            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sentTransac.getString("date"));
                            model.setDate(date);
                            model.setType_symbol(sentTransac.getString("type_symbol"));
                            TransactionModel.typeTransaction typeTransaction = TransactionModel.typeTransaction.SEND;
                            model.setType(typeTransaction);
                            Alltransactions.add(model);
                        }
                        JSONObject jsonReceive = jsonResponse.getJSONObject("receive");
                        JSONArray jsonReceiveTransactions = jsonReceive.getJSONArray("transactions");
                        for (int i = 0; i < jsonReceiveTransactions.length(); i++) {
                            JSONObject sentTransac = jsonReceiveTransactions.getJSONObject(i);
                            TransactionModel model = new TransactionModel();
                            model.setWallet_addr_sender(sentTransac.getString("wallet_address_sender"));
                            model.setWallet_addr_receiver(sentTransac.getString("wallet_address_receiver"));
                            model.setAmount_crypto(new BigDecimal(sentTransac.getDouble("amount_crypto")));
                            model.setAmount_us(sentTransac.getDouble("amount_us"));
                            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sentTransac.getString("date"));
                            model.setDate(date);
                            model.setType_symbol(sentTransac.getString("type_symbol"));
                            TransactionModel.typeTransaction typeTransaction = TransactionModel.typeTransaction.RECEIVE;
                            model.setType(typeTransaction);
                            Alltransactions.add(model);
                        }
                    } catch (JSONException j1) {
                        Log.d("JsonResponseeee", Arrays.toString(j1.getStackTrace()));
                    } catch (ParseException e) {
                        Log.d("JsonResponseeee", Arrays.toString(e.getStackTrace()));
                        e.printStackTrace();
                    }
                    return null;
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
            pdLoading.cancel();
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