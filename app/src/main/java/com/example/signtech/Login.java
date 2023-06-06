package com.example.signtech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    EditText etLoginEmail;
    EditText etLoginPass;
    Button btnLogin;
    TextView tvRegisterHere;
    TextView tvForgotPassword;
    ImageView imgBackLogin;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[!@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etLoginEmail = (EditText) findViewById(R.id.etLoginEmail);
        etLoginPass = (EditText) findViewById(R.id.etLoginPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvRegisterHere = (TextView) findViewById(R.id.tvRegisterHere);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        imgBackLogin = (ImageView) findViewById(R.id.imgBackLogin);
        progressDialog = new ProgressDialog(Login.this);

        mAuth = FirebaseAuth.getInstance();
        onStart();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserLogin();
            }
        });
        tvRegisterHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Register.class));

            }
        });
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);

            }
        });

        imgBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login.super.onBackPressed();
            }
        });
    }

    private void UserLogin() {
        progressDialog.setMessage("loading");
        progressDialog.show();
        String email = etLoginEmail.getText().toString().trim();
        String pass = etLoginPass.getText().toString().trim();

        if (email.isEmpty()) {
            progressDialog.hide();
            etLoginEmail.setError("Email is required");
            etLoginEmail.requestFocus();
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            progressDialog.hide();
            etLoginEmail.setError("Provide a valid email");
            etLoginEmail.requestFocus();
        }

        if (pass.isEmpty()) {
            progressDialog.hide();
            etLoginPass.setError("password is required");
            etLoginPass.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(Login.this, Home.class);
                        startActivity(intent);
                        finish();

                        Toast.makeText(Login.this, "Login successfully", Toast.LENGTH_LONG).show();
                    } else {
                        progressDialog.hide();
                        Toast.makeText(Login.this, "Unable to login ", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        if (pass.length() < 8 ) {
            progressDialog.hide();
            etLoginPass.setError("Password too weak, please enter atleast 8 characters, 1 special character with no spaces");
            etLoginPass.requestFocus();
        }

    }

    @Override
    protected void onStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkChangeListener, filter);
            super.onStart();
        }
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(networkChangeListener);
            super.onStop();

        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    protected void onDestroy() {
        super.onDestroy();
        onStop();

    }

}