package org.agilelovers.ui.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.agilelovers.ui.Constants;
import org.agilelovers.ui.object.Question;
import org.agilelovers.ui.object.Transcription;
import org.agilelovers.ui.object.UserCredential;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * A utility class for sending requests to the backend API.
 */
public class FrontEndAPIUtils {

    /**
     * Private constructor to prevent instantiation of this class
     */
    private FrontEndAPIUtils() {
    }

    /**
     * Create account user credential and return the user credential.
     * Send a POST request to the backend API to create a new account.
     *
     * @param username username of the new account
     * @param password password of the new account
     * @return the user credential of the newly created account
     * @throws URISyntaxException       thrown if the URI is invalid
     * @throws IOException              thrown if the request cannot be sent
     * @throws InterruptedException     thrown if the request is interrupted
     * @throws IllegalArgumentException thrown if the username already exists
     */
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
            System.err.println("Response body: " + response.body());
            throw new IllegalArgumentException("Username already exists");
        }

        return new Gson().fromJson(response.body(), UserCredential.class);
    }

    /**
     * Login user credential and return the user credential.
     * Send a GET request to the backend API to login to an existing account.
     * If the user credentials are invalid, an exception is thrown.
     * If the user credentials are valid, the user credential is returned.
     *
     * @param username username of the account
     * @param password password of the account
     * @param id       id of the account
     * @return the user credential of the account
     * @throws IOException              thrown if the request cannot be sent
     * @throws InterruptedException     thrown if the request is interrupted
     * @throws IllegalArgumentException thrown if the user credentials are invalid
     */
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
            System.err.println("Response body: " + response.body());
            throw new IllegalArgumentException("Incorrect user credentials.");
        }

        return new Gson().fromJson(response.body(), UserCredential.class);
    }

    /**
     * Upload user question to the backend API and get the response from ChatGPT.
     * Send a POST request to the backend API to upload the user question.
     * If the user credentials are invalid, an exception is thrown.
     * If the user credentials are valid, the question object parameter is populated with the response from the
     * backend.
     *
     * @param id       id of the account
     * @param question the question object
     * @return the question object
     * @throws IOException          thrown if the request cannot be sent
     * @throws InterruptedException thrown if the request is interrupted
     * @throws URISyntaxException   thrown if the URI is invalid
     */
    public static Question readQuestion(String id, Question question)
            throws IOException, InterruptedException, URISyntaxException {
        HttpRequest postRequest =
                HttpRequest.newBuilder().uri(new URI(Constants.SERVER_URL + Constants.QUESTION_ENDPOINT + id))
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

    /**
     * Fetch history list.
     *
     * @param id the id
     * @return the list
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public static List<Question> fetchHistory(String id) throws IOException, InterruptedException {
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(Constants.SERVER_URL + Constants.QUESTION_ENDPOINT + id))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("Response code: " + response.statusCode());
            System.err.println("Response body: " + response.body());
            System.err.println(id);
            throw new RuntimeException("Fetch history failed.");
        }

        Type listType = new TypeToken<List<Question>>() {
        }.getType();
        return new Gson().fromJson(response.body(), listType);
    }

    /**
     * Delete question.
     *
     * @param id the id
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public static void deleteQuestion(String id) throws IOException, InterruptedException {
        HttpRequest deleteRequest =
                HttpRequest.newBuilder().uri(URI.create(Constants.SERVER_URL + Constants.DELETION_ENDPOINT + id))
                        .DELETE()
                        .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new RuntimeException("Question deletion failed.");
    }

    /**
     * Delete all.
     *
     * @param uid the uid
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public static void deleteAll(String uid) throws IOException, InterruptedException {
        HttpRequest deleteRequest =
                HttpRequest.newBuilder().uri(URI.create(Constants.SERVER_URL + Constants.DELETE_ALL_ENDPOINT + uid))
                        .DELETE()
                        .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new RuntimeException("Question deletion failed.");
    }

    /**
     * Send audio string.
     *
     * @param uid the uid
     * @return the string
     * @throws IOException the io exception
     */
    public static String sendAudio(String uid) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost(Constants.SERVER_URL + Constants.API_TRANSCRIBE_ENDPOINT + uid);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        // This attaches the file to the POST:
        File f = new File(Constants.RECORDING_PATH);
        builder.addBinaryBody(
                "file",
                new FileInputStream(f),
                ContentType.APPLICATION_OCTET_STREAM,
                f.getName()
        );

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);
        CloseableHttpResponse response = httpClient.execute(uploadFile);
        HttpEntity responseEntity = response.getEntity();
        String question = new String(responseEntity.getContent().readAllBytes());
        System.err.println(question);
        return new Gson().fromJson(question, Transcription.class).getTranscribed();
    }


}
