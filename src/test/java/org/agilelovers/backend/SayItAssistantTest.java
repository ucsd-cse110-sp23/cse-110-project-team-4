package org.agilelovers.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class SayItAssistantTest {

    private SayItAssistant assistant;
    private File file;
    private String question;

    @BeforeEach
    void setUp() {
        assistant = SayItAssistant.assistant;
        file = new File("recording.m4a");
        assistant.audioFile = file;
    }

    @Test
    void testEndRecording() throws IOException, InterruptedException {
        /* you can delete this if you'd like
        assistant.endRecording();
        Assertions.assertThat(assistant.audioFile.exists()).isFalse();
        Assertions.assertThat(assistant.getAnswer(question)).isNotNull();
         */
    }

    @Test
    void testObtainQuery() {
        // TODO: Write test
    }

    @Test
    void testGetTextFromAudio() throws IOException {
        /* you can delete this if you'd like
        String question = assistant.getTextFromAudio(file);
        Assertions.assertThat(question).isEqualTo("test test test");

         */
    }

    /*
    Can't make this test because we don't have unlimited tokens.
    Making this test case would result in a unfriendly use of our limited tokens.

    @Test
    void testGetAnswer() {
        ...
    }
     */
}