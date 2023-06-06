package org.agilelovers.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.agilelovers.ui.object.AudioRecorder;
import org.agilelovers.ui.object.Question;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Stores the API data for use in SayItAssistant class.
 * The first string contains the API endpoint, specific location in API to access/manipulate resources
 * The second string contains the specific model name
 */
record APIData(String endpoint, String model) {
}

/**
 * SayIt Assistant handles all the API requests and responses.
 */
public class SayItAssistant {
    /**
     * The constant assistant.
     */
    public static SayItAssistant assistant = new SayItAssistant();

    /**
     * API data for Whisper, for use within the SayItAssistant class
     */
    private static final APIData WHISPER = new APIData("https://api.openai" +
            ".com/v1/audio/transcriptions", "whisper-1");

    /**
     * API data for GPT-3.5, for use within the SayItAssistant class
     */
    private static final APIData CHATGPT = new APIData("https://api.openai" +
            ".com/v1/completions", "text-davinci-003");
    /**
     * API key used to make API requests
     */

    private final String TOKEN;
    /**
     * organization ID used to make API requests
     */
    private final String ORGANIZATION;
    /**
     * stores the database of queries (questions) and answers
     */
    private Database queryDatabase;
    /**
     * audio file to be transcribed by Whisper API
     */
    File audioFile;
    /**
     * AudioRecorder object to record audio to be sent to Whisper API
     */
    private AudioRecorder recorder;

    /**
     * Constructor for the SayItAssistant class.
     *
     * Initializes the TOKEN, ORGANIZATION, audioFile, queryDataBase, and recorder.
     */
    private SayItAssistant() {
        Dotenv dotenv = Dotenv.load();
        this.TOKEN = dotenv.get("OPENAI_API_KEY");
        this.ORGANIZATION = dotenv.get("OPENAI_ORG");
        this.audioFile = new File("./recording.wav");
        this.queryDatabase = new Database(new File("AgileLovers_DB"));
        this.recorder = new AudioRecorder(audioFile);
        this.audioFile.deleteOnExit();
    }

    /**
     * Starts the recording of the user's voice.
     *
     * Creates a new thread to start the recording.
     */
    public void startRecording() {
        new Thread(() -> recorder.start()).start();
    }

    /**
     * Stops the recording and transcribes the audio into text.
     *
     * Ends the recording to send to Whisper API, then sends the question transcribed by the Whisper recording
     * and sends it to the OpenAI API to generate a response.
     *
     * @return Question object with the question, answer, and title
     */
    public Question endRecording() {
        Question ques = new Question();

        // new thread for operations
        Thread thread = new Thread(() -> {

            recorder.stop();

            String question;
            String temp = "";
            char upper = 0;
            try {
                // set everything to lowercase and capitalize first letter
                question =
                        Objects.requireNonNull(
                                // get transcription of audio file
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
            ques.setTitle(question);

            String prompt = question;
            String response = null;

            // get response from GPT-3.5
            try {
                response = ChatGPTHelper.getAnswer(CHATGPT, TOKEN,
                        ORGANIZATION, prompt);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            String title = question;
            String answerToQuestion = response;

            // store question and answer in database
            try {
                assistant.queryDatabase.transcribeQueryIntoFile(title, question,
                        answerToQuestion);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println(answerToQuestion);
            System.out.println(title);
            ques.setBody(answerToQuestion);
        });

        thread.start();
        return ques;
    }

    /**
     * Gets stored questions
     *
     * @return list of questions currently in the database
     */
    public List<Question> getDatabaseQuestions() {
        System.out.println("Getting database questions");
        return queryDatabase.obtainQuestions();
    }

    /**
     * Deletes database question.
     *
     * @param question the question to be deleted
     * @throws IOException if the file is not found/cannot be opened
     */
    public void deleteDatabaseQuestion(Question question) throws IOException {
        queryDatabase.deleteQueryFromFile(question.getTitle());
    }
}
