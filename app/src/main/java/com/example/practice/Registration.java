package com.example.practice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends Activity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private EditText emailRegister;
    private EditText passwordRegister;
    private EditText repeatPasswordRegister;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        emailRegister = findViewById(R.id.editTextTextEmailAddress);
        passwordRegister = findViewById(R.id.editTextTextPassword);
        repeatPasswordRegister = findViewById(R.id.editTextTextPassword2);
        registerButton = findViewById(R.id.button);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidationPassed()){
                    mAuth.createUserWithEmailAndPassword(emailRegister.getText().toString(), passwordRegister.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        reference.child("Users").child(mAuth.getCurrentUser().getUid()).child("email").setValue(emailRegister.getText().toString());
                                        startActivity(new Intent(Registration.this, MainActivity.class));
                                    }
                                    else if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                        Toast.makeText(Registration.this, "user already exists", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(Registration.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private boolean isValidEmail(CharSequence target) {
        return (Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isValidationPassed(){
        if (emailRegister.getText().toString().isEmpty()){
            Toast.makeText(Registration.this, "email cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (passwordRegister.getText().toString().isEmpty()){
            Toast.makeText(Registration.this, "password cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (passwordRegister.getText().toString().length() < 6){
            Toast.makeText(Registration.this, "password is too short", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (repeatPasswordRegister.getText().toString().isEmpty()){
            Toast.makeText(Registration.this, "repeat password cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!passwordRegister.getText().toString().equals(passwordRegister.getText().toString())){
            Toast.makeText(Registration.this, "passwords must be equal", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidEmail(emailRegister.getText().toString())){
            Toast.makeText(Registration.this, "email is not valid", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
