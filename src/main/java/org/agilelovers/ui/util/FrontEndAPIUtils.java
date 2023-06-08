package org.agilelovers.ui.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.agilelovers.common.models.*;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.agilelovers.ui.Constants.*;

/**
 * A utility class for sending requests to the backend API.
 */
public class FrontEndAPIUtils {

    public static final ObjectMapper mapper = new ObjectMapper();

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
    public static String createAccount(String username, String password, String apiPassword)
            throws URISyntaxException, IOException, InterruptedException, IllegalArgumentException {
        HttpRequest postRequest = HttpRequest.newBuilder().uri(new URI(
                        SERVER_URL + USER_ENDPOINT + SIGN_UP_REQUEST))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(
                        new SecureUserModel(username, password, apiPassword))))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("Response code: " + response.statusCode());
            System.err.println("Response body: " + response.body());
            throw new IllegalArgumentException(response.body());
        }

        return mapper.readValue(response.body(), ReducedUserModel.class).getId();
    }

    /**
     * Login user credential and return the user credential.
     * Send a GET request to the backend API to log in to an existing account.
     * If the user credentials are invalid, an exception is thrown.
     * If the user credentials are valid, the user credential is returned.
     *
     * @param username username of the account
     * @param password password of the account
     * @return the user credential of the account
     * @throws IOException              thrown if the request cannot be sent
     * @throws InterruptedException     thrown if the request is interrupted
     * @throws IllegalArgumentException thrown if the user credentials are invalid
     */
    public static String login(String username, String password) throws IOException,
            InterruptedException,
            IllegalArgumentException {
        // send a get request to the api endpoint
        HttpRequest getRequest =
                HttpRequest.newBuilder().uri(URI.create(SERVER_URL + USER_ENDPOINT + SIGN_IN_REQUEST))
                        .header("Content-Type", "application/json")
                        .method("GET",
                                HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(
                                        new UserModel(username, password))))
                        .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("Response code: " + response.statusCode());
            System.err.println("Response body: " + response.body());
            throw new IllegalArgumentException("Incorrect user credentials.");
        }

        return mapper.readValue(response.body(), ReducedUserModel.class).getId();
    }

    /**
     * Fetch email history list.
     *
     * @param userId the userId
     * @return the list
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public static List<Prompt> fetchPromptHistory(String command, String userId) throws IOException,
            InterruptedException {
        String endpoint = switch (command) {
            case QUESTION_COMMAND -> QUESTION_ENDPOINT;
            case CREATE_EMAIL_COMMAND -> EMAIL_ENDPOINT;
            case SEND_EMAIL_COMMAND -> RETURNED_EMAIL_ENDPOINT;
            default -> throw new IllegalArgumentException("Invalid prompt type.");
        };

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + endpoint + GET_ALL_REQUEST + userId))
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

        System.out.println(response.body());

        List<Prompt> output = new ArrayList<>();

        switch (command) {
            case QUESTION_COMMAND ->
                    output.addAll(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").create().fromJson(
                            response.body(), new TypeToken<List<Question>>() {
                            }.getType()));

            case CREATE_EMAIL_COMMAND -> output.addAll(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                    .create().fromJson(response.body(), new TypeToken<List<EmailDraft>>() {
                    }.getType()));
            case SEND_EMAIL_COMMAND -> output.addAll(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                    .create().fromJson(response.body(), new TypeToken<List<ReturnedEmail>>() {
                    }.getType()));
        }

        return output;
    }

    public static void newPrompt(Command command, Prompt prompt, String userId)
            throws IOException, InterruptedException {
        HttpRequest postRequest = HttpRequest.newBuilder().uri(URI.create(
                        SERVER_URL + (command.getCommand().equals(QUESTION_COMMAND) ? QUESTION_ENDPOINT :
                                EMAIL_ENDPOINT) +
                                POST_REQUEST + userId))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        mapper.writeValueAsString(new EmailModel(command.getTranscribed()))))
                .build();

        System.out.println(mapper.writeValueAsString(new EmailModel(command.getTranscribed())));

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("Response code: " + response.statusCode());
            System.err.println("Response body: " + response.body());
            throw new IllegalArgumentException(response.body());
        }

        Prompt responsePrompt = new Gson().fromJson(response.body(),
                command.getCommand().equals(QUESTION_COMMAND) ? Question.class : EmailDraft.class);
        prompt.setBody(responsePrompt.getBody());
        prompt.setId(responsePrompt.getId());
        prompt.setCreatedDate(responsePrompt.getCreatedDate());

    }

    public static void deletePrompt(Prompt prompt) throws IOException, InterruptedException {
        System.out.println(prompt.getCommand());
//        String endpoint = switch (prompt.getCommand()) {
//            case QUESTION_COMMAND -> QUESTION_ENDPOINT;
//            case CREATE_EMAIL_COMMAND -> EMAIL_ENDPOINT;
//            case SEND_EMAIL_COMMAND -> RETURNED_EMAIL_ENDPOINT;
//            default -> throw new IllegalArgumentException("Invalid prompt type.");
//        };
        String endpoint;
        if (prompt instanceof Question) {
            endpoint = QUESTION_ENDPOINT;
        } else if (prompt instanceof EmailDraft) {
            endpoint = EMAIL_ENDPOINT;
        } else if (prompt instanceof ReturnedEmail) {
            endpoint = RETURNED_EMAIL_ENDPOINT;
        } else {
            throw new IllegalArgumentException("Invalid prompt type.");
        }

        System.out.println(prompt.getId());

        HttpRequest deleteRequest =
                HttpRequest.newBuilder().uri(URI.create(SERVER_URL + endpoint + DELETE_REQUEST + prompt.getId()))
                        .DELETE()
                        .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new RuntimeException("Question deletion failed.");
    }

    public static void clearAll(String commandType, String uid) throws IOException, InterruptedException {
        String endpoint = switch (commandType) {
            case QUESTION_COMMAND -> QUESTION_ENDPOINT;
            case CREATE_EMAIL_COMMAND -> EMAIL_ENDPOINT;
            case SEND_EMAIL_COMMAND -> RETURNED_EMAIL_ENDPOINT;
            default -> throw new IllegalArgumentException("Invalid prompt type.");
        };

        HttpRequest deleteRequest =
                HttpRequest.newBuilder().uri(URI.create(SERVER_URL + endpoint + DELETE_ALL_REQUEST + uid))
                        .DELETE()
                        .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new RuntimeException("Question deletion failed.");
    }

    // TODO
    public static void sendEmail(Prompt prompt, Command currentCommand, String command, Prompt selectedPrompt, String userId)
            throws IOException, InterruptedException {
        HttpRequest sendRequest =
                HttpRequest.newBuilder().uri(URI.create(SERVER_URL + RETURNED_EMAIL_ENDPOINT + SEND_REQUEST + userId))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(
                                new ReturnedEmailModel(selectedPrompt.getId(), currentCommand.getCommand_arguments(),
                                        command, currentCommand.getTranscribed()))))
                        .build();

        HttpClient client = HttpClient.newHttpClient();

        System.out.println(new ReturnedEmailModel(selectedPrompt.getId(), currentCommand.getCommand_arguments(),
                command, currentCommand.getTranscribed()));

        HttpResponse<String> response = client.send(sendRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("Response code: " + response.statusCode());
            System.err.println("Response body: " + response.body());
            throw new IllegalArgumentException(response.body());
        }

        Prompt responsePrompt = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                    .create().fromJson(response.body(), ReturnedEmail.class);
        prompt.setBody(responsePrompt.getBody());
        prompt.setId(responsePrompt.getId());
        prompt.setCreatedDate(responsePrompt.getCreatedDate());

    }

    public static EmailConfig getEmailConfig(String userId) throws IOException, InterruptedException {
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + EMAIL_CONFIGURATION_ENDPOINT + GET_REQUEST + userId))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 404) {
            return new EmailConfig();
        } else if (response.statusCode() != 200) {
            System.err.println("Response code: " + response.statusCode());
            System.err.println("Response body: " + response.body());
            System.err.println(userId);
            throw new RuntimeException("Get email config failed.");
        }

        return new Gson().fromJson(response.body(), EmailConfig.class);
    }

    // TODO
    public static void setEmailConfig(EmailConfig config, String userId) throws IOException, InterruptedException {
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + EMAIL_CONFIGURATION_ENDPOINT + SAVE_REQUEST + userId))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(config)))
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
        HttpPost uploadFile = new HttpPost(SERVER_URL + API_TRANSCRIBE_ENDPOINT + uid);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        // This attaches the file to the POST:
        File f = new File(RECORDING_PATH);
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
