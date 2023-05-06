package org.agilelovers.backend;

import javafx.fxml.FXMLLoader;
import org.agilelovers.ui.Controller;
import org.agilelovers.ui.object.Question;
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
import java.nio.file.Files;
import java.util.Base64;
import java.util.Iterator;
import java.util.regex.Pattern;

import io.github.cdimascio.dotenv.Dotenv;

record APIData(String endpoint, String model) { }

public class SayItAssistant {

    public static SayItAssistant assistant = new SayItAssistant();

    // reference variable to the list view
    private FXMLLoader fxmlLoader;

    public void setFXMLLoader(FXMLLoader loader) {
        this.fxmlLoader = loader;
    }

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
    private final File queryDataBase;

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

    private void writeToFile(String encodedKey, JSONObject jsonObject) throws IOException {

        // for multi-thread - if we ever wanted to
        /*
        synchronized (queryDataBase) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(queryDataBase, true))) {
                writer.write(jsonObject.toString());
                writer.newLine(); // add a newline character after each JSON object
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

         */

        synchronized (queryDataBase) {
            // Read existing contents of file into a JSONObject
            JSONObject existingData = new JSONObject();
            if (queryDataBase.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(queryDataBase))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        JSONObject data = new JSONObject(line);

                        Iterator<String> keys = data.keys();
                        while(keys.hasNext()){
                            String key = keys.next();
                            JSONObject answer = data.getJSONObject(key);
                            existingData.put(key, answer);
                        }

                    }
                }
            }

            // Update or add new key-value pair to JSONObject
            existingData.put(encodedKey, jsonObject);

            // Write updated JSONObject to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(queryDataBase))) {
                for (String existingKey : existingData.keySet()) {
                    JSONObject currLine = new JSONObject();
                    currLine.put(existingKey, existingData.getJSONObject(existingKey));
                    writer.write(currLine.toString());
                    writer.newLine();
                }
            }
        }
    }

    private String encodeQuery(String query){
        byte[] query_bytes = query.getBytes();
        byte[] query_bytesEncoded = Base64.getEncoder().encode(query_bytes);
        return new String(query_bytesEncoded);
    }

    private void transcribeQueryIntoFile(String title, String questionQuery, String answerQuery) throws IOException {

        JSONObject innerShell = new JSONObject();

        innerShell.put("Title", title);
        innerShell.put("Question", questionQuery);
        innerShell.put("Answer", answerQuery);

        String key_inBytes = encodeQuery(questionQuery);

        writeToFile(key_inBytes, innerShell);
    }

    public Question obtainQuery(String questionQuery) throws IOException {

        String jsonStr = new String(Files.readAllBytes(queryDataBase.toPath()));
        JSONObject tempJSON = new JSONObject(jsonStr);

        String key_inBytes = encodeQuery(questionQuery);
        JSONObject queryValue = tempJSON.getJSONObject(key_inBytes);

        String title = queryValue.getString("Title");
        String question = queryValue.getString("Question");
        String answer = queryValue.getString("Answer");


        return new Question(title, question, answer);
    }


    private SayItAssistant() {
        Dotenv dotenv = Dotenv.load();
        this.TOKEN = dotenv.get("OPENAI_API_KEY");
        this.ORGANIZATION = dotenv.get("OPENAI_ORG");
        queryDataBase = new File("AgileLovers_DB");
    }

    private File audioFile;
    private AudioRecorder recorder;

    public void startRecording(){
        audioFile = new File("./recording.wav");
        recorder = new AudioRecorder(audioFile);

        recorder.start();
    }

    public Question endRecording(){
        Question ques = new Question();

        // new thread for operations
        var thread = new Thread(() -> {

            recorder.stop();

            String question = null;
            try {
                question = assistant.getTextFromAudio(audioFile).toLowerCase();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ques.setQuestion(question);
            ((Controller) this.fxmlLoader.getController()).refreshLabels();

            String prompt =
                    "In the first line of the response, provide a title for my query." +
                            " Following the title, provide the response for my query in a new line." +
                            " Follow the format:\n" +
                            "[title] * [answer]\n" +
                            question;

            String response = null;
            try {
                response = assistant.getAnswer(prompt);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            String[] split_response = response.split(Pattern.quote("*"), 2);
            String title = split_response[0].replaceAll("\n", "");
            String answerToQuestion = split_response[1];

            try {
                assistant.transcribeQueryIntoFile(title, question, answerToQuestion);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println(answerToQuestion);
            System.out.println(title);
            ques.setAnswer(answerToQuestion);
            ques.setTitle(title);


            audioFile.deleteOnExit();

            // refresh
            ((Controller) this.fxmlLoader.getController()).getHistoryList().refresh();
            ((Controller) this.fxmlLoader.getController()).refreshLabels();
        });

        thread.start();



        return ques;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
    /*
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


        String question = assistant.getTextFromAudio(audioFile).toLowerCase();

        String prompt =
                "In the first line of the response, provide a title for my query." +
                " Following the title, provide the response for my query in a new line." +
                " Follow the format:\n" +
                "[title] * [answer]\n" +
                question;

        String response = assistant.getAnswer(prompt);
        String[] split_response = response.split(Pattern.quote("*"), 2);

        String title = split_response[0].replaceAll("\n", "");
        String answerToQuestion = split_response[1];

        //multi-thread file writing?
        assistant.transcribeQueryIntoFile(title, question, answerToQuestion);

        audioFile.deleteOnExit();

     */
    }


}
