package org.agilelovers.backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * ChatGPT helper class to get answer from GPT-3.5 (used by SayItAssistant)
 */
public class ChatGPTHelper {
    /**
     * Gets answer from GPT-3.5
     *
     * @param CHATGPT      API data to access GPT-3.5
     * @param token        token to make API request
     * @param organization the organization
     * @param question     question to be answered
     * @return answer to provided question
     * @throws IOException          if any I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public static String getAnswer(APIData CHATGPT, String token,
                                   String organization, String question)
            throws IOException, InterruptedException {


        JSONObject requestBody = new JSONObject();
        requestBody.put("model", CHATGPT.model());
        requestBody.put("prompt", question);
        requestBody.put("max_tokens", question.length() + 5);
        requestBody.put("temperature", 1.0);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CHATGPT.endpoint()))
                .header("Content-Type", "application/json")
                .header("Authorization", String.format("Bearer %s", token))
                .header("OpenAI-Organization", organization)
                .POST(HttpRequest.BodyPublishers.ofString(
                        String.valueOf(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();

        JSONObject responseJson = new JSONObject(responseBody);

        JSONArray choices = responseJson.getJSONArray("choices");

        return choices.getJSONObject(0).getString("text");
    }
}
