package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import java.util.HashMap;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton addNewDocumentButton;
    private ImageButton toSettingsButton;
    private LinearLayout mainScrollLayout;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;

    private String Base_url;
    private Retrofit retrofit;
    private ServiseAPI service;

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

        Base_url = "http://127.0.0.1:8000/";

        Context context = getApplicationContext();

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override public okhttp3.Response intercept(Chain chain) throws IOException {
                String tokenAccess = "";

                File filePath = new File(context.getFilesDir(), "access.txt");

                try(FileReader reader = new FileReader(filePath))
                {
                    // читаем посимвольно
                    int c;
                    while((c=reader.read())!=-1){
                        tokenAccess = tokenAccess + (char)c;
                    }
                }
                catch(IOException ex){

                    System.out.println(ex.getMessage());
                }
                Request request = chain.request();
                Request newReq = request.newBuilder()
                        .addHeader("Authorization", tokenAccess)
                        .build();
                return chain.proceed(newReq);
            }
        }).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(ServiseAPI.class);

        Call<List<DocDescriptionWithId>> call = service.getAllDocs();
        call.enqueue(new Callback<List<DocDescriptionWithId>>() {
            @Override
            public void onResponse(Call<List<DocDescriptionWithId>> call, Response<List<DocDescriptionWithId>> response) {
                if (response.isSuccessful()) {
                    List<DocDescriptionWithId> buttons = response.body();

                    // TODO: Создать кнопки для файлов. Принимайте name как название файла и body как id кнопки
                } else {
                    System.out.println("error1!");
                }
            }

            @Override
            public void onFailure(Call<List<DocDescriptionWithId>> call, Throwable t) {
                System.out.println(t.toString());
            }
        });
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
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String nameFile = dateFormat.format(date);
            int fileId = addDocIntoScrollView(nameFile);
            Intent openTextEditorActivity = new Intent(this, TextEditor.class);
            openTextEditorActivity.putExtra("fileId", fileId);
            openTextEditorActivity.putExtra("openStat", OpenTextEditorStatus.NewFile);
            startActivity(openTextEditorActivity);

            //Часть с работой Docker-сервера
            DocDescription doc = new DocDescription(nameFile, String.valueOf(fileId));

            Call<List<DocDescriptionWithId>> call = service.createNewDoc(doc);

            call.enqueue(new Callback<List<DocDescriptionWithId>>() {
                @Override
                public void onResponse(Call<List<DocDescriptionWithId>> call, Response<List<DocDescriptionWithId>> response) {
                    if (response.isSuccessful()) {
                        System.out.println("success");
                    } else {
                        System.out.println("error1!");
                    }
                }

                @Override
                public void onFailure(Call<List<DocDescriptionWithId>> call, Throwable t) {
                    System.out.println(t.toString());
                }
            });

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