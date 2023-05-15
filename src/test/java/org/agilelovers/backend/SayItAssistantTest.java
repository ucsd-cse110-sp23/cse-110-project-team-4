package org.agilelovers.backend;

import org.agilelovers.ui.object.Question;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class SayItAssistantTest {
    @Mock
    private SayItAssistant mock;

    MockDatabase queryDatabase;


    private static final APIData WHISPER = new APIData("https://api.openai" +
            ".com/v1/audio/transcriptions", "whisper-1");

    private static final APIData CHATGPT = new APIData("https://api.openai" +
            ".com/v1/completions", "text-davinci-003");
    private final String TOKEN = "sk_test_123";
    private final String ORGANIZATION = "org_123";

    private SayItAssistant assistant; //non mock object
    private File file;
    private String question;
    private String answer;


    @BeforeEach
    void setUp() throws IOException {
        //mocks startRecording method
        mock = mock(SayItAssistant.class);
        queryDatabase = new MockDatabase();


        assistant = SayItAssistant.assistant;
        file = new File("assets/recording.wav");
        mock.audioFile = file;
    }

    @Test
    void testGetDatabaseQuestions() {
        // Arrange
        List<Question> expectedQuestions = new ArrayList<>();
        expectedQuestions.add(new Question("Title1", "Question1", "Answer1"));
        expectedQuestions.add(new Question("Title2", "Question2", "Answer2"));

        when(mock.getDatabaseQuestions()).thenReturn(expectedQuestions);

        // Act
        List<Question> actualQuestions = mock.getDatabaseQuestions();
        System.out.println(actualQuestions);

        // Assert
        verify(mock).getDatabaseQuestions();
        Assertions.assertThat(actualQuestions).isEqualTo(expectedQuestions);
    }

    @Test
    void testDatabaseObtainQuestions() {
        List<Question> expectedQuestions = new ArrayList<>();
        expectedQuestions.add(new Question("title2", "question2", "answer2"));
        expectedQuestions.add(new Question("title3", "question3", "answer3"));
        expectedQuestions.add(new Question("title1", "question1", "answer1"));

        List<Question> actualQuestions = queryDatabase.obtainQuestions();
        Assertions.assertThat(actualQuestions.size()).isEqualTo(expectedQuestions.size());
        Assertions.assertThat(actualQuestions.get(0).toString()).hasToString(expectedQuestions.get(0).toString());
        Assertions.assertThat(actualQuestions.get(1).toString()).hasToString(expectedQuestions.get(1).toString());
        Assertions.assertThat(actualQuestions.get(2).toString()).hasToString(expectedQuestions.get(2).toString());
    }

    @Test
    void testDatabaseDeleteQuestion() throws IOException {
        List<Question> expectedQuestions = new ArrayList<>();
        expectedQuestions.add(new Question("title2", "question2", "answer2"));
        expectedQuestions.add(new Question("title3", "question3", "answer3"));
        expectedQuestions.add(new Question("title1", "question1", "answer1"));

        queryDatabase.deleteQueryFromFile("question1");
        List<Question> actualQuestions = queryDatabase.obtainQuestions();
        Assertions.assertThat(actualQuestions.size()).isEqualTo(expectedQuestions.size());
        Assertions.assertThat(actualQuestions.get(0).toString()).hasToString(expectedQuestions.get(0).toString());
        Assertions.assertThat(actualQuestions.get(1).toString()).hasToString(expectedQuestions.get(1).toString());
    }

    @Test
    void testGetMockTextFromAudio() throws IOException {
        question = TestWhisper.getTextFromAudio(WHISPER, TOKEN, ORGANIZATION, file);
        Assertions.assertThat(question).isEqualTo("Who's in the CSE 110 Spring 2023 Team 4?");
    }

    @Test //mock test because we need to request from  openai to generate a response
    void testGetAnswer() throws IOException, InterruptedException {
        String prompt = TestWhisper.getTextFromAudio(WHISPER, TOKEN, ORGANIZATION, file);
        answer = TestChatGPT.getAnswer(CHATGPT, TOKEN, ORGANIZATION, prompt);
        Assertions.assertThat(answer).isEqualTo("The team members are: Billy, Lilian, Louie, Anish, Shera, and Nicholas.");
    }


    static class TestWhisper extends WhisperAPIHelper{
        public static String getTextFromAudio(APIData data, String token, String organization, File file) {
            return "Who's in the CSE 110 Spring 2023 Team 4?";
        }
    }

    static class TestChatGPT extends ChatGPTHelper {
        public static String getAnswer(APIData data, String token, String organization, String prompt) {
            return "The team members are: Billy, Lilian, Louie, Anish, Shera, and Nicholas.";
        }
    }
}