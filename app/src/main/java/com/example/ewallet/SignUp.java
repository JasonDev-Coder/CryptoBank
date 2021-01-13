package com.example.ewallet;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {
    EditText name,username,email,password,birthdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        name=findViewById(R.id.name_form);
        username=findViewById(R.id.username_form);
        email=findViewById(R.id.email_form);
        password=findViewById(R.id.password_form);
        birthdate=findViewById(R.id.datebirth_form);

    }
    public void SignUp(View v){

    }

}
