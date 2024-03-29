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

import com.google.firebase.FirebaseException;
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

public class EditProfile extends AppCompatActivity {

    EditText etEditName;
    EditText etEditEmail;
    EditText etEditPhone;
    Button btnUpdate;
    AlertDialog.Builder builder;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

String name,email,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etEditName = (EditText) findViewById(R.id.etEditName);
        etEditEmail = (EditText) findViewById(R.id.etEditEmail);
        etEditPhone = (EditText) findViewById(R.id.etEditPhone);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        builder = new AlertDialog.Builder(this);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        progressDialog = new ProgressDialog(EditProfile.this);

        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userEdit = snapshot.getValue(User.class);
                if (userEdit != null) {
                    name = userEdit.name;
                    email = userEdit.email;
                    phone = userEdit.phone;

                    etEditName.setText(name);
                    etEditEmail.setText(email);
                    etEditPhone.setText(phone);

                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfile.this,"Something went wrong", Toast.LENGTH_LONG).show();
            }
        });



        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Warning!")
                        .setMessage("Do you want to change your account information?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AccountChange();

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
        });
    }

    private boolean updateAccount( String name,String email, String phone) {


        User userUpdate = new User(name,email,phone);

        reference.child(userID).setValue(userUpdate);
        Toast.makeText(getApplicationContext()," information is now updated",Toast.LENGTH_LONG).show();
        return true;
    }

    private void AccountChange() {
        progressDialog.setMessage("loading");
        progressDialog.show();
        String changeName = etEditName.getText().toString().trim();
        String changeEmail = etEditEmail.getText().toString().trim();
        String changePhone = etEditPhone.getText().toString().trim();

        updateAccount(changeName, changeEmail, changePhone);
        user.updateEmail(changeEmail);

        if (!changePhone.equals(phone)) {
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    progressDialog.hide();
                    Toast.makeText(EditProfile.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {

                    Toast.makeText(EditProfile.this,"OTP is successfully send", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(EditProfile.this, Update_OTP_Verification.class);
                    intent.putExtra("phone", phone);
                    intent.putExtra("verificationId",verificationId);
                    startActivity(intent);
                    finish();
                }
            };
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber("+63" + changePhone)
                            .setTimeout(0l, TimeUnit.SECONDS)
                            .setActivity(this)
                            .setCallbacks(mCallbacks)
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }

        else {
            Toast.makeText(EditProfile.this,"Phone number already verified",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(EditProfile.this,Profile.class);
            startActivity(intent);
            progressDialog.hide();
            finish();
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