package com.example.practice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TextEditor extends Activity implements View.OnClickListener {
    ImageButton quit;
    ImageButton save;
    private int _fileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_editor);
        quit = findViewById(R.id.quitButton);
        save = findViewById(R.id.saveButton);
        quit.setOnClickListener(this);
        save.setOnClickListener(this);
        //Параметры открытого окна
        Bundle arguments = getIntent().getExtras();

        _fileId = arguments.getInt("fileId");

        OpenTextEditorStatus openStat = (OpenTextEditorStatus) arguments.get("openStat");
        if (openStat == OpenTextEditorStatus.OldFile)
        {
            getFile();
        } else if (openStat == OpenTextEditorStatus.NewFile)
        {
            putFile();
        }
    }

    private File saveTextInFile(String text) {

        Context context = getApplicationContext();

        File tempFilePath = new File(context.getFilesDir(), "textBuffer.txt");

        FileWriter writer = null;
        try {
            writer = new FileWriter(tempFilePath);
            writer.write(text);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tempFilePath;
    }

    private boolean checkFile(AmazonS3Client s3Client) {
        //Данные объекта
        String bucketName = "osgyqgrczgejueayrttvztamhzyawribhyjveaxyifjmgtgckw";
        String keyName = String.valueOf(_fileId);

        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Boolean> future = service.submit(new Callable<Boolean>() {
            @Override
            public synchronized Boolean call() throws Exception {
                return s3Client.doesObjectExist(bucketName, keyName);
            }
        });

        try {
            boolean isExist = future.get();
            return isExist;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private AmazonS3Client configClient() {

        //Подключение к s3 серверу
        String accessKey = "YCAJEL4iy2T_galhGQdLy4yWj";
        String secretKey = "YCPy2-dfJPDUTUz1LLZHCq2mW2cWgaqkPi0qC5mn";
        BasicAWSCredentials awsCreds = new BasicAWSCredentials (accessKey, secretKey);

        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTPS);
        AmazonS3Client s3Client = new AmazonS3Client(awsCreds, clientConfig);
        s3Client.setRegion(Region.getRegion(Regions.US_EAST_1));
        s3Client.setEndpoint("storage.yandexcloud.net");

        return s3Client;
    }

    private void putFile() {
        EditText textEditor = findViewById(R.id.plain_text_input);
        String text = textEditor.getText().toString();

        //Подключение к s3 серверу
        AmazonS3Client s3Client = configClient();

        //Данные объекта
        String bucketName = "osgyqgrczgejueayrttvztamhzyawribhyjveaxyifjmgtgckw";
        String keyName = String.valueOf(_fileId);

        //Запись текста в служебный файл
        File tempFile = saveTextInFile(text);

        //Загрузка файла на s3
        ExecutorService service = Executors.newSingleThreadExecutor();

        Future future = service.submit(new Runnable() {
            public void run() {
                s3Client.putObject(bucketName, keyName, tempFile);
            }
        });

        try {
            future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        service.shutdown();

        //Очистка текста в служебном файле
        saveTextInFile("");
    }

    private void getFile() {
        //Подключение к s3 серверу
        AmazonS3Client s3Client = configClient();

        //Данные объекта
        String bucketName = "osgyqgrczgejueayrttvztamhzyawribhyjveaxyifjmgtgckw";
        String keyName = String.valueOf(_fileId);

        if (!checkFile(s3Client)) {
            return;
        }

        //Чтение файла с s3
        ExecutorService service = Executors.newSingleThreadExecutor();

        Future<String> future = service.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return s3Client.getObjectAsString(bucketName, keyName);
            }
        });

        try {
            String text = future.get();
            // Вставляем текст в редактор
            EditText textEditor = findViewById(R.id.plain_text_input);
            textEditor.setText(text);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        service.shutdown();
    }

    private void deleteFile() {
        //Подключение к s3 серверу
        AmazonS3Client s3Client = configClient();

        //Данные объекта
        String bucketName = "osgyqgrczgejueayrttvztamhzyawribhyjveaxyifjmgtgckw";
        String keyName = String.valueOf(_fileId);

        if (!checkFile(s3Client)) {
            return;
        }

        //Загрузка файла на s3
        ExecutorService service = Executors.newSingleThreadExecutor();

        Future future = service.submit(new Runnable() {
            public void run() {
                s3Client.deleteObject(bucketName, keyName);
            }
        });

        try {
            future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        service.shutdown();
    }
    @Override
    public void onClick(View clickedButton) {
        if (clickedButton.getId() == R.id.saveButton) {
            putFile();
        }
        else {
            deleteFile();
        }
        Intent openTextEditorActivity = new Intent(this, MainActivity.class);
        startActivity(openTextEditorActivity);
    }
}