package com.example.ewallet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class SignUp extends AppCompatActivity {
    EditText name, username, email, password, birthdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        name = findViewById(R.id.name_form);
        username = findViewById(R.id.username_form);
        email = findViewById(R.id.email_form);
        password = findViewById(R.id.password_form);
        birthdate = findViewById(R.id.datebirth_form);

    }

    public void SignUp(View v) {
        final String name_str = name.getText().toString();
        final String username_str = username.getText().toString();
        final String email_str = email.getText().toString();
        final String password_str = password.getText().toString();
        final String birthdate_str = birthdate.getText().toString();
        if(name_str.isEmpty() || username_str.isEmpty() || email_str.isEmpty() || password_str.isEmpty() || birthdate_str.isEmpty()){
            Toast.makeText(SignUp.this,"Missing Field",Toast.LENGTH_LONG).show();
            return;
        }
        new AsyncLogin().execute(name_str, username_str, email_str, password_str, birthdate_str);
    }

    private class AsyncLogin extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(SignUp.this);
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
                url = new URL("http://10.0.2.2/WalletAppScripts/signup.inc.php");
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
                        .appendQueryParameter("name", params[0])//params[0] is the email from AsyncLogin().execute(email,password);
                        .appendQueryParameter("username", params[1])//params[1] is the password AsyncLogin().execute(email,password);
                        .appendQueryParameter("email", params[2])
                        .appendQueryParameter("password", params[3])
                        .appendQueryParameter("birthdate", params[4]);
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

            if (result.equalsIgnoreCase("true")) {//if the php echoed true meaning the query from the table user gave a result meaning the user exist and can sign in
                Intent intent = new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);
                SignUp.this.finish();

            } else if (result.equalsIgnoreCase("Missing Field")) {
                //If field is missing
                Toast.makeText(SignUp.this, "Missing Field", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("Already Exists")) {
                Toast.makeText(SignUp.this, "Email already used", Toast.LENGTH_LONG).show();
            }else if(result.equalsIgnoreCase("Invalid Email")){
                Toast.makeText(SignUp.this, "Invalid Email", Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("exception")) {
                Toast.makeText(SignUp.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
            }
        }

    }

}