package org.agilelovers.ui.util;

import org.agilelovers.ui.Constants;
import org.agilelovers.ui.object.AudioRecorder;
import org.agilelovers.ui.object.Question;

import java.io.File;
import java.io.IOException;

public class RecordingUtils {
    private static File audioFile = new File(Constants.RECORDING_PATH);
    private static AudioRecorder recorder = new AudioRecorder(audioFile);


    public static Question endRecording(String id, Question ques) {
        // new thread for operations
        recorder.stop();

        String question = null;
        String temp = "";
        char upper = 0;
        try {
            question = FrontEndAPIUtils.sendAudio(id);
            System.out.println("Current question: " + question);
            for (String s : question.split("")) {
                if (upper == 0) {
                    upper = (char) (question.charAt(0) - 32);
                    temp = temp + upper;
                } else temp = temp + s;
            }
            question = temp;
        } catch (IOException e) {
            System.err.println(question);
            throw new RuntimeException(e);
        }
        ques.setQuestion(question);
        return ques;
    }

    public static void startRecording() {
        new Thread(() -> recorder.start()).start();
    }
}
