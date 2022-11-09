package com.example.signtech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Update_OTP_Verification extends AppCompatActivity {
    EditText etUpdateOTPCode;
    TextView tvUpdateResendBtn;
    TextView tvUpdateTimer;
    Button btnUpdateVerify;


    private String verificationId;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private ProgressDialog progressDialog;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_otp_verification);

        etUpdateOTPCode = (EditText) findViewById(R.id.etUpdateOTPCode);
        tvUpdateResendBtn = (TextView) findViewById(R.id.tvUpdateResendBtn);
        tvUpdateTimer = (TextView) findViewById(R.id.tvUpdateTimer);
        btnUpdateVerify = (Button) findViewById(R.id.btnUpdateVerify);

        verificationId = getIntent().getStringExtra("verificationId");
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(Update_OTP_Verification.this);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        editTextInput();
        tvUpdateResendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+63" + getIntent().getStringExtra("phone"),
                        0,
                        TimeUnit.SECONDS,
                        Update_OTP_Verification.this,

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

                                Toast.makeText(Update_OTP_Verification.this,"OTP send", Toast.LENGTH_LONG).show();

                            }
                        }
                );
                startTimer(60 * 1000,1000);
                tvUpdateResendBtn.setEnabled(false);

            }
        });


        btnUpdateVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify();
            }
        });

    }




    public void verify() {
        String OTPCode = etUpdateOTPCode.getText().toString().trim();
        progressDialog.setMessage("loading");
        progressDialog.show();
        if (OTPCode.isEmpty()) {
            progressDialog.hide();
            Toast.makeText(Update_OTP_Verification.this,"Otp is not valid",Toast.LENGTH_LONG).show();

        }
        else {
            if (verificationId != null) {
                String VerifyCode = OTPCode;

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,VerifyCode);
               user.updatePhoneNumber(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()) {
                           progressDialog.hide();
                           Intent intent = new Intent(Update_OTP_Verification.this,Profile.class);
                           startActivity(intent);
                           Toast.makeText(Update_OTP_Verification.this,"Successfully updated phone number",Toast.LENGTH_LONG).show();
                       }
                       else {
                           Toast.makeText(Update_OTP_Verification.this,"Invalid OTP",Toast.LENGTH_LONG).show();
                       }
                   }
               });

            }
        }
    }

    private void editTextInput() {
        etUpdateOTPCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etUpdateOTPCode.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }
    private void startTimer(final long finish, long tick){

        tvUpdateTimer.setVisibility(View.VISIBLE);
        CountDownTimer countDownTimer;

        countDownTimer = new CountDownTimer(finish,tick) {
            @Override
            public void onTick(long l) {
                long remindSec = l / 1000;
                tvUpdateTimer.setText("Resend OTP in: " + (remindSec / 60) + ":" + (remindSec % 60));

            }

            @Override
            public void onFinish() {
                tvUpdateResendBtn.setEnabled(true);
                tvUpdateTimer.setVisibility(View.INVISIBLE);
            }
        }.start();
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