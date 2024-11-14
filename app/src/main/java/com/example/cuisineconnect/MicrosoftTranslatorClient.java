package com.example.cuisineconnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MicrosoftTranslatorClient {
    private final String endpoint = "https://api.cognitive.microsofttranslator.com";
    private final String apiKey = "e3d3ab48f7cf42828f5f07bc25085d3e";
    private final String region = "southafricanorth"; // Ensure this matches your resource region

    public MicrosoftTranslatorClient() {
        // Constructor can be empty since we set the endpoint, apiKey, and region as constants
    }

    public String translate(String text, String fromLanguage, String toLanguage) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Ensure valid language codes
        if (fromLanguage == null || fromLanguage.isEmpty() || toLanguage == null || toLanguage.isEmpty()) {
            throw new IllegalArgumentException("Both fromLanguage and toLanguage must be provided.");
        }

        // Build the URL with query parameters
        HttpUrl url = HttpUrl.parse(endpoint + "/translate")
                .newBuilder()
                .addQueryParameter("api-version", "3.0")
                .addQueryParameter("from", fromLanguage)
                .addQueryParameter("to", toLanguage)
                .build();

        // Create the JSON request body
        JSONArray requestBody = new JSONArray();
        try {
            requestBody.put(new JSONObject().put("Text", text));
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException("Error creating JSON request body", e);
        }

        // Build the HTTP request
        Request request = new Request.Builder()
                .url(url)  // Use the constructed URL
                .addHeader("Ocp-Apim-Subscription-Key", apiKey)  // Add API key to header
                .addHeader("Ocp-Apim-Subscription-Region", region)  // Add region to header
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toString(), MediaType.get("application/json")))
                .build();

        // Send the request and handle the response
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code() + " - " + response.message());
            }

            String responseBody = response.body().string();

            // Parse the response JSON
            try {
                JSONArray jsonResponse = new JSONArray(responseBody);
                return jsonResponse.getJSONObject(0)
                        .getJSONArray("translations")
                        .getJSONObject(0)
                        .getString("text");
            } catch (JSONException e) {
                e.printStackTrace();
                throw new IOException("Error parsing translation response", e);
            }
        }
    }
}
