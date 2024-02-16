package com.example.practice;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiseAPI {
    @POST("auth/register/")
    Call<User> createNewUser(@Body User user);
}
