package org.agilelovers.ui.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.agilelovers.ui.Constants;
import org.agilelovers.ui.object.Question;
import org.agilelovers.ui.object.UserCredential;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class FrontEndAPIUtils {

    /**
     * Private constructor to prevent instantiation of this class
     */
    private FrontEndAPIUtils() {}

    public static UserCredential createAccount(String username, String password)
            throws URISyntaxException, IOException, InterruptedException, IllegalArgumentException {
        HttpRequest postRequest = HttpRequest.newBuilder().uri(new URI(Constants.SERVER_URL + Constants.USER_ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(new UserCredential(username, password, null).toString()))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("Response code: " + response.statusCode());
            throw new IllegalArgumentException("Username already exists");
        }

        return new Gson().fromJson(response.body(), UserCredential.class);
    }

    public static UserCredential login(String username, String password, String id) throws IOException,
            InterruptedException,
            IllegalArgumentException {
        // send a get request to the api endpoint
        HttpRequest getRequest =
                HttpRequest.newBuilder().uri(URI.create(Constants.SERVER_URL + Constants.USER_ENDPOINT))
                        .header("Content-Type", "application/json")
                        .method("GET",
                                HttpRequest.BodyPublishers.ofString(
                                        new UserCredential(username, password, id).toString()))
                        .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("Response code: " + response.statusCode());
            throw new IllegalArgumentException("Incorrect user credentials.");
        }

        return new Gson().fromJson(response.body(), UserCredential.class);
    }

    public static Question readQuestion(String id, Question question) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest postRequest = HttpRequest.newBuilder().uri(new URI(Constants.SERVER_URL + Constants.QUESTION_ENDPOINT + "?uid=" + id))
                .header("Content-Type", "text/plain")
                .method("POST",
                        HttpRequest.BodyPublishers.ofString(question.getQuestion()))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IllegalArgumentException("Username already exists");

        Question output = new Gson().fromJson(response.body(), Question.class);
        question.setAnswer(output.getAnswer());
        question.setUserId(output.getUserId());
        question.setId(output.getId());
        return question;
    }

    public static List<Question> fetchHistory(String id) throws IOException, InterruptedException {
        HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(Constants.SERVER_URL + Constants.QUESTION_ENDPOINT + "?uid=" + id))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new RuntimeException("Fetch history failed.");

        Type listType = new TypeToken<List<Question>>() {}.getType();
        return new Gson().fromJson(response.body(), listType);
    }

    public static void deleteQuestion(String id) throws IOException, InterruptedException {
        HttpRequest deleteRequest = HttpRequest.newBuilder().uri(URI.create(Constants.SERVER_URL + Constants.DELETION_ENDPOINT + "?id=" + id))
                .DELETE()
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new RuntimeException("Question deletion failed.");
    }
    public static void deleteAll(String uid) throws IOException, InterruptedException {
        HttpRequest deleteRequest = HttpRequest.newBuilder().uri(URI.create(Constants.SERVER_URL + Constants.DELETE_ALL_ENDPOINT + "?uid=" + uid))
                .DELETE()
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new RuntimeException("Question deletion failed.");
    }
}
