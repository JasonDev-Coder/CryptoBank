package com.example.ewallet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Arrays;

public class SignIn extends AppCompatActivity {
    private EditText email_in;
    private EditText password_in;
    private TextView ClickToSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        email_in = findViewById(R.id.email);
        password_in = findViewById(R.id.password);
        ClickToSignUp = findViewById(R.id.sign_up_link);
        ClickToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    public void checkLogin(View v) {//Trigger
        final String email = email_in.getText().toString();
        final String password = password_in.getText().toString();
        new AsyncLogin().execute(email, password);
    }


    private class AsyncLogin extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(SignIn.this);
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
                url = new URL("http://10.0.2.2/cryptoBank/public/UserController/LogIn");
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
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", params[0])//params[0] is the email from AsyncLogin().execute(email,password);
                        .appendQueryParameter("password", params[1]);//params[1] is the password AsyncLogin().execute(email,password);
                //strings username and password must correspond with the written php code(in my case i used $_POST["username"]
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
                Log.d("HOSTINGG", e1.getMessage());
                Log.d("HOSTING", Arrays.toString(e1.getStackTrace()));
                Log.d("HOSTING", "OK");
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
                } else
                    return "unsuccessful";
            } catch (IOException e) {
                e.printStackTrace();
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
                Log.v("JSONresponse",result);
                String error_type=jsonResponse.getString("error_type");
                if (error_type.equalsIgnoreCase("true")){//if the php echoed true meaning the query from the table user gave a result meaning the user exist and can sign in
                    String session_id = jsonResponse.getString("session_id");
                    SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = saved_values.edit();
                    editor.putString("session_id", session_id);/*In the php script if the log in is successful we echo the session id to store it in android because the session is being closed after finishing executing the script in android only*/
                    editor.commit();
                    Log.d("sessionid_id", session_id);
                    Intent intent = new Intent(SignIn.this, MainActivity.class);
                    startActivity(intent);
                    SignIn.this.finish();

                } else if (error_type.equalsIgnoreCase("false")) {
                    // If username and password does not match display a error message
                    Toast.makeText(SignIn.this, "Invalid email or password", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException j1) {
                Log.d("JsonResponse", Arrays.toString(j1.getStackTrace()));
                j1.printStackTrace();
                Toast.makeText(SignIn.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
            }
        }

    }
}