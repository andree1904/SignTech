package com.example.signtech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTP_Verification extends AppCompatActivity {
    EditText etOTPCode;
    TextView tvMobile;
    TextView tvResendBtn;
    Button btnVerify;
    ProgressBar progressBarVerify;

    private String verificationId;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        etOTPCode = findViewById(R.id.etOTPCode);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvResendBtn = (TextView) findViewById(R.id.tvResendBtn);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        progressBarVerify = (ProgressBar) findViewById(R.id.progressBarVerify);

        verificationId = getIntent().getStringExtra("verificationId");
        mAuth = FirebaseAuth.getInstance();
        User user = new User();
        tvMobile.setText(String.format("+63-%s", getIntent().getStringExtra("phone")));

        editTextInput();
        tvResendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+63" + getIntent().getStringExtra("phone"),
                        60l,
                        TimeUnit.SECONDS,
                        OTP_Verification.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                            }
                            @Override
                            public void onCodeSent(@NonNull String newverificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                verificationId = newverificationId;
                                Toast.makeText(OTP_Verification.this,"OTP send", Toast.LENGTH_LONG).show();

                            }
                        }
                );

            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBarVerify.setVisibility(View.VISIBLE);
                btnVerify.setVisibility(View.INVISIBLE);
                verify();
            }
        });

    }




    public void verify() {
        String OTPCode = etOTPCode.getText().toString().trim();


        if (OTPCode.isEmpty()) {
            Toast.makeText(OTP_Verification.this,"Otp is not valid",Toast.LENGTH_LONG).show();

        }
        else {
            if (verificationId != null) {
                String VerifyCode = OTPCode;

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,VerifyCode);
                mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBarVerify.setVisibility(View.VISIBLE);
                            btnVerify.setVisibility(View.INVISIBLE);
                            Toast.makeText(OTP_Verification.this,"phone number verified",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(OTP_Verification.this,RegisterDone.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            progressBarVerify.setVisibility(View.INVISIBLE);
                            btnVerify.setVisibility(View.VISIBLE);
                            Toast.makeText(OTP_Verification.this,"Invalid OTP", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        }
    }

    private void editTextInput() {
        etOTPCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etOTPCode.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

}