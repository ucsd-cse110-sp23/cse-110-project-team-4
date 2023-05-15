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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class SayItAssistantTest {
    @Mock
    private SayItAssistant mock;

    private TestWhisper mockWhisperAPIHelper;
    private TestChatGPT mockChatGPTHelper;
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

    SayItAssistantTest() {
    }

    @BeforeEach
    void setUp() {
        //mocks startRecording method
        mock = mock(SayItAssistant.class);
        mockWhisperAPIHelper = new TestWhisper();
        mockChatGPTHelper = new TestChatGPT();

        assistant = SayItAssistant.assistant;
        file = new File("assets/recording.wav");
        mock.audioFile = file;
    }

    /*
    tests for *startRecording()* and *endRecording()* are commented out because
    1) startRecording() is trivial
    2) endRecording() generates a response from OpenAI, which is not a mock
        nor is it desired since it will consume tokens
     */


    @Test
    void testObtainQuery() throws IOException {
        //mocks obtainQuery method
        question = "Who's in the CSE 110 Spring 2023 Team 4?";
        when(mock.obtainQuery(question)).thenReturn(new Question("Who's in the CSE 110 Spring 2023 Team 4?",
                "Who's in the CSE 110 Spring 2023 Team 4?",
                "The team members are: Billy, Lilian, Louie, Anish, Shera, and Nicholas."));
        String query = String.valueOf(mock.obtainQuery(question));
        Assertions.assertThat(query).isEqualTo("Who's in the CSE 110 Spring 2023 Team 4?");
    }


    /*
        have to comment this test out because endRecording() currently consumes
        tokens by generating a response
        -- it calls getAnswer()
    @Test
    void testGetTextFromAudio() throws IOException {
        question = assistant.getTextFromAudio(file);
        Assertions.assertThat(question).isEqualTo("Who's in the CSE 110 Spring 2023 Team 4?");
    }
     */

    @Test
    void testGetMockTextFromAudio() throws IOException {
        question = mockWhisperAPIHelper.getTextFromAudio(WHISPER, TOKEN, ORGANIZATION, file);
        Assertions.assertThat(question).isEqualTo("Who's in the CSE 110 Spring 2023 Team 4?");
    }

    @Test //mock test because we need to request from  openai to generate a response
    void testGetAnswer() throws IOException, InterruptedException {
        String prompt = mockWhisperAPIHelper.getTextFromAudio(WHISPER, TOKEN, ORGANIZATION, file);
        answer = mockChatGPTHelper.getAnswer(CHATGPT, TOKEN, ORGANIZATION, prompt);
        Assertions.assertThat(answer).isEqualTo("The team members are: Billy, Lilian, Louie, Anish, Shera, and Nicholas.");
    }

    @Test
    void testSavedQuery() {

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