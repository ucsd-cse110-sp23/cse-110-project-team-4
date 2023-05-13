package org.agilelovers.backend;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class SayItAssistantTest {
    @Mock
    private SayItAssistant mock;

    private SayItAssistant assistant; //non mock object
    private File file;
    private String question;
    private String answer;

    @BeforeEach
    void setUp() {
        //mocks startRecording method
        mock = mock(SayItAssistant.class);
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
    void testObtainQuery() {
        // TODO: Write test
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
        when(mock.getTextFromAudio(file)).thenReturn("Who's in the CSE 110 Spring 2023 Team 4?");
        question = mock.getTextFromAudio(file);
        verify(mock).getTextFromAudio(file);
        Assertions.assertThat(question).isEqualTo("Who's in the CSE 110 Spring 2023 Team 4?");

    }

    @Test //mock test because we need to request from  openai to generate a response
    void testGetAnswer() throws IOException, InterruptedException {
        String prompt = mock.getTextFromAudio(file);
        when(mock.getAnswer(prompt)).thenReturn("The team members are: Billy, Lilian, Louie, Anish, Shera, and Nicholas.");
        answer = mock.getAnswer(prompt);
        verify(mock).getAnswer(prompt);
        Assertions.assertThat(answer).isEqualTo("The team members are: Billy, Lilian, Louie, Anish, Shera, and Nicholas.");
    }


}