package com.example.practice;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Tokens {
    public String refresh;
    public String access;

    public Tokens(String refresh, String access) {
        this.refresh = refresh;
        this.access = access;
    }

    public void writeAccess(File path) {
        File tempFilePath = new File(path, "access.txt");

        FileWriter writer = null;
        try {
            writer = new FileWriter(tempFilePath);
            writer.write(this.access);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeRefresh(File path) {
        File tempFilePath = new File(path, "refresh.txt");

        FileWriter writer = null;
        try {
            writer = new FileWriter(tempFilePath);
            writer.write(this.refresh);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cleanAccess(File path) {

        File tempFilePath = new File(path, "access.txt");

        FileWriter writer = null;
        try {
            writer = new FileWriter(tempFilePath);
            writer.write("");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cleanRefresh(File path) {

        File tempFilePath = new File(path, "refresh.txt");

        FileWriter writer = null;
        try {
            writer = new FileWriter(tempFilePath);
            writer.write("");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}