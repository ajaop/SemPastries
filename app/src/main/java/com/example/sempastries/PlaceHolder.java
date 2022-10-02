package com.example.sempastries;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PlaceHolder {

    //@Multipart
    @POST("signup/")
    Call<ResponseBody> getState(
            @Part("subPropertyId") RequestBody PropertyId);

}