package org.agilelovers.ui.api;

import com.google.gson.Gson;
import org.agilelovers.ui.Constants;
import org.agilelovers.ui.object.Question;
import org.agilelovers.ui.object.UserCredential;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class FrontEndAPIUtils {

    /**
     * Private constructor to prevent instantiation of this class
     */
    private FrontEndAPIUtils() {}

    protected static UserCredential createAccount(String username, String password)
            throws URISyntaxException, IOException, InterruptedException, IllegalArgumentException {
        HttpRequest postRequest = HttpRequest.newBuilder().uri(new URI(Constants.SERVER_URL + "/api/users"))
                .header("Content-Type", "application/json")
                .method("POST",
                        HttpRequest.BodyPublishers.ofString(new UserCredential(username, password, null).toString()))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IllegalArgumentException("Username already exists");

        return new Gson().fromJson(response.body(), UserCredential.class);
    }

    protected static UserCredential login(String username, String password, String id) throws IOException,
            InterruptedException,
            IllegalArgumentException {
        // send a get request to the api endpoint
        HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(Constants.SERVER_URL + "/api/users/"))
                .header("Content-Type", "application/json")
                .method("GET",
                        HttpRequest.BodyPublishers.ofString(new UserCredential(username, password, id).toString()))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IllegalArgumentException("Incorrect user credentials.");

        return new Gson().fromJson(response.body(), UserCredential.class);
    }

    protected static Question getQuestion(String id) throws IOException, InterruptedException {
        // TODO: implement this method
        return null;
    }


}
