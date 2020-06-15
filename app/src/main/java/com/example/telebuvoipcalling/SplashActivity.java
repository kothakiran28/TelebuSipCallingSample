package com.example.telebuvoipcalling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SplashActivity extends AppCompatActivity {

    EditText edtMobileNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button call =findViewById(R.id.btnRegister);
        edtMobileNumber=findViewById(R.id.edtMobileNumber);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String username = prefs.getString("namePref", "");
        final SharedPreferences.Editor editor=prefs.edit();
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtMobileNumber.getText().toString().trim().length()==0){
                    edtMobileNumber.setError("Please enter mobile number");
                }else {
                    editor.putString("namePref",edtMobileNumber.getText().toString().trim());
                    editor.putString("domainPref","202.65.140.55:5070");
                    editor.putString("passPref","1234abcd");
                    editor.apply();
                    Intent i =new Intent(SplashActivity.this, Registeration.class);
                    startActivity(i);
                    finish();
                }
            }
        });
        Log.e("username",username+"..........");
        if(username.trim().length()>0){
            Intent i =new Intent(SplashActivity.this, Registeration.class);
            startActivity(i);
            finish();
        }

    }

}
