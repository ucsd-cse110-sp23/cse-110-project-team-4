package org.agilelovers.ui;

public class Constants {
    public final static String SERVER_URL = "https://server.agilelovers.org";
    public final static String USER_ENDPOINT = "/api/users/";
    public final static String QUESTION_ENDPOINT = "/api/questions/";
    public final static String EMAIL_ENDPOINT = "/api/emails/";
    public final static String API_TRANSCRIBE_ENDPOINT = "/api/assistant/";
    public final static String QUESTION_DELETION_ENDPOINT = "/api/questions/delete/";
    public final static String EMAIL_DELETION_ENDPOINT = "/api/emails/delete/";
    public final static String DELETE_ALL_QUESTIONS_ENDPOINT = "/api/questions/delete-all/";
    public final static String DELETE_ALL_EMAILS_ENDPOINT = "/api/emails/delete-all/";
    public final static String QUESTION_COMMAND = "QUESTION";
    public final static String DELETE_PROMPT_COMMAND = "DELETE_PROMPT";
    public final static String CLEAR_ALL_COMMAND = "CLEAR_ALL";
    public final static String SETUP_EMAIL_COMMAND = "SETUP_EMAIL";
    public final static String CREATE_EMAIL_COMMAND = "CREATE_EMAIL";
    public final static String SEND_EMAIL_COMMAND = "SEND_EMAIL";
    public final static String USER_TOKEN_PATH = "./user_token.txt";
    public final static String RECORDING_PATH = ".//recording.wav";
}
