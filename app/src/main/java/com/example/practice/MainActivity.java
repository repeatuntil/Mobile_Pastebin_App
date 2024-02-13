package com.example.practice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton addNewDocumentButton;
    private ImageButton toSettingsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNewDocumentButton = findViewById(R.id.addButton);
        addNewDocumentButton.setOnClickListener(this);

        toSettingsButton = findViewById(R.id.settingsButton);
        toSettingsButton.setOnClickListener(this);
        // TODO: Здесь будет выгрузка всех текущих документов из бд
    }


    @Override
    public void onClick(View clickedButton) {
        // TODO: Здесь надо будет сделать добавление нового документа в базу данных

        if (clickedButton.getId() == R.id.settingsButton) {
            Intent openSettingsActivity = new Intent(this, Settings.class);
            startActivity(openSettingsActivity);
        }
        else {
            Intent openTextEditorActivity = new Intent(this, TextEditor.class);
            startActivity(openTextEditorActivity);
        }
    }

}