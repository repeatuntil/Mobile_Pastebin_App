package com.example.practice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiseAPI {
    @POST("auth/register/")
    Call<List<Tokens>> createNewUser(@Body User user);
}
