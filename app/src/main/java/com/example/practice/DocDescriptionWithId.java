package com.example.practice;

public class DocDescriptionWithId {
    private int id;
    private String name;

    //body id hush
    private String body;

    public DocDescriptionWithId(int id, String name, String body) {
        this.body = body;
        this.name = name;
        this.id = id;
    }
}
