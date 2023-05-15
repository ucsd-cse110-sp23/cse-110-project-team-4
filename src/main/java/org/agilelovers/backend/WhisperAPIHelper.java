package org.agilelovers.backend;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * WhisperAPI helper class to get transcribed test from Whisper API (used by SayItAssistant)
 */
public class WhisperAPIHelper {
    /**
     * Writes parameter to the output stream in multipart form data format.
     *
     * @param outputStream output stream to write to
     * @param parameterName name of the parameter
     * @param parameterValue value of the parameter
     * @param boundary boundary to separate the parameters
     * @throws IOException if any I/O error occurs
     */
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

    /**
     * Writes file to the output stream in multipart form data format.
     *
     * @param outputStream output stream to write to
     * @param file file to write
     * @param boundary boundary to separate the parameters
     * @throws IOException if any I/O error occurs
     */
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

    /**
     * Handles a successful response from the connection's input stream and builds a string that contains
     * the response data
     *
     * @param connection connection to get the input stream from
     * @return String containing the response data
     * @throws IOException if any I/O error occurs
     * @throws JSONException if any JSON error occurs
     */
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

    /**
     * Handles an error response from the connection's error stream and prints the error response
     *
     * @param connection connection to get the input stream from
     * @throws IOException if any I/O error occurs
     * @throws JSONException if any JSON error occurs
     */
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

    /**
     * Gets text from audio.
     * Uses the Whisper API to convert the given audio file to text
     *
     * @param WHISPER      API data for Whisper API
     * @param token        token to make API request
     * @param organization the organization
     * @param file         the file containing audio to be transcribed
     * @return String containing the text transcribed from given file
     * @throws IOException if any I/O error occurs
     */
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