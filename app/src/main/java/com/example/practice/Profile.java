package com.example.practice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public class Profile extends Activity implements View.OnClickListener {
    ImageButton toMainMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);
        toMainMenu = findViewById(R.id.BackButton);
        toMainMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent openTextEditorActivity = new Intent(this, Settings.class);
        startActivity(openTextEditorActivity);
    }
}
