package com.example.filterify;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.filterify.API.RetrofitService;
import com.example.filterify.model.ImageData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Filterify {


    // Method to send the image to the server
    public Bitmap sendImageToServer(ImageView imageViewUser, List<String> filters) {
        Bitmap bitmap = ((BitmapDrawable) imageViewUser.getDrawable()).getBitmap();
        if (bitmap == null) {
            Log.e("OnFailure", "Bitmap is null");
            return null;
        }
        // Convert Bitmap to byte array
        String baseString = convertBitmapToBase64String(bitmap);
        Bitmap[] bitmapReturn = new Bitmap[]{null};

        // Make API call to send image to server
        RetrofitService retrofitService = createRetrofitConnection();


        Call<ImageData> call = retrofitService.applyFilters(new ImageData(baseString), filters);
        call.enqueue(new Callback<ImageData>() {
            @Override
            public void onResponse(@NonNull Call<ImageData> call, @NonNull Response<ImageData> response) {
                if (response.isSuccessful()) {
                    // Handle success response
                    Log.d("OnResponse", "Image sent successfully");
                    assert response.body() != null;
                    byte[] decodedString = Base64.decode(response.body().getByteArray(), Base64.DEFAULT);
                    Bitmap bitmapResponse = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    bitmapReturn[0] = bitmapResponse;
                } else {
                    // Handle error response
                    Log.e("OnResponse", "Failed to send image: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ImageData> call, @NonNull Throwable t) {
                // Handle failure
                Log.e("OnFailure", "Failed to send image", t);
            }
        });

        return bitmapReturn[0];
    }

    private String convertBitmapToBase64String(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @NonNull
    private RetrofitService createRetrofitConnection() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://seminartest.pythonanywhere.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(RetrofitService.class);
    }
}
