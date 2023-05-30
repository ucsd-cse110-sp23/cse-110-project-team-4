package org.agilelovers.ui.api;

import com.google.gson.Gson;
import org.agilelovers.ui.Constants;
import org.agilelovers.ui.object.UserCredential;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FrontEndAPIUtils {

    /**
     * Private constructor to prevent instantiation of this class
     */
    private FrontEndAPIUtils() {}

    protected static UserCredential login(String username, String password)
            throws URISyntaxException, IOException, InterruptedException, IllegalArgumentException {
        HttpRequest postRequest = HttpRequest.newBuilder().uri(new URI(Constants.SERVER_URL + "/api/users"))
                .POST(HttpRequest.BodyPublishers.ofString(new UserCredential(username, password, null).toString()))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IllegalArgumentException("Incorrect user credentials.");

        return new Gson().fromJson(response.body(), UserCredential.class);
    }
}
