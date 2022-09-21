package com.example.signtech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Authentication_Choice extends AppCompatActivity {

    Button btnGotoLogin;
    Button btnGotoRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication_choice);


        btnGotoLogin = (Button) findViewById(R.id.btnGotoLogin);
        btnGotoRegister = (Button) findViewById(R.id.btnGotoRegister);


        btnGotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Authentication_Choice.this,Login.class);
                startActivity(intent);
                finish();

            }
        });

        btnGotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Authentication_Choice.this,Register.class);
                startActivity(intent);
                finish();
            }
        });

    }

}