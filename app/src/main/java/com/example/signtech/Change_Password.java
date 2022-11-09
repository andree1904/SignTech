package com.example.signtech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class Change_Password extends AppCompatActivity {

    EditText etCurrentPass;
    EditText etNewPass;
    EditText etConfirmNewPass;
    Button btnChangePass;

    private ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    AlertDialog.Builder builder;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[!@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etCurrentPass = (EditText) findViewById(R.id.etCurrentPass);
        etNewPass = (EditText) findViewById(R.id.etNewPass);
        etConfirmNewPass = (EditText) findViewById(R.id.etConfirmNewPass);
        btnChangePass = (Button) findViewById(R.id.btnChangePass);
        progressDialog = new ProgressDialog(Change_Password.this);

        builder = new AlertDialog.Builder(this);
        mAuth = FirebaseAuth.getInstance();
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePassword();
            }
        });
    }



    public void ChangePassword() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String currentpass = etCurrentPass.getText().toString().trim();
        String newpass = etNewPass.getText().toString().trim();
        String confirmNewPass = etConfirmNewPass.getText().toString().trim();

        if (currentpass.isEmpty() && newpass.isEmpty() && confirmNewPass.isEmpty()) {
            progressDialog.hide();
            etCurrentPass.setError("this is required");
        }

            if (newpass.equals(confirmNewPass)){
                if (PASSWORD_PATTERN.matcher(newpass).matches() || PASSWORD_PATTERN.matcher(confirmNewPass).matches()) {
                    builder.setTitle("Warning!")
                        .setMessage("Do you want to change password?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog.setMessage("loading");
                                progressDialog.show();
                                AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), currentpass);
                                user.reauthenticate(authCredential)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                user.updatePassword(newpass)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(Change_Password.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(Change_Password.this, Profile.class);
                                                                startActivity(intent);
                                                                finish();

                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                progressDialog.hide();
                                                                Toast.makeText(Change_Password.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.hide();
                                                Toast.makeText(Change_Password.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();

                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            }
            else {
                    progressDialog.hide();
                    builder.setTitle("Error")
                            .setMessage("Password too weak, please enter atleast 8 characters, Upper and lowe cases, 1 special character with no spaces")
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            })
                            .show();

            }
        }
        else {
                progressDialog.hide();
                Toast.makeText(Change_Password.this, "Password Unmatch", Toast.LENGTH_SHORT).show();
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




