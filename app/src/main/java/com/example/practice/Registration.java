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

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Registration extends Activity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private EditText emailRegister;
    private EditText passwordRegister;
    private EditText repeatPasswordRegister;
    private Button registerButton;

    private String Base_url;
    private Retrofit retrofit;
    private ServiseAPI service;

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

        Base_url = "http://127.0.0.1:8000/";

        retrofit = new Retrofit.Builder()
                .baseUrl(Base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ServiseAPI.class);

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

                                        //Часть с Docker сервером

                                        String newUserEmail = emailRegister.getText().toString();
                                        String newUserPassword = passwordRegister.getText().toString();

                                        User newUser = new User(newUserEmail, newUserPassword);

                                        Call<List<Tokens>> call = service.createNewUser(newUser);

                                        call.enqueue(new Callback<List<Tokens>>() {
                                            @Override
                                            public void onResponse(Call<List<Tokens>> call, Response<List<Tokens>> response) {
                                                if (response.isSuccessful()) {
                                                    List<Tokens> data = response.body();
                                                    Tokens tokens = data.get(0);

                                                    System.out.println(tokens.access);
                                                    System.out.println(tokens.refresh);

                                                    File pathToStorage = getApplicationContext().getFilesDir();

                                                    tokens.writeAccess(pathToStorage);
                                                    tokens.writeRefresh(pathToStorage);
                                                } else {
                                                    System.out.println("error1!");
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<List<Tokens>> call, Throwable t) {
                                                System.out.println("error2");
                                                System.out.println(t.toString());
                                            }
                                        });

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
