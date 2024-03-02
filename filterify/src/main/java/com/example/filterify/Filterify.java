package com.example.filterify;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.example.filterify.API.RetrofitService;
import com.example.filterify.model.ImageData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Filterify {

    private Retrofit retrofit;
    private RetrofitService retrofitService;


    // Method to send the image to the server
    public  Bitmap sendImageToServer(ImageView imageViewUser, List<String> filters) {
        Bitmap bitmap = ((BitmapDrawable) imageViewUser.getDrawable()).getBitmap();
        Bitmap[] bitmapReturn = new Bitmap[0];
        if (bitmap != null) {
            // Convert Bitmap to byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] byteArray = stream.toByteArray();

            // Create request body for image bytes
            RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), byteArray);

            // Make API call to send image to server
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://seminartest.pythonanywhere.com")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            retrofitService = retrofit.create(RetrofitService.class);

            String baseString = Base64.encodeToString(byteArray, Base64.DEFAULT);
            bitmapReturn = new Bitmap[]{null};
            Bitmap[] finalBitmapReturn = bitmapReturn;
            Call<ImageData> call = retrofitService.applyFilters(new ImageData(baseString), "BLUR,CONTOUR");
            call.enqueue(new Callback<ImageData>() {
                @Override
                public void onResponse(Call<ImageData> call, Response<ImageData> response) {
                    if (response.isSuccessful()) {
                        // Handle success response
                        Log.d("OnResponse", "Image sent successfully, body= " + response.body());
                        byte[] decodedString = Base64.decode(response.body().getByteArray(), Base64.DEFAULT);
                        Bitmap bitmapResponse = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        finalBitmapReturn[0] = bitmapResponse;
                    } else {
                        // Handle error response
                        Log.e("OnResponse", "Failed to send image: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ImageData> call, Throwable t) {
                    // Handle failure
                    Log.e("OnFailure", "Failed to send image", t);
                }
            });
        } else {
            Log.e("OnFailure", "Bitmap is null");
        }
        return bitmapReturn[0];
    }
}
