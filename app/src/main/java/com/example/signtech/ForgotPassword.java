package com.example.signtech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    private EditText etEmailAddress;
    private Button btnResetPass;
    private ProgressDialog progressDialog;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmailAddress = (EditText) findViewById(R.id.etEmailAddress);
        btnResetPass = (Button) findViewById(R.id.btnResetPass);
        progressDialog = new ProgressDialog(ForgotPassword.this);

        mAuth = FirebaseAuth.getInstance();


        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPass();

            }
        });

    }

    private void resetPass() {
        progressDialog.setMessage("loading");
        progressDialog.show();
        String email = etEmailAddress.getText().toString().trim();


        if (email.isEmpty()){
            progressDialog.hide();
            etEmailAddress.setError("Email is required");
            etEmailAddress.requestFocus();
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            progressDialog.hide();
            etEmailAddress.setError("Enter Valid Email");
            etEmailAddress.requestFocus();
        }


        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(ForgotPassword.this,"The reset password will be send to your email address",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ForgotPassword.this,Login.class);
                    startActivity(intent);
                    finish();

                } else {
                    progressDialog.hide();
                    Toast.makeText(ForgotPassword.this,"There is something wrong",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}