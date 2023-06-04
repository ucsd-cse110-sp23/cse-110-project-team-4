package org.agilelovers.ui.util;

import org.agilelovers.ui.Constants;
import org.agilelovers.ui.object.AudioRecorder;
import org.agilelovers.ui.object.Query;
import org.agilelovers.ui.object.Question;

import java.io.File;
import java.io.IOException;

public class RecordingUtils {
    private static File audioFile = new File(Constants.RECORDING_PATH);
    private static AudioRecorder recorder = new AudioRecorder(audioFile);


    public static Query endRecording(String id, Question ques) throws IOException {
        // new thread for operations
        recorder.stop();

        return FrontEndAPIUtils.sendAudio(id);
    }

    public static void startRecording() {
        new Thread(() -> recorder.start()).start();
    }
}
