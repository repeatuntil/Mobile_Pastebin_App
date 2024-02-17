package com.example.practice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Authorization extends Activity {

    private FirebaseAuth mAuth;

    private EditText email;
    private EditText password;
    private Button signInButton;
    private TextView registerTransfer;

    private String Base_url;
    private Retrofit retrofit;
    private ServiseAPI service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization);

        email = findViewById(R.id.editTextTextEmailAddress2);
        password = findViewById(R.id.editTextTextPassword3);
        signInButton = findViewById(R.id.signInButton);
        registerTransfer = findViewById(R.id.registerTransfer);

        mAuth = FirebaseAuth.getInstance();

        Base_url = "http://127.0.0.1:8000/";

        retrofit = new Retrofit.Builder()
                .baseUrl(Base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ServiseAPI.class);

        registerTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Authorization.this, Registration.class));
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty()){
                    Toast.makeText(Authorization.this, "email cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if (password.getText().toString().isEmpty()){
                    Toast.makeText(Authorization.this, "password cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        startActivity(new Intent(Authorization.this, MainActivity.class));
                                    }
                                    else {
                                        Toast.makeText(Authorization.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    //Часть с Docker сервером
                    String newUserEmail = email.getText().toString();
                    String newUserPassword = password.getText().toString();

                    User autUser = new User(newUserEmail, newUserPassword);

                    Call<List<Tokens>> call = service.authorizationUser(autUser);

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
                            System.out.println(t.toString());
                        }
                    });
                }
            }
        });
    }
}
