package com.example.baicuoiky_nhom13.API;

import com.cloudinary.utils.ObjectUtils;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Field;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public interface CloudinaryService {

    // Endpoint để upload ảnh lên Cloudinary
    @Multipart
    @POST("image/upload")
    Call<CloudinaryResponse> uploadImage(
            @Part("api_key") RequestBody apiKey,
            @Part("upload_preset") RequestBody uploadPreset,
            @Part("timestamp") RequestBody timestamp,
            @Part("signature") RequestBody signature,
            @Part MultipartBody.Part file
    );

}

