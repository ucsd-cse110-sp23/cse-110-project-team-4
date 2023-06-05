package org.agilelovers.server.assistant.errors;

public class NoAudioError extends RuntimeException{
    public NoAudioError() {
        super("nothing was said in the audio file");
    }
}
