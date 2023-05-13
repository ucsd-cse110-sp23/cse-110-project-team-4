package org.agilelovers.backend;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.fxml.FXMLLoader;
import org.agilelovers.ui.Controller;
import org.agilelovers.ui.object.Question;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Iterator;
import java.util.Objects;


record APIData(String endpoint, String model) {
}


public class SayItAssistant {
    public static SayItAssistant assistant = new SayItAssistant();

    // reference variable to the list view
    private FXMLLoader fxmlLoader;

    public void setFXMLLoader(FXMLLoader loader) {
        this.fxmlLoader = loader;
    }

    private static final APIData WHISPER = new APIData("https://api.openai" +
            ".com/v1/audio/transcriptions", "whisper-1");

    private static final APIData CHATGPT = new APIData("https://api.openai" +
            ".com/v1/completions", "text-davinci-003");
    private final String TOKEN;
    private final String ORGANIZATION;
    private final File queryDataBase;
    File audioFile;
    private AudioRecorder recorder;

    private void writeToFile(String encodedKey, JSONObject jsonObject)
            throws IOException {

        // for multi-thread - if we ever wanted to
        /*
        synchronized (queryDataBase) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter
            (queryDataBase, true))) {
                writer.write(jsonObject.toString());
                writer.newLine(); // add a newline character after each JSON
                object
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

         */

        synchronized (queryDataBase) {
            // Read existing contents of file into a JSONObject
            JSONObject existingData = new JSONObject();
            if (queryDataBase.exists()) {
                try (BufferedReader reader = new BufferedReader(
                        new FileReader(queryDataBase))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        JSONObject data = new JSONObject(line);

                        Iterator<String> keys = data.keys();
                        while (keys.hasNext()) {
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
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(queryDataBase))) {
                for (String existingKey : existingData.keySet()) {
                    JSONObject currLine = new JSONObject();
                    currLine.put(existingKey,
                            existingData.getJSONObject(existingKey));
                    writer.write(currLine.toString());
                    writer.newLine();
                }
            }
        }
    }

    private void deleteFromFile(String questionQuery) throws IOException {

        synchronized (queryDataBase) {
            // Read existing contents of file into a JSONObject
            JSONObject existingData = new JSONObject();
            if (queryDataBase.exists()) {
                try (BufferedReader reader = new BufferedReader(
                        new FileReader(queryDataBase))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        JSONObject data = new JSONObject(line);

                        Iterator<String> keys = data.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            JSONObject answer = data.getJSONObject(key);
                            existingData.put(key, answer);
                        }

                    }
                }
            }

            String encodedKey = encodeQuery(questionQuery);
            // Update or add new key-value pair to JSONObject
            existingData.remove(encodedKey);

            // Write updated JSONObject to file
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(queryDataBase))) {
                for (String existingKey : existingData.keySet()) {
                    JSONObject currLine = new JSONObject();
                    currLine.put(existingKey,
                            existingData.getJSONObject(existingKey));
                    writer.write(currLine.toString());
                    writer.newLine();
                }
            }
        }
    }

    private String encodeQuery(String query) {
        byte[] query_bytes = query.getBytes();
        byte[] query_bytesEncoded = Base64.getEncoder().encode(query_bytes);
        return new String(query_bytesEncoded);
    }

    private void transcribeQueryIntoFile(String title, String questionQuery,
                                         String answerQuery)
            throws IOException {

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
        this.audioFile = new File("./recording.wav");
        this.queryDataBase = new File("AgileLovers_DB");
        this.recorder = new AudioRecorder(audioFile);
        this.audioFile.deleteOnExit();
    }

    public void startRecording() {
        new Thread(() -> recorder.start()).start();
    }

    public void startRecording(AssistantCallback callback) {
        callback.start();
        startRecording();
        callback.end();
    }

    public Question endRecording() {
        Question ques = new Question();

        // new thread for operations
        Thread thread = new Thread(() -> {

            recorder.stop();

            String question;
            String temp = "";
            char upper = 0;
            try {
                question =
                        Objects.requireNonNull(
                                WhisperAPIHelper.getTextFromAudio(WHISPER,
                                        TOKEN, ORGANIZATION, audioFile)).toLowerCase();
                for (String s : question.split("")) {
                    if (upper == 0) {
                        upper = (char) (question.charAt(0) - 32);
                        temp = temp + upper;
                    } else temp = temp + s;
                }
                question = temp;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ques.setQuestion(question);
            ((Controller) this.fxmlLoader.getController()).refreshLabels();

            String prompt = question;
            String response = null;

            try {
                response = ChatGPTHelper.getAnswer(CHATGPT, TOKEN,
                        ORGANIZATION, prompt);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            //String[] split_response = response.split(Pattern.quote("*"), 2);
            String title = question;//split_response[0].replaceAll("\n", "");
            String answerToQuestion = response;//split_response[1];
            // bug here

            try {
                assistant.transcribeQueryIntoFile(title, question,
                        answerToQuestion);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println(answerToQuestion);
            System.out.println(title);
            ques.setAnswer(answerToQuestion);
            ques.setTitle(title);

            // refresh
            ((Controller) this.fxmlLoader.getController()).getHistoryList()
                    .refresh();
            ((Controller) this.fxmlLoader.getController()).refreshLabels();
        });

        thread.start();
        return ques;
    }

    public Question endRecording(AssistantCallback callback) {
        callback.start();
        Question result = endRecording();
        callback.end();
        return result;
    }
}
