package com.example.practice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Settings extends Activity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private TextView logoutField;
    private ImageButton returnField;
    LinearLayout toProfile;
    LinearLayout aboutUs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        mAuth = FirebaseAuth.getInstance();

        logoutField = findViewById(R.id.textView9);
        returnField = findViewById(R.id.imageButton);

        toProfile = findViewById(R.id.toProfile);
        toProfile.setOnClickListener(this);
        aboutUs = findViewById(R.id.about_us);
        aboutUs.setOnClickListener(this);

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(Settings.this, Authorization.class));
        }

        logoutField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(Settings.this, Authorization.class));
            }
        });
        returnField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this, MainActivity.class));
            }
        });


    }

    @Override
    protected void onStart(){
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(Settings.this, Authorization.class));
        }
    }

    @Override
    public void onClick(View clickedButton) {
        if (clickedButton.getId() == R.id.about_us) {
            Intent openAboutUsActivity = new Intent(this, About_Us.class);
            startActivity(openAboutUsActivity);
        }
        else {
            Intent openProfileActivity = new Intent(this, Profile.class);
            startActivity(openProfileActivity);
        }
    }
}
