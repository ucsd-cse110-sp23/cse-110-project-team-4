package org.agilelovers.backend;

import org.agilelovers.ui.object.Question;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

public class MockDatabase {
    private static File queryDatabase;

    public MockDatabase() throws IOException {
        queryDatabase = new File("MockDatabase.JSON");
        //write mock data to file
        transcribeQueryIntoFile("title1", "question1", "answer1");
        transcribeQueryIntoFile("title2", "question2", "answer2");
        transcribeQueryIntoFile("title3", "question3", "answer3");
    }

    /*
     * writeToFile() is used to write a specified query and its answer to the database file.
     *
     * It reads the queryDatabase file and adds the query and its answer to the file.
     * @param encodedKey the encoded key of the query
     * @param jsonObject the JSONObject of the query and its answer
     * @return void
     * @throws IOException if the file is not found
     */
    private void writeToFile(String encodedKey, JSONObject jsonObject)
            throws IOException {

        // for multi-thread - if we ever wanted to
        /*
        synchronized (queryDatabase) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter
            (queryDatabase, true))) {
                writer.write(jsonObject.toString());
                writer.newLine(); // add a newline character after each JSON
                object
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

         */

        synchronized (queryDatabase) {
            // Read existing contents of file into a JSONObject
            JSONObject existingData = new JSONObject();
            if (queryDatabase.exists()) {
                try (BufferedReader reader = new BufferedReader(
                        new FileReader(queryDatabase))) {
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
                    new FileWriter(queryDatabase))) {
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

    /*
     * deleteFromFile() is used to delete a specified query from the database file.
     *
     * It reads the queryDatabase file and deletes the query that matches the questionQuery.
     * @param questionQuery the question query to be deleted from the file
     * @return void
     * @throws IOException if the file is not found
     */
    private void deleteFromFile(String questionQuery) throws IOException {
        synchronized (queryDatabase) {
            // Read existing contents of file into a JSONObject
            JSONObject existingData = new JSONObject();
            if (queryDatabase.exists()) {
                try (BufferedReader reader = new BufferedReader(
                        new FileReader(queryDatabase))) {
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
                    new FileWriter(queryDatabase))) {
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

    /*
     * encodeQuery() takes in the query and encodes it into a string of bytes.
     *
     * @param query - the query that the user wants to obtain the answer
     * @return String of bytes that represents the query
     */
    private String encodeQuery(String query) {
        byte[] query_bytes = query.getBytes();
        byte[] query_bytesEncoded = Base64.getEncoder().encode(query_bytes);
        return new String(query_bytesEncoded);
    }

    /*
     * transcribeQueryIntoFile() takes in the title, question and answer of the query and writes it into the queryDatabase file.
     *
     * It encodes the questionQuery into a string of bytes and uses that as the key for the queryDatabase file.
     * It then writes the title, question and answer into the queryDatabase file.
     *
     * @param title - the title of the query
     * @param questionQuery - the query that the user wants to obtain the answer
     * @param answerQuery - the answer to the query
     * @return void
     * @throws IOException if the file is not found
     */
    void transcribeQueryIntoFile(String title, String questionQuery,
                                 String answerQuery)
            throws IOException {

        JSONObject innerShell = new JSONObject();

        innerShell.put("Title", title);
        innerShell.put("Question", questionQuery);
        innerShell.put("Answer", answerQuery);

        String key_inBytes = encodeQuery(questionQuery);

        writeToFile(key_inBytes, innerShell);
    }

    void deleteQueryFromFile(String questionQuery) throws IOException {
        deleteFromFile(questionQuery);
    }
    /*
     * obtainQuery() returns a Question object that contains the title, question and answer of the query.
     *
     * It reads the queryDatabase file and returns the Question object that matches the query.
     *
     * @param questionQuery - the query that the user wants to obtain the answer
     * @return Question object that contains the title, question and answer of the query
     * @throws IOException if the file is not found
     */
    //temporary removal of method obtainQuery(...)
    /* public Question obtainQuery(String questionQuery) throws IOException {

        String jsonStr = new String(Files.readAllBytes(queryDatabase.toPath()));
        JSONObject tempJSON = new JSONObject(jsonStr);

        String key_inBytes = encodeQuery(questionQuery);
        JSONObject queryValue = tempJSON.getJSONObject(key_inBytes);

        String title = queryValue.getString("Title");
        String question = queryValue.getString("Question");
        String answer = queryValue.getString("Answer");


        return new Question(title, question, answer);
    }
    */

    public static List<Question> obtainQuestions() {
        List<Question> questions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(queryDatabase))) {
            String line;
            while ((line = br.readLine()) != null) {
                JSONObject queryValue = new JSONObject(line);
                String key = queryValue.keys().next();

                String title = queryValue.getJSONObject(key).getString("Title");
                String question = queryValue.getJSONObject(key).getString("Question");
                String answer = queryValue.getJSONObject(key).getString("Answer");

                questions.add(new Question(title, question, answer));
            }
        } catch (IOException e) { e.printStackTrace();}

        return questions;
    }
}
