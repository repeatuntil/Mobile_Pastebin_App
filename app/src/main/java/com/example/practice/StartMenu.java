package com.example.practice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartMenu extends Activity implements View.OnClickListener {
    Button toReg, toLogIn;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);
        toReg = findViewById(R.id.buttonReg);
        toLogIn = findViewById(R.id.buttonLogin);
        toReg.setOnClickListener(this);
        toLogIn.setOnClickListener(this);
    }
    @Override
    public void onClick(View clickedButton) {
        if (clickedButton.getId() == R.id.buttonReg) {
            Intent openTextEditorActivity = new Intent(this, Registration.class);
            startActivity(openTextEditorActivity);
        }
        else if (clickedButton.getId() == R.id.buttonLogin) {
            Intent openTextEditorActivity = new Intent(this, Authorization.class);
            startActivity(openTextEditorActivity);
        }
    }
}
