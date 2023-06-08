package org.agilelovers.server.email;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.agilelovers.common.documents.ReturnedEmailDocument;
import org.agilelovers.common.models.EmailConfigModel;
import org.agilelovers.common.models.EmailModel;
import org.agilelovers.common.models.ReducedUserModel;
import org.agilelovers.common.models.SecureUserModel;
import org.agilelovers.server.Server;
import org.agilelovers.common.documents.EmailDocument;
import org.agilelovers.server.email.base.EmailRepository;
import org.agilelovers.common.documents.EmailConfigDocument;
import org.agilelovers.server.email.returned.ReturnedEmailRepository;
import org.agilelovers.common.documents.UserDocument;
import org.agilelovers.server.user.UserRepository;
import org.junit.After;
import org.junit.Before;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private SecureUserModel user;
    private EmailConfigModel configDocument;
    private String userID;
    public EmailControllerTest() {
        Dotenv env = Dotenv.load();
        this.API_KEY = env.get("API_SECRET");
        this.EMAIL_USERNAME = env.get("EMAIL_USERNAME");
        this.EMAIL_PASSWORD = env.get("EMAIL_PASSWORD");
        this.SMTP_HOST = env.get("SMTP_HOST");
        this.TLS_PORT = env.get("TLS_PORT");
    }


    @Before
    public void setUp() throws Exception {
        user = SecureUserModel.builder()
                .username(EMAIL_USERNAME)
                .password(EMAIL_PASSWORD)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/user/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))
        );

        // Step 2: post a config to database under a specific user ID
        userID = userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .orElseThrow(TestAbortedException::new).getId();

        configDocument = EmailConfigModel.builder()
                .firstName("Anish")
                .lastName("Govind")
                .email(EMAIL_USERNAME)
                .emailPassword(EMAIL_PASSWORD)
                .displayName("Anish Display Govind")
                .smtpHost(SMTP_HOST)
                .tlsPort(TLS_PORT)
                .build();

        mvc.perform(post("/api/email/config/save/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
        );
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

        UserDocument userDocument = userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .orElseThrow(TestAbortedException::new);

        EmailModel emailModel = EmailModel.builder()
                .prompt("create email to nick do you want to go to geisel later")
                .build();

        ResultActions createdEmail = mvc.perform(post("/api/email/post/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(emailModel))
        );

        assertThat(this.emailRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void getAllEmailsTest() throws Exception {

        EmailModel emailModel1 = EmailModel.builder()
                .prompt("create email to nick do you want to go to geisel later")
                .build();

        mvc.perform(post("/api/email/post/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(emailModel1))
        );

        EmailModel emailModel2 = EmailModel.builder()
                .prompt("create email to Anish do you want to go to geisel later")
                .build();

        mvc.perform(post("/api/email/post/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(emailModel2))
        );


        ResultActions listOfEmailsInDataBase = mvc.perform(get("/api/email/get/all/" + userID));
        var httpResponse = listOfEmailsInDataBase.andReturn().getResponse();
        //turn HttpResponse JSON content into string to pass into JsonUtil.fromJson
        String str = httpResponse.getContentAsString();
        List<EmailDocument> result = mapper.readValue(str, new TypeReference<>() {});

        assertThat(result.size()).isEqualTo(2);

    }

    @Test
    public void getEmailbyId() throws Exception{
        EmailModel emailModel1 = EmailModel.builder()
                .prompt("create email to nick do you want to go to geisel later")
                .build();

        ResultActions createdEmail1 = mvc.perform(post("/api/email/post/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(emailModel1))
        );

        var httpResponse1 = createdEmail1.andReturn().getResponse();
        String str1 = httpResponse1.getContentAsString();
        EmailDocument result1 = mapper.readValue(str1, EmailDocument.class);



        ResultActions listOfEmailsInDataBase = mvc.perform(get("/api/email/get/all/" + userID)
                .contentType(result1.getId()));


        var httpResponse2 = createdEmail1.andReturn().getResponse();
        String str2 = httpResponse2.getContentAsString();
        EmailDocument result2 = mapper.readValue(str2, EmailDocument.class);

        assertThat(result2.getUserId()).isEqualTo(result1.getUserId());

    }

    @Test
    public void deleteEmailById() throws Exception{
        assertThat(this.emailRepository.findAll().size()).isEqualTo(0);
        EmailModel emailModel1 = EmailModel.builder()
                .prompt("create email to nick do you want to go to geisel later")
                .build();

        ResultActions createdEmail1 = mvc.perform(post("/api/email/post/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(emailModel1))
        );

        assertThat(this.emailRepository.findAll().size()).isEqualTo(1);

        var httpResponse1 = createdEmail1.andReturn().getResponse();
        String str1 = httpResponse1.getContentAsString();
        EmailDocument result1 = mapper.readValue(str1, EmailDocument.class);

        mvc.perform(delete("/api/email/delete/" + result1.getId()));

        assertThat(this.emailRepository.findAll().size()).isEqualTo(0);

    }

    @Test
    public void deleteAllEmails() throws Exception {
        EmailModel emailModel1 = EmailModel.builder()
                .prompt("create email to nick do you want to go to geisel later")
                .build();

        mvc.perform(post("/api/email/post/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(emailModel1))
        );

        EmailModel emailModel2 = EmailModel.builder()
                .prompt("create email to Anish do you want to go to geisel later")
                .build();

        mvc.perform(post("/api/email/post/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(emailModel2))
        );

        assertThat(this.emailRepository.findAll().size()).isEqualTo(2);

        mvc.perform(delete("/api/email/delete/all/" + userID));

        assertThat(this.emailRepository.findAll().size()).isEqualTo(0);
    }




}