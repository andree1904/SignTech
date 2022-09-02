package com.example.signtech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfile extends AppCompatActivity {

    EditText etEditName;
    EditText etEditEmail;
    EditText etEditPhone;
    Button btnUpdate;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private FirebaseAuth mAuth;

String name,email,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etEditName = (EditText) findViewById(R.id.etEditName);
        etEditEmail = (EditText) findViewById(R.id.etEditEmail);
        etEditPhone = (EditText) findViewById(R.id.etEditPhone);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);


        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        showData();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Update();

            }
        });
    }

    private void showData() {
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



    }
public void Update() {
        if (NameChange() || EmailChange() || PhoneChange()) {
            Toast.makeText(EditProfile.this,"Data is updated",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(EditProfile.this,"It is the same",Toast.LENGTH_LONG).show();
        }

}

    private boolean NameChange() {
        if (!name.equals(etEditName.getText().toString())) {
            reference.child(userID).child("name").setValue(etEditName.getText().toString());
            Intent intent = new Intent(EditProfile.this,Settings.class);
            startActivity(intent);
            finish();
            return true;
        } else {
            return false;
        }
    }
    private boolean EmailChange() {
        if (!email.equals((etEditEmail.getText().toString()))) {
            reference.child(userID).child("email").setValue(etEditEmail.getText().toString());
            user.updateEmail(etEditEmail.getText().toString());
            Intent intent = new Intent(EditProfile.this,Settings.class);
            startActivity(intent);
            finish();
            return true;
        } else {
            return false;
        }
    }

    private boolean PhoneChange() {
        if (!phone.equals((etEditPhone.getText().toString()))) {
            reference.child(userID).child("phone").setValue(etEditPhone.getText().toString());
            Intent intent = new Intent(EditProfile.this,Settings.class);
            startActivity(intent);
            finish();
            return true;
        } else {
            return false;
        }
    }
}