package com.example.practice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class About_Us extends AppCompatActivity implements View.OnClickListener {
    private ImageButton returnButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        returnButton = findViewById(R.id.backButton);
        returnButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View clickedButton) {

        Intent openAboutUsActivity = new Intent(this, Settings.class);
        startActivity(openAboutUsActivity);
    }
}