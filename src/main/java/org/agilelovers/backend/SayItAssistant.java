package org.agilelovers.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.agilelovers.ui.object.Question;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

record APIData(String endpoint, String model) {
}

public class SayItAssistant {
    public static SayItAssistant assistant = new SayItAssistant();

    // reference variable to the list view

    private static final APIData WHISPER = new APIData("https://api.openai" +
            ".com/v1/audio/transcriptions", "whisper-1");

    private static final APIData CHATGPT = new APIData("https://api.openai" +
            ".com/v1/completions", "text-davinci-003");
    private final String TOKEN;
    private final String ORGANIZATION;
    private Database queryDatabase;
    File audioFile;
    private AudioRecorder recorder;

    /*
     * SayItAssistant() is the constructor for the SayItAssistant class.
     *
     * It initializes the TOKEN, ORGANIZATION, audioFile, queryDataBase, and recorder.
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

    /*
     * startRecording() starts the recording of the user's voice.
     *
     * It creates a new thread to start the recording.
     */
    public void startRecording() {
        new Thread(() -> recorder.start()).start();
    }

    /*
     * endRecording() stops the recording and transcribes the audio into text.
     *
     * It grabs the question prompted by the Whisper recording and sends it to the OpenAI API to generate a response
     * to the corresponding transcribed text.
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

            String prompt = question;
            String response = null;

            try {
                response = ChatGPTHelper.getAnswer(CHATGPT, TOKEN,
                        ORGANIZATION, prompt);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            String title = question;
            String answerToQuestion = response;

            try {
                assistant.queryDatabase.transcribeQueryIntoFile(title, question,
                        answerToQuestion);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println(answerToQuestion);
            System.out.println(title);
            ques.setAnswer(answerToQuestion);
            ques.setTitle(title);
        });

        thread.start();
        return ques;
    }

    /*
     * getQueryDatabase() returns the queryDatabase list of Question objects.
     *
     * @return Database object
     */
    public List<Question> getDatabaseQuestions() throws IOException {
        System.out.println("Getting database questions");
        return queryDatabase.obtainQuestions();
    }

    public void deleteDatabaseQuestion(Question question) throws IOException {
        queryDatabase.deleteQueryFromFile(question.question());
    }
}
