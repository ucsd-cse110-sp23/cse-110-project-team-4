package org.agilelovers.backend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChatGPT {
    private static final String API_ENDPOINT = "https://api.openai.com/v1/completions";
    private static final String API_KEY = "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";
    private static final String MODEL = "text-davinci-003";
    private static final String ORGANIZATION = "org-Sd9bwBmEf5IDns4KIh3k3fXp";

    public static String getAnswer(String question) throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        requestBody.put("prompt", question);
        requestBody.put("max_tokens", question.length() + 5);
        requestBody.put("temperature", 1.0);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_ENDPOINT))
                .header("Content-Type", "application/json")
                .header("Authorization", String.format("Bearer %s", API_KEY))
                .header("OpenAI-Organization", ORGANIZATION)
                .POST(HttpRequest.BodyPublishers.ofString(
                        String.valueOf(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();

        JSONObject responseJson = new JSONObject(responseBody);

        JSONArray choices = responseJson.getJSONArray("choices");
        String generatedText = choices.getJSONObject(0).getString("text");

        System.out.println(generatedText);
        return generatedText;
    }
}
