package com.example.signtech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.Image;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    EditText etRegisterName;
    EditText etRegisterEmail;
    EditText etRegisterPhone;
    EditText etRegisterPass;
    EditText etRegisterConfirmPass;
    Button btnRegister;
    TextView tvLoginHere;
    ImageView imgBackRegister;
    CheckBox chkPrivacy;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private ProgressDialog progressDialog;
    AlertDialog.Builder builder;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[!@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$");

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String text ="I have read Data Privacy policy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etRegisterName = (EditText) findViewById(R.id.etRegisterName);
        etRegisterEmail = (EditText) findViewById(R.id.etRegisterEmail);
        etRegisterPhone = (EditText) findViewById(R.id.etRegisterPhone);
        etRegisterPass = (EditText) findViewById(R.id.etRegisterPass);
        etRegisterConfirmPass = (EditText) findViewById(R.id.etRegisterConfirmPass);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        tvLoginHere = (TextView) findViewById(R.id.tvLoginHere);
        imgBackRegister = (ImageView) findViewById(R.id.imgBackRegister);
        chkPrivacy = (CheckBox) findViewById(R.id.chkPrivacy);
        progressDialog = new ProgressDialog(Register.this);


        builder = new AlertDialog.Builder(this);
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                registerUser();
            }
        });

        tvLoginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        imgBackRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register.super.onBackPressed();
            }
        });

        SpannableString ss = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {

                final Dialog dialog = new Dialog(Register.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.terms_and_conditions);

                Button btnAccept = dialog.findViewById(R.id.btnAccept);
                Button btnDecline = dialog.findViewById(R.id.btnDecline);

                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chkPrivacy.setChecked(true);
                        dialog.dismiss();
                    }
                });

                btnDecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chkPrivacy.setChecked(false);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.setCancelable(true);
            }
        };
    ss.setSpan(clickableSpan,12,31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    chkPrivacy.setText(ss);
    chkPrivacy.setMovementMethod(LinkMovementMethod.getInstance());


    chkPrivacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if (b) {
                etRegisterName.setEnabled(true);
                etRegisterEmail.setEnabled(true);
                etRegisterPhone.setEnabled(true);
                etRegisterPass.setEnabled(true);
                etRegisterConfirmPass.setEnabled(true);
                btnRegister.setEnabled(true);
                }
            else {

                etRegisterName.setEnabled(false);
                etRegisterEmail.setEnabled(false);
                etRegisterPhone.setEnabled(false);
                etRegisterPass.setEnabled(false);
                etRegisterConfirmPass.setEnabled(false);
                btnRegister.setEnabled(false);


            }
            }


    });
    }

    private void registerUser() {
        progressDialog.setMessage("loading");
        progressDialog.show();
        String name = etRegisterName.getText().toString().trim();
        String email = etRegisterEmail.getText().toString().trim();
        String phone = etRegisterPhone.getText().toString().trim();
        String pass = etRegisterPass.getText().toString().trim();
        String confirmpass = etRegisterConfirmPass.getText().toString().trim();

        DatabaseReference ref = reference.child(userID).child("User Information");

        ref.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(Register.this,"Email Address is already taken", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Toast.makeText(Register.this,"There is something wrong", Toast.LENGTH_LONG).show();
            }
        });

        if(name.isEmpty()) {
           progressDialog.hide();
            btnRegister.setVisibility(View.VISIBLE);
            etRegisterName.setError("name is required");
            etRegisterName.requestFocus();
            return;

        }
        if(email.isEmpty()) {
           progressDialog.hide();
            etRegisterEmail.setError("email is required");
            etRegisterEmail.requestFocus();
            return;
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
          progressDialog.hide();
            etRegisterEmail.setError("Please provide a valid email");
            etRegisterEmail.requestFocus();
            return;

        }
        if (pass.isEmpty() && confirmpass.isEmpty()) {
           progressDialog.hide();
            etRegisterPass.setError("password is required");
            etRegisterPass.requestFocus();
            return;

        }

        if (pass.equals(confirmpass)) {
            if (PASSWORD_PATTERN.matcher(pass).matches() || PASSWORD_PATTERN.matcher(confirmpass).matches()) {
                if (phone.isEmpty()) {
                    progressDialog.hide();
                    etRegisterPhone.setError("phone Number is required");
                    etRegisterPhone.requestFocus();
                }
                else {
                    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            progressDialog.hide();
                            Toast.makeText(Register.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCodeSent(@NonNull String verificationId,
                                               @NonNull PhoneAuthProvider.ForceResendingToken token) {

                            Toast.makeText(Register.this,"OTP is successfully send", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Register.this, OTP_Verification.class);
                            intent.putExtra("phone", phone);
                            intent.putExtra("verificationId",verificationId);
                            startActivity(intent);
                            finish();
                        }
                    };
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber("+63" + phone)
                                    .setTimeout(0l, TimeUnit.SECONDS)
                                    .setActivity(this)
                                    .setCallbacks(mCallbacks)
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);

                    mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User user = new User(name,email,phone);

                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Register.this,"User has been registered successfully", Toast.LENGTH_SHORT).show();


                                        } else {
                                            progressDialog.hide();
                                            Toast.makeText(Register.this,"Failed to register try again", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            else {
                                progressDialog.hide();
                                Toast.makeText(Register.this,"Failed to register try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
            else {
                progressDialog.hide();
                builder.setTitle("Error")
                        .setMessage("Password too weak, please enter atleast 8 characters, 1 special character with no spaces")
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            }


        } else {
          progressDialog.hide();
            Toast.makeText(Register.this,"Password Unmatched", Toast.LENGTH_LONG).show();
        }
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
