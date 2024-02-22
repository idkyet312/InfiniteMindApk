package com.example.myapplication;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenAIRequest {
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/engines/davinci/completions";
    private static final String OPENAI_API_KEY = "your_api_key_here";

    public static String getResponse(String prompt) {
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"prompt\": \"" + prompt + "\", \"max_tokens\": 150}");
            Request request = new Request.Builder()
                    .url(OPENAI_API_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + "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")
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
