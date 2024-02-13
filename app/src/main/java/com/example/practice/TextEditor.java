package com.example.practice;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TextEditor extends Activity {

    private int _fileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_editor);

        _fileId = getIntent().getExtras().getInt("fileId");
    }

    private File saveTextInFile(String text) {

        Context context = getApplicationContext();

        File tempFilePath = new File(context.getFilesDir(), "");

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

    private void putFile() {
        EditText textEditor = findViewById(R.id.plain_text_input);
        String text = textEditor.getText().toString();

        //Подключение к s3 серверу
        String accessKey = "YCAJEL4iy2T_galhGQdLy4yWj";
        String secretKey = "YCPy2-dfJPDUTUz1LLZHCq2mW2cWgaqkPi0qC5mn";
        BasicAWSCredentials awsCreds = new BasicAWSCredentials (accessKey, secretKey);

        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTPS);
        AmazonS3Client s3Client = new AmazonS3Client(awsCreds, clientConfig);
        s3Client.setRegion(Region.getRegion(Regions.US_EAST_1));
        s3Client.setEndpoint("storage.yandexcloud.net");

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
}