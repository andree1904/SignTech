package com.example.signtech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterDone extends AppCompatActivity {

    Button btnLoginPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_done);

        btnLoginPage = (Button) findViewById(R.id.btnLoginPage);

        btnLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterDone.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

    }
}