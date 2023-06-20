package org.agilelovers.ui.util;

import javafx.application.Platform;
import org.agilelovers.ui.Constants;
import org.agilelovers.ui.controller.MainController;

import java.io.File;
import java.io.IOException;

public class RecordingUtils {
    private static File audioFile = new File(Constants.RECORDING_PATH);
    private static AudioRecorder recorder = new AudioRecorder(audioFile);


    public static void endRecording(MainController controller, String id) throws IOException {
        // new thread for operations
        recorder.stop();
        Platform.runLater(() -> controller.setRecordingLabel(false));
    }

    public static void startRecording(MainController controller) {
        controller.setRecordingLabel(true);
        new Thread(() -> recorder.start()).start();
    }
}
