package org.agilelovers.backend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

import io.github.cdimascio.dotenv.Dotenv;

record APIData(String endpoint, String model) { }

public class SayItAssistant {

    private class OutputStreamFormatter {
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
    }
    private static final APIData WHISPER = new APIData("https://api.openai" +
            ".com/v1/audio/transcriptions", "whisper-1");

    private static final APIData CHATGPT = new APIData("https://api.openai" +
            ".com/v1/completions", "text-davinci-003");
    private final String TOKEN;
    private final String ORGANIZATION;

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

    public String getTextFromAudio(File file) throws IOException {
        URL url = new URL(WHISPER.endpoint());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String boundary = "Boundary-" + System.currentTimeMillis();
        connection.setRequestProperty(
                "Content-Type",
                "multipart/form-data; boundary=" + boundary
        );
        connection.setRequestProperty("Authorization", "Bearer " + TOKEN);
        connection.setRequestProperty("OpenAI-Organization", ORGANIZATION);

        OutputStream outputStream = connection.getOutputStream();

        OutputStreamFormatter.writeParameterToOutputStream(outputStream,
                "model", WHISPER.model(),
                boundary);
        OutputStreamFormatter.writeFileToOutputStream(outputStream, file,
                boundary);

        outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());

        outputStream.flush();
        outputStream.close();

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            return handleSuccessResponse(connection);
        } else {
            handleErrorResponse(connection);
            return null;
        }
    }

    public String getAnswer(String question)
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
                .header("Authorization", String.format("Bearer %s", TOKEN))
                .header("OpenAI-Organization", ORGANIZATION)
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

    SayItAssistant() {
        Dotenv dotenv = Dotenv.load();
        this.TOKEN = dotenv.get("OPENAI_API_KEY");
        this.ORGANIZATION = dotenv.get("OPENAI_ORG");
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        SayItAssistant assistant = new SayItAssistant();

        File audioFile = new File("./recording.wav");
        AudioRecorder recorder = new AudioRecorder(audioFile);
        Scanner sc = new Scanner(System.in);

        recorder.start();
        System.out.print("STARTED RECORDING, ENTER ANYTHING TO STOP " +
                "RECORDING: ");
        while (!sc.hasNextLine()) {};
        recorder.stop();
        System.out.println("STOPPED RECORDING");

        String question = assistant.getTextFromAudio(audioFile);
        String response = assistant.getAnswer(question);
        System.out.println(response);

        audioFile.deleteOnExit();
    }


}
