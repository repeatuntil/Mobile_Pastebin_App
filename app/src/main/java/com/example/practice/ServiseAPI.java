package com.example.practice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ServiseAPI {
    @POST("auth/register/")
    Call<List<Tokens>> createNewUser(@Body User user);

    @POST("auth/token/")
    Call<List<Tokens>> authorizationUser(@Body User user);

    @POST("documents/")
    Call<List<DocDescriptionWithId>> createNewDoc(@Body DocDescription doc);

    @GET("documents/")
    Call<List<DocDescriptionWithId>> getAllDocs();

    /*
    @DELETE(documents/)
    deleteDocs();
    */
}