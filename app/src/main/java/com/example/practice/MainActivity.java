package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton addNewDocumentButton;
    private ImageButton toSettingsButton;
    private LinearLayout mainScrollLayout;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;


    private final LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    );

    final int mod = 69_273_666, hashPrime = 31, maxLength = 100;
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

    @Override
    protected void onStart(){
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this, Authorization.class));
        }
    }

    private int countHash(String docName) {

        int n = docName.length(), currentHash = 0;
        for (int index = 0; index < n; index++) {
            currentHash = (currentHash * hashPrime + docName.charAt(index)) % mod;
        }
        return currentHash;
    }

    private int addDocIntoScrollView(String docName) {
        // Добавить на панель новую кнопку с надписью. Доработать
        Button newDocument = new Button(this);

        newDocument.setText(docName);
        int docHash = countHash(docName);

        newDocument.setId(docHash);
        newDocument.setOnClickListener(this);

        mainScrollLayout.addView(newDocument, linearLayoutParams);

        return docHash;
    }

    @Override
    public void onClick(View clickedButton) {

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        if (clickedButton.getId() == R.id.settingsButton) {
                // Открытие Settings activity
                Intent openSettingsActivity = new Intent(this, Settings.class);
                startActivity(openSettingsActivity);
            }
        else if (clickedButton.getId() == R.id.addButton) {
                addDocumentToDB();
                // TODO: Здесь надо будет сделать добавление нового документа в базу данных
                // Открытие TextEditor activity
                int fileId = addDocIntoScrollView("Unnamed");
                Intent openTextEditorActivity = new Intent(this, TextEditor.class);
                openTextEditorActivity.putExtra("fileId", fileId);
                openTextEditorActivity.putExtra("openStat", OpenTextEditorStatus.NewFile);
                startActivity(openTextEditorActivity);
        }
        else {
            // TODO: Открывая старый файл стоит передавать в качестве параметра "fileId" Id нажатой кнопки, и также OldFile как "OpenStat"
            System.out.println(clickedButton.getId());
        }
    }

    private void addDocumentToDB(){
        DatabaseReference userRef = reference.child("Users").child(mAuth.getCurrentUser().getUid());
        String newRecordKey = userRef.child("files").push().getKey();
        HashMap<String, Object> textRecord = new HashMap<>();
        textRecord.put("title", "");
        textRecord.put("body", "");
        userRef.child("files").child(newRecordKey).updateChildren(textRecord);
    }

    private void idk(){
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot fileSnapshot : snapshot.getChildren()) {
                    String title = fileSnapshot.child("title").getValue(String.class);
                    String body = fileSnapshot.child("body").getValue(String.class);
                    // Далее как-то отображайте название что ли
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}