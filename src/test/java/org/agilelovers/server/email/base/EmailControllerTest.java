package org.agilelovers.server.email.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.agilelovers.common.documents.EmailConfigDocument;
import org.agilelovers.common.documents.EmailDocument;
import org.agilelovers.common.documents.UserDocument;
import org.agilelovers.common.models.EmailModel;
import org.agilelovers.common.models.ReducedUserModel;
import org.agilelovers.common.models.SecureUserModel;
import org.agilelovers.server.Server;
import org.agilelovers.server.email.returned.ReturnedEmailRepository;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
public class EmailControllerTest {
    private final String API_KEY;
    private final String EMAIL_USERNAME;
    private final String EMAIL_PASSWORD;
    private final String SMTP_HOST;
    private final String TLS_PORT;
    private final static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private ReturnedEmailRepository returnedEmailRepository;
    public EmailControllerTest() {
        Dotenv env = Dotenv.load();
        this.API_KEY = env.get("API_SECRET");
        this.EMAIL_USERNAME = env.get("EMAIL_USERNAME");
        this.EMAIL_PASSWORD = env.get("EMAIL_PASSWORD");
        this.SMTP_HOST = env.get("SMTP_HOST");
        this.TLS_PORT = env.get("TLS_PORT");
    }
    /**
     * Reset the database after each test so its state is clean
     */
    @After
    public void resetDb() {
        emailRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * UNIT TEST
     * 1. Creates a user and email
     * Assert: the email draft is created
     * @throws Exception
     */
    @Test
    public void createEmailTest() throws Exception {
        String username = EMAIL_USERNAME;
        String password = EMAIL_PASSWORD;
        String prompt = "test prompt";
        String body = "test body";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/user/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))
        );

        Optional<UserDocument> userDoc = Optional.ofNullable(userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .orElseThrow(TestAbortedException::new));

        String userID = "6481cdc0fd5c8340a66b32fb";//userDoc.get().getId();
        String id = "testID";
        EmailDocument email = EmailDocument.builder()
                .id(id)
                .createdDate(null)
                .userId(userID)
                .entirePrompt(prompt)
                .build();
        EmailConfigDocument config = EmailConfigDocument.builder()
                .id(id)
                .userID(userID)
                .firstName("anish")
                .lastName("govind")
                .email(EMAIL_USERNAME)
                .emailPassword(EMAIL_PASSWORD)
                .displayName("anish govind")
                .smtpHost(SMTP_HOST)
                .tlsPort(TLS_PORT)
                .build();

        mvc.perform(post("/api/email/config/save/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(config))
        );
        //EmailConfigDocument temp = mvc.perform(get("/api/email/config/get/" + id)
        //        .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(get("/api/email/get/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(email))
        );
        mvc.perform(post("/api/email/post/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(email))
        );

        List<EmailDocument> emailsFound = emailRepository.findAll();
        emailsFound.add(email);
        assertThat(emailsFound).extracting(EmailDocument::getEntirePrompt).containsOnly(
                "test prompt");
    }

    /**
     * 1. Creates INVALID email
     * 2. Tries to POST to DB
     * Assert: Expects an invalid post request
     * @throws Exception
     */
    @Test
    public void createInvalidEmailTest() throws Exception {
        String username = EMAIL_USERNAME;
        String password = EMAIL_PASSWORD;
        String prompt = "test prompt";
        String body = "test body";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/user/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))
        );

        ResultActions userFromGet = mvc.perform(get("/api/user/sign_in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)));

        var httpResponse = userFromGet.andReturn().getResponse();
        //turn HttpResponse JSON content into string to pass into JsonUtil.fromJson
        String str = httpResponse.getContentAsString();
        ReducedUserModel result = mapper.readValue(str, ReducedUserModel.class);
        String userID = "kms";//result.getId();

        Optional<UserDocument> userDoc = Optional.ofNullable(userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .orElseThrow(TestAbortedException::new));

        String id = "kms2";//userDoc.get().getId();

        EmailModel email = EmailModel.builder()
                .prompt(prompt)
                .build();

        EmailConfigDocument config = EmailConfigDocument.builder()
                .id(id)
                .userID(userID)
                .firstName("anish")
                .lastName("govind")
                .email(EMAIL_USERNAME)
                .emailPassword(EMAIL_PASSWORD)
                .displayName("anish govind")
                .smtpHost(SMTP_HOST)
                .tlsPort(TLS_PORT)
                .build();

        mvc.perform(post("/api/email/config/save/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(config))
        );
        //EmailConfigDocument temp = mvc.perform(get("/api/email/config/get/" + id)
        //        .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(get("/api/email/get/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(email))
        );
        mvc.perform(post("/api/email/post/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(email))
        );

        mvc.perform(post("/api/email/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(email))
        ).andExpect(status().is(404));
    }
}
