package org.agilelovers.ui.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.agilelovers.ui.Constants;
import org.agilelovers.ui.object.*;
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
    public static UserCredential createAccount(String username, String password, String apiPassword)
            throws URISyntaxException, IOException, InterruptedException, IllegalArgumentException {
        HttpRequest postRequest = HttpRequest.newBuilder().uri(new URI(Constants.SERVER_URL + Constants.USER_ENDPOINT + Constants.SIGN_UP_REQUEST))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(new UserCredentialWithKey(username, password, null, null, apiPassword).toString()))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("Response code: " + response.statusCode());
            System.err.println("Response body: " + response.body());
            throw new IllegalArgumentException(response.body());
        }

        return new Gson().fromJson(response.body(), UserCredential.class);
    }

    /**
     * Login user credential and return the user credential.
     * Send a GET request to the backend API to log in to an existing account.
     * If the user credentials are invalid, an exception is thrown.
     * If the user credentials are valid, the user credential is returned.
     *
     * @param username username of the account
     * @param password password of the account
     * @param userId       id of the account
     * @return the user credential of the account
     * @throws IOException              thrown if the request cannot be sent
     * @throws InterruptedException     thrown if the request is interrupted
     * @throws IllegalArgumentException thrown if the user credentials are invalid
     */
    public static UserCredential login(String username, String password, String userId) throws IOException,
            InterruptedException,
            IllegalArgumentException {
        // send a get request to the api endpoint
        HttpRequest getRequest =
                HttpRequest.newBuilder().uri(URI.create(Constants.SERVER_URL + Constants.USER_ENDPOINT + Constants.SIGN_IN_REQUEST))
                        .header("Content-Type", "application/json")
                        .method("GET",
                                HttpRequest.BodyPublishers.ofString(
                                        new UserCredential(username, password, null, userId).toString()))
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
     * Fetch email history list.
     *
     * @param userId the userId
     * @return the list
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public static List<Prompt> fetchPromptHistory(String command, String userId) throws IOException, InterruptedException {
        String endpoint = switch (command) {
            case Constants.QUESTION_COMMAND -> Constants.QUESTION_ENDPOINT;
            case Constants.CREATE_EMAIL_COMMAND -> Constants.EMAIL_ENDPOINT;
            case Constants.SEND_EMAIL_COMMAND -> Constants.RETURNED_EMAIL_ENDPOINT;
            default -> throw new IllegalArgumentException("Invalid prompt type.");
        };

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(Constants.SERVER_URL + endpoint + Constants.GET_ALL_REQUEST + userId))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("Response code: " + response.statusCode());
            System.err.println("Response body: " + response.body());
            System.err.println(userId);
            throw new RuntimeException("Fetch history failed.");
        }

        Type listType = new TypeToken<List<Prompt>>() {
        }.getType();
        return new Gson().fromJson(response.body(), listType);
    }

    public static Prompt newPrompt(Command command, Prompt prompt, String userId) throws IOException, InterruptedException {
        HttpRequest postRequest = HttpRequest.newBuilder().uri(URI.create(Constants.SERVER_URL + (command.getCommand().equals(Constants.QUESTION_COMMAND) ? Constants.QUESTION_ENDPOINT : Constants.EMAIL_ENDPOINT) + Constants.POST_REQUEST + userId))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(command.getTranscribed()))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("Response code: " + response.statusCode());
            System.err.println("Response body: " + response.body());
            throw new IllegalArgumentException(response.body());
        }

        Prompt prompt1 = new Gson().fromJson(response.body(), (command.getCommand().equals(Constants.QUESTION_COMMAND) ? Question.class : EmailDraft.class));
        prompt.setBody(prompt1.getBody());
        prompt.setId(prompt1.getId());
        prompt.setCreatedDate(prompt1.getCreatedDate());

        return prompt;
    }

    public static void deletePrompt(Prompt prompt) throws IOException, InterruptedException {
        String endpoint = switch (prompt.getCommand()) {
            case Constants.QUESTION_COMMAND -> Constants.QUESTION_ENDPOINT;
            case Constants.CREATE_EMAIL_COMMAND -> Constants.EMAIL_ENDPOINT;
            case Constants.SEND_EMAIL_COMMAND -> Constants.RETURNED_EMAIL_ENDPOINT;
            default -> throw new IllegalArgumentException("Invalid prompt type.");
        };

        HttpRequest deleteRequest =
                HttpRequest.newBuilder().uri(URI.create(Constants.SERVER_URL + endpoint + Constants.DELETE_REQUEST + prompt.getId()))
                        .DELETE()
                        .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new RuntimeException("Question deletion failed.");
    }

    public static void clearAll(String command, String uid) throws IOException, InterruptedException {
        String endpoint = switch (command) {
            case Constants.QUESTION_COMMAND -> Constants.QUESTION_ENDPOINT;
            case Constants.CREATE_EMAIL_COMMAND -> Constants.EMAIL_ENDPOINT;
            case Constants.SEND_EMAIL_COMMAND -> Constants.RETURNED_EMAIL_ENDPOINT;
            default -> throw new IllegalArgumentException("Invalid prompt type.");
        };

        HttpRequest deleteRequest =
                HttpRequest.newBuilder().uri(URI.create(Constants.SERVER_URL + endpoint + Constants.DELETE_ALL_REQUEST + uid))
                        .DELETE()
                        .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new RuntimeException("Question deletion failed.");
    }

    // TODO
    public static Prompt sendEmail(Command currentCommand, String command, String SentId, String userId) {
        // command is for email address
        // prompt is for checking valid email draft
        EmailInformation info = new EmailInformation(command, currentCommand.getTranscribed(), currentCommand.getCommand_arguments(), SentId);
        HttpRequest sendRequest =
                HttpRequest.newBuilder().uri(URI.create(Constants.SERVER_URL + Constants.RETURNED_EMAIL_ENDPOINT + Constants.SEND_REQUEST + userId))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(info)))
                        .build();
        return null;
    }

    public static EmailConfig getEmailConfig(String userId) throws IOException, InterruptedException {
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(Constants.SERVER_URL + Constants.EMAIL_CONFIGURATION_ENDPOINT + Constants.GET_REQUEST + userId))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("Response code: " + response.statusCode());
            System.err.println("Response body: " + response.body());
            System.err.println(userId);
            throw new RuntimeException("Fetch history failed.");
        }

        return new Gson().fromJson(response.body(), EmailConfig.class);
    }

    // TODO
    public static void setEmailConfig(EmailConfig config, String userId) throws IOException, InterruptedException {
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(Constants.SERVER_URL + Constants.EMAIL_CONFIGURATION_ENDPOINT + Constants.SAVE_REQUEST + userId))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(config)))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("Response code: " + response.statusCode());
            System.err.println("Response body: " + response.body());
            System.err.println(userId);
            throw new RuntimeException("Set email config failed.");
        }
    }

    /**
     * Send audio string.
     *
     * @param uid the uid
     * @return the string
     * @throws IOException the io exception
     */
    public static Command sendAudio(String uid) throws IOException {
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
        return new Gson().fromJson(question, Command.class);
    }
}
