package org.agilelovers.ui;

public class Constants {
    public final static String SERVER_URL = "http://localhost:8080";
    public final static String USER_ENDPOINT = "/api/users/";
    public final static String QUESTION_ENDPOINT = "/api/questions/";
    public final static String API_TRANSCRIBE_ENDPOINT = "/api/assistant/";
    public final static String DELETION_ENDPOINT = "/api/questions/delete/";
    public final static String DELETE_ALL_ENDPOINT = "/api/questions/delete-all/";
    public final static String QUESTION_COMMAND = "question";
    public final static String DELETE_PROMPT_COMMAND = "delete prompt";
    public final static String CLEAR_ALL_COMMAND = "clear all";
    public final static String SETUP_EMAIL_COMMAND = "setup email";
    public final static String CREATE_EMAIL_COMMAND = "create email";
    public final static String SEND_EMAIL_COMMAND = "send email";
    public final static String USER_TOKEN_PATH = "./user_token.txt";
    public final static String RECORDING_PATH = ".//recording.wav";
}
