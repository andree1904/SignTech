package com.example.signtech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class OTP_Verification extends AppCompatActivity {
    EditText etOTPCode;
    TextView tvResendBtn;
    TextView tvTimer;
    Button btnVerify;


    private String verificationId;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        etOTPCode = findViewById(R.id.etOTPCode);

        tvResendBtn = (TextView) findViewById(R.id.tvResendBtn);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        tvTimer = (TextView) findViewById(R.id.tvTimer);



        verificationId = getIntent().getStringExtra("verificationId");
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(OTP_Verification.this);
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        editTextInput();
        tvResendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+63" + getIntent().getStringExtra("phone"),
                        0,
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
                startTimer(60 * 1000,1000);
                tvResendBtn.setEnabled(false);

            }
        });


        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify();
            }
        });

    }




    public void verify() {
        String OTPCode = etOTPCode.getText().toString().trim();
        progressDialog.setMessage("loading");
        progressDialog.show();
        if (OTPCode.isEmpty()) {
            progressDialog.hide();
            Toast.makeText(OTP_Verification.this,"Otp is not valid",Toast.LENGTH_LONG).show();

        }
        else {
            if (verificationId != null) {
                String VerifyCode = OTPCode;

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,VerifyCode);
                user.updatePhoneNumber(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(OTP_Verification.this,"phone number verified",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(OTP_Verification.this,RegisterDone.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            progressDialog.hide();
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
        private void startTimer(final long finish, long tick){

        tvTimer.setVisibility(View.VISIBLE);
            CountDownTimer countDownTimer;

            countDownTimer = new CountDownTimer(finish,tick) {
                @Override
                public void onTick(long l) {
                    long remindSec = l / 1000;
                    tvTimer.setText("Resend OTP in: " + (remindSec / 60) + ":" + (remindSec % 60));

                }

                @Override
                public void onFinish() {
                    tvResendBtn.setEnabled(true);
                    tvTimer.setVisibility(View.INVISIBLE);
                }
            }.start();
        }
}