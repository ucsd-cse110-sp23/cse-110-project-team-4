package org.agilelovers.backend;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class WhisperAPIHelper {
    private static void writeParameterToOutputStream(
            OutputStream outputStream,
            String parameterName,
            String parameterValue,
            String boundary
    ) throws IOException {
        outputStream.write(("--" + boundary + "\r\n").getBytes());
        outputStream.write(
                ("Content-Disposition: form-data; name=\"" + parameterName +
                        "\"\r\n\r\n").getBytes()
        );
        outputStream.write((parameterValue + "\r\n").getBytes());
    }

    private static void writeFileToOutputStream(
            OutputStream outputStream,
            File file,
            String boundary
    ) throws IOException {
        outputStream.write(("--" + boundary + "\r\n").getBytes());
        outputStream.write(
                ("Content-Disposition: form-data; name=\"file\"; filename=\"" +
                        file.getName() + "\"\r\n").getBytes()
        );
        outputStream.write(("Content-Type: audio/wav\r\n\r\n").getBytes());

        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        fileInputStream.close();
    }

    private static String handleSuccessResponse(HttpURLConnection connection)
            throws IOException, JSONException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
        );
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject responseJson = new JSONObject(response.toString());

        return responseJson.getString("text");
    }

    private static void handleErrorResponse(HttpURLConnection connection)
            throws IOException, JSONException {
        BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(connection.getErrorStream())
        );

        String errorLine;
        StringBuilder errorResponse = new StringBuilder();
        while ((errorLine = errorReader.readLine()) != null) {
            errorResponse.append(errorLine);
        }

        errorReader.close();
        String errorResult = errorResponse.toString();

        System.err.println("Error Result: " + errorResult);
    }

    public static String getTextFromAudio(APIData WHISPER, String token,
                                   String organization, File file) throws IOException {
        URL url = new URL(WHISPER.endpoint());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String boundary = "Boundary-" + System.currentTimeMillis();
        connection.setRequestProperty(
                "Content-Type",
                "multipart/form-data; boundary=" + boundary
        );
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setRequestProperty("OpenAI-Organization", organization);

        OutputStream outputStream = connection.getOutputStream();

        WhisperAPIHelper.writeParameterToOutputStream(outputStream,
                "model", WHISPER.model(),
                boundary);
        WhisperAPIHelper.writeFileToOutputStream(outputStream, file,
                boundary);

        outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());

        outputStream.flush();
        outputStream.close();

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            return WhisperAPIHelper.handleSuccessResponse(connection);
        } else {
            WhisperAPIHelper.handleErrorResponse(connection);
            return null;
        }
    }
}