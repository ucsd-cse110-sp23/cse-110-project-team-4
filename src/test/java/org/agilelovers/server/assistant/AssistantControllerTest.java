package org.agilelovers.server.assistant;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.agilelovers.common.CommandIdentifier;
import org.agilelovers.common.CommandType;
import org.agilelovers.common.documents.UserDocument;
import org.agilelovers.common.models.AssistantResponseModel;
import org.agilelovers.common.models.SecureUserModel;
import org.agilelovers.server.Server;
import org.agilelovers.server.common.OpenAIClient;
import org.agilelovers.server.email.base.EmailRepository;
import org.agilelovers.server.user.UserRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentest4j.TestAbortedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Server.class
)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class AssistantControllerTest {
    private final String API_KEY = Dotenv.load().get("API_SECRET");//"apikey";
    private final String EMAIL_USERNAME = Dotenv.load().get("EMAIL_USERNAME");;
    private final String EMAIL_PASSWORD = Dotenv.load().get("EMAIL_PASSWORD");;
    private final static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailRepository emailRepository;
    private OpenAIClient client;
    public AssistantControllerTest() {
        client = new OpenAIClient();
    }
    @After
    public void resetDb() {
        emailRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * UNIT TEST
     * 1. Tests invalid assistant: improper file format
     * Assert: 404 error
     * @throws Exception
     */
    @Test
    public void createAssistantTest() throws Exception {
        AssistantController hi = new AssistantController(userRepository);
        String transcription = "how was your day";
        String command = "question";
        String command_arguments;
        int starting_index = switch (command) {
            case CommandType.ASK_QUESTION -> CommandIdentifier.QUESTION_COMMAND.length() - 1;
            case CommandType.SEND_EMAIL -> CommandIdentifier.SEND_EMAIL_COMMAND.length() - 1;
            case CommandType.CREATE_EMAIL -> CommandIdentifier.CREATE_EMAIL_COMMAND.length() - 1;
            default -> 0;
        };
        while (transcription.charAt(starting_index + 1) != ' ') starting_index++;

        command_arguments = transcription.substring(starting_index + 1).strip();
        SecureUserModel user = SecureUserModel.builder()
                .username(EMAIL_USERNAME)
                .password(EMAIL_PASSWORD)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/user/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))
        );
        Optional<UserDocument> userDoc = Optional.ofNullable(userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .orElseThrow(TestAbortedException::new));

        String uid = userDoc.get().getId();
        AssistantResponseModel assistantResponse = AssistantResponseModel.builder()
                .transcribed(transcription)
                .command(command)
                .command_arguments(command_arguments)
                .build();

        mvc.perform(post("/ask/" + uid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(assistantResponse))
        ).andExpect(status().is(404));
    }

    /**
     * UNIT TEST
     * 1. Tests invalid assistant: null transcription
     * Assert: return 404
     * @throws Exception
     */
    @Test
    public void createBadAssistantTest() throws Exception {
        AssistantController hi = new AssistantController(userRepository);
        String transcription = null;
        String command = "question";
        String command_arguments = "bad arg";

        AssistantResponseModel assistantResponse = AssistantResponseModel.builder()
                .transcribed(transcription)
                .command(command)
                .command_arguments(command_arguments)
                .build();

        mvc.perform(post("/ask/" + "uid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(assistantResponse))
                ).andExpect(status().is(404));
    }
}
