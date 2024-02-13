package com.example.practice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton addNewDocumentButton;
    private ImageButton toSettingsButton;
    private LinearLayout mainScrollLayout;

    private final LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNewDocumentButton = findViewById(R.id.addButton);
        toSettingsButton = findViewById(R.id.settingsButton);
        mainScrollLayout = findViewById(R.id.scrollViewLayout);

        addNewDocumentButton.setOnClickListener(this);
        toSettingsButton.setOnClickListener(this);

        // TODO: Здесь будет выгрузка всех текущих документов из бд


    }

    private void addDocIntoScrollView(String docName) {
        // Добавить на панель новую кнопку с надписью. Доработать
        Button newDocument = new Button(this);
        newDocument.setText(docName);
        mainScrollLayout.addView(newDocument, linearLayoutParams);
    }

    @Override
    public void onClick(View clickedButton) {
        if (clickedButton.getId() == R.id.settingsButton) {
            // Открытие Settings activity
            Intent openSettingsActivity = new Intent(this, Settings.class);
            startActivity(openSettingsActivity);
        }
        else if (clickedButton.getId() == R.id.addButton) {
            // TODO: Здесь надо будет сделать добавление нового документа в базу данных
            // Открытие TextEditor activity
            addDocIntoScrollView("Unnamed");
            Intent openTextEditorActivity = new Intent(this, TextEditor.class);
            startActivity(openTextEditorActivity);
        }
    }

}