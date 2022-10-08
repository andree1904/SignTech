package com.example.signtech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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

public class Change_Password extends AppCompatActivity {

    EditText etCurrentPass;
    EditText etNewPass;
    EditText etConfirmNewPass;
    Button btnChangePass;
    ProgressBar progressBarChangePass;
    FirebaseAuth mAuth;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etCurrentPass = (EditText) findViewById(R.id.etCurrentPass);
        etNewPass = (EditText) findViewById(R.id.etNewPass);
        etConfirmNewPass = (EditText) findViewById(R.id.etConfirmNewPass);
        btnChangePass = (Button) findViewById(R.id.btnChangePass);
        progressBarChangePass = (ProgressBar) findViewById(R.id.progressBarChangePass);

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
            progressBarChangePass.setVisibility(View.INVISIBLE);
            btnChangePass.setVisibility(View.VISIBLE);
            etCurrentPass.setError("this is required");
        }

        if (newpass.length() < 8 ) {
            progressBarChangePass.setVisibility(View.INVISIBLE);
            btnChangePass.setVisibility(View.VISIBLE);
            etNewPass.setError("Password length must be 8 characters");
        }
        if (confirmNewPass.length() < 8) {
            progressBarChangePass.setVisibility(View.INVISIBLE);
            btnChangePass.setVisibility(View.VISIBLE);
            etConfirmNewPass.setError("Password length must be 8 characters");
        }

        if (newpass.equals(confirmNewPass)) {
            builder.setTitle("Warning!")
                    .setMessage("Do you want to change password?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progressBarChangePass.setVisibility(View.VISIBLE);
                            btnChangePass.setVisibility(View.INVISIBLE);
                            AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(),currentpass);
                            user.reauthenticate(authCredential)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            user.updatePassword(newpass)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(Change_Password.this,"Password Updated",Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(Change_Password.this,Profile.class);
                                                            startActivity(intent);
                                                            finish();

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressBarChangePass.setVisibility(View.INVISIBLE);
                                                            btnChangePass.setVisibility(View.VISIBLE);
                                                            Toast.makeText(Change_Password.this,""+ e.getMessage(),Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBarChangePass.setVisibility(View.INVISIBLE);
                                            btnChangePass.setVisibility(View.VISIBLE);
                                            Toast.makeText(Change_Password.this,""+ e.getMessage(),Toast.LENGTH_LONG).show();

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
            progressBarChangePass.setVisibility(View.INVISIBLE);
            btnChangePass.setVisibility(View.VISIBLE);
            Toast.makeText(Change_Password.this,"Password Unmatch",Toast.LENGTH_SHORT).show();
        }
    }

}