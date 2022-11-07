package com.example.signtech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.signtech.R.drawable.custom_button10;
import static com.example.signtech.R.drawable.custom_button9;

public class Profile extends AppCompatActivity {

    TextView tvVerified;
    TextView tvWelcome;
    TextView tvPhone;
    TextView tvEmail;
    TextView tvEditProfile;
    TextView tvChangePass;
    Button btnDelete;
    Button btnDeleteAccount;
    TextView btnLogout;
    Button btnCancel;
    EditText etDeleteAccountPass;

    AlertDialog.Builder builder;


    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvVerified = (TextView) findViewById(R.id.tvVerified);
        btnLogout = (TextView) findViewById(R.id.btnLogout);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        tvEditProfile = (TextView) findViewById(R.id.tvEditProfile);
        tvChangePass = (TextView) findViewById(R.id.tvChangePass);

        builder = new AlertDialog.Builder(this);
        progressDialog = new ProgressDialog(Profile.this);

        EmailVerified();
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this,EditProfile.class);
                startActivity(intent);
                finish();
            }
        });

        tvChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this,Change_Password.class);
                startActivity(intent);
                finish();
            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            DeleteAccountDialog();

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Logout!")
                        .setMessage("Do you want to Logout?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAuth.signOut();
                                Intent intent = new Intent(Profile.this,Authentication_Choice.class);
                                startActivity(intent);
                                finish();
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

        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userWelcome = snapshot.getValue(User.class);
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (userWelcome != null) {
                    String name = userWelcome.name;
                    String email = userWelcome.email;
                    String phone = userWelcome.phone;

                    tvWelcome.setText(name);
                    tvEmail.setText(email);
                    tvPhone.setText("+63-"+phone);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this,"Something went wrong", Toast.LENGTH_LONG).show();
            }
        });



    }


    private void EmailVerified() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            if (user.isEmailVerified()) {
                tvVerified.setText("Email Verified");
                tvVerified.setBackground(ContextCompat.getDrawable(this, custom_button10));
                tvVerified.setTextColor(Color.BLACK);
            } else {
                tvVerified.setText("Email not Verified (Click to verify)");
                tvVerified.setBackground(ContextCompat.getDrawable(this, custom_button9));
                tvVerified.setTextColor(Color.WHITE);
                tvVerified.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.setMessage("loading");
                        progressDialog.show();
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.hide();
                                    Toast.makeText(Profile.this, "Email verification sent", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        }

    }
    private void DeleteAccount() {
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Users")
                .child(user.getUid())
                .setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    finish();
                                    Toast.makeText(Profile.this, "Successfully Deleted User Account", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
        }

        void DeleteAccountDialog() {
        final Dialog dialog = new Dialog(Profile.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.delete_account_layout);


         etDeleteAccountPass = dialog.findViewById(R.id.etDeleteAccountPass);
         btnDeleteAccount = dialog.findViewById(R.id.btnDeleteAccount);
         btnCancel = dialog.findViewById(R.id.btnCancel);
        etDeleteAccountPass.addTextChangedListener(deleteTextWatcher);

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setCancelable(false);
        }
        private TextWatcher deleteTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String pass = etDeleteAccountPass.getText().toString().trim();
            btnDeleteAccount.setEnabled(!pass.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void Delete() {
        String pass = etDeleteAccountPass.getText().toString().trim();
        builder.setTitle("Warning!")
                .setMessage("Do you want to delete your account?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),pass);
                        user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.setMessage("loading");
                                progressDialog.show();

                                DeleteAccount();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.hide();
                                        Toast.makeText(Profile.this,""+ e.getMessage(),Toast.LENGTH_LONG).show();
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

}