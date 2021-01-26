package com.example.ewallet;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private ImageView QRImageView;
    private TextView address_view;
    private Button button_copy;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestFragment newInstance(String param1, String param2) {
        RequestFragment fragment = new RequestFragment();
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

        View v = inflater.inflate(R.layout.request, container, false);
        QRImageView = v.findViewById(R.id.Qr_image);
        address_view = v.findViewById(R.id.address_view);
        button_copy = v.findViewById(R.id.copy_button);
        button_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipBoard();
            }
        });
        Spinner spinner_choice = v.findViewById(R.id.choice_spinner);
        String []adapterStrings=new String[HomeFragment.SupportedCurrencies.size()];
        for(int i=0;i<HomeFragment.SupportedCurrencies.size();i++)
            adapterStrings[i]=HomeFragment.SupportedCurrencies.get(i).getCurrencyName();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,adapterStrings); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_choice.setAdapter(spinnerArrayAdapter);
        spinner_choice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ((TextView) parentView.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) parentView.getChildAt(0)).setTextSize(25);
                String selected_currency = ((TextView) parentView.getChildAt(0)).getText().toString();
                loadAddress(selected_currency);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        loadAddress(spinner_choice.getAdapter().getItem(0).toString());
        return v;
    }

    private void loadAddress(String WalletName) {
        try {
            new GetWalletAddress().execute(WalletName).get();
        } catch (Exception e) {

        }
    }

    public void generateQR() {
        float dip = 300f;
        Resources r = getResources();
        Bitmap bitmap;
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        String wallet_address = address_view.getText().toString();
        QRGEncoder qrgEncoder = new QRGEncoder(wallet_address, null, QRGContents.Type.TEXT, (int) px);
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            QRImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void copyToClipBoard() {
        ClipboardManager clipboard = (ClipboardManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CLIPBOARD_SERVICE);
        String wallet_address = address_view.getText().toString();
        ClipData clip = ClipData.newPlainText("Copied Text", wallet_address);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getActivity(), "Address Copied",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class GetWalletAddress extends AsyncTask<String, String, String> {
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
                url = new URL("http://10.0.2.2/cryptoBank/public/WalletController/getWalletAddress");
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
                    Log.v("SESS_ID", session_id);
                }
                //append parameters to url so that the script uses them
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("wallet_name", params[0])//params[0] is the message from AsyncLogin().execute(help_message);
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
                    return result.toString();
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
                    return "unsuccessful";
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
                JSONObject jsonResponse = new JSONObject(result);
                int success = jsonResponse.getInt("success");
                String wallet_address = "";
                if (success == 1) {
                    wallet_address = jsonResponse.getString("wallet_address");
                }
                address_view.setText(wallet_address);
                generateQR();

            } catch (JSONException j) {

            }
        }
    }
}
