package com.example.practice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ServiseAPI {
    @POST("api/v1/auth/register/")
    Call<List<Tokens>> createNewUser(@Body User user);

    @POST("api/v1/auth/token/")
    Call<List<Tokens>> authorizationUser(@Body User user);

    @POST("api/v1/documents/")
    Call<List<DocDescriptionWithId>> createNewDoc(@Body DocDescription doc);

    @GET("api/v1/documents/")
    Call<List<DocDescriptionWithId>> getAllDocs();

    /*
    @DELETE(documents/)
    deleteDocs();
    */
}