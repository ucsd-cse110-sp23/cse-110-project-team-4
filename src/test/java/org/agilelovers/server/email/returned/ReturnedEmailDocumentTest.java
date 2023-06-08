package org.agilelovers.server.email.returned;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.agilelovers.common.documents.*;
import org.agilelovers.common.models.EmailConfigModel;
import org.agilelovers.common.models.EmailModel;
import org.agilelovers.common.models.ReturnedEmailModel;
import org.agilelovers.common.models.SecureUserModel;
import org.agilelovers.server.Server;
import org.agilelovers.server.email.base.EmailRepository;
import org.agilelovers.server.email.config.EmailConfigRepository;
import org.agilelovers.server.user.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opentest4j.TestAbortedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.validation.constraints.Email;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.agilelovers.common.CommandIdentifier.CREATE_EMAIL_COMMAND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Server.class
)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class ReturnedEmailDocumentTest {

    private EmailDocument emailDocument;
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
    private EmailConfigRepository emailConfigRepository;
    @Autowired
    private ReturnedEmailRepository returnedEmailRepository;
    @Autowired
    private EmailRepository emailRepository;


    private SecureUserModel user;
    private EmailConfigModel configDocument;
    private EmailModel emailModel;
    private String userID;

//    @Mock
//    private UserRepository userRepositoryMock;
//    @Mock
//    private EmailRepository emailRepositoryMock;
//    @Mock
//    private ReturnedEmailRepository returnedEmailRepository;
//    @Mock
//    private ReturnedEmailController returnedEmailController;

    public ReturnedEmailDocumentTest() {
        Dotenv env = Dotenv.load();
        this.API_KEY = env.get("API_SECRET");
        this.EMAIL_USERNAME = env.get("EMAIL_USERNAME");
        this.EMAIL_PASSWORD = env.get("EMAIL_PASSWORD");
        this.SMTP_HOST = env.get("SMTP_HOST");
        this.TLS_PORT = env.get("TLS_PORT");

    }

    @Before
    public void setUp() throws Exception {

        // Step 1: Post a user to database
        user = SecureUserModel.builder()
                .username(this.EMAIL_USERNAME)
                .password(this.EMAIL_PASSWORD)
                .apiPassword(this.API_KEY)
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


        // Step 3: Draft an email under a specific user ID
        UserDocument userDocument = userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .orElseThrow(TestAbortedException::new);

        emailModel = EmailModel.builder()
                .prompt("create email to nick do you want to go to geisel later")
                .build();

        ResultActions createdEmail = mvc.perform(post("/api/email/post/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(emailModel))
        );

        // Step 4: sent an email under a specific user ID

        var httpResponse_createdEmail = createdEmail.andReturn().getResponse();
        String str_createdEmail = httpResponse_createdEmail.getContentAsString();
        EmailDocument result_createdEmail = mapper.readValue(str_createdEmail, EmailDocument.class);

        ReturnedEmailModel toSend = ReturnedEmailModel.builder()
                .sentId(result_createdEmail.getId())
                .recipient("nnlam@ucsd.edu")
                .command(CREATE_EMAIL_COMMAND)
                .entirePrompt(result_createdEmail.getEntirePrompt())
                .build();

        ResultActions returnedEmail = mvc.perform(post("/api/email/returned/send/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(toSend))
        );

//        var httpResponse_returnedEmail  = createdEmail.andReturn().getResponse();
//        String str_returnedEmail = httpResponse_createdEmail.getContentAsString();
//        ReturnedEmailDocument result_returnedEmail= mapper.readValue(str_returnedEmail, ReturnedEmailDocument.class);

    }

    @After
    public void reset(){
        emailConfigRepository.deleteAll();
        userRepository.deleteAll();
        returnedEmailRepository.deleteAll();
        emailRepository.deleteAll();
    }

    @Test
    public void getAllReturnedEmailsTest() throws Exception {
        ResultActions listOfAllSentEmails = mvc.perform(get("/api/email/returned/get/all/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                );

        var httpResponse = listOfAllSentEmails.andReturn().getResponse();
        //turn HttpResponse JSON content into string to pass into JsonUtil.fromJson
        String str = httpResponse.getContentAsString();
        List<ReturnedEmailDocument> result = mapper.readValue(str, new TypeReference<>() {});

        assertThat(result).size().isEqualTo(1);

        ReturnedEmailDocument returnedEmail = result.get(0);

        assertThat(returnedEmail.getEntirePrompt()).isEqualTo("create email to nick do you want to go to geisel later");
    }

    @Test
    public void getReturnedEmailTest() {

    }

    @Test
    public void deleteOneReturnedEmailTest() throws Exception {
        List<ReturnedEmailDocument> emailList = returnedEmailRepository.findAllByUserId(userID).get();
        assertThat(emailList.size()).isEqualTo(1);

        ReturnedEmailDocument email = emailList.get(0);
        String emailID = email.getId();

        mvc.perform(delete("/api/email/returned/delete/" + emailID)
                .contentType(MediaType.APPLICATION_JSON)
        );

        assertThat(returnedEmailRepository.findAll()).isEmpty();
    }

    @Test
    public void deleteAllReturnedEmailsTest() throws Exception {
        EmailModel emailModel1 = EmailModel.builder()
                .prompt("create email to Anish do you want to go to geisel later")
                .build();

        ResultActions createdEmail = mvc.perform(post("/api/email/post/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(emailModel1))
        );
        var httpResponse_createdEmail = createdEmail.andReturn().getResponse();
        String str_createdEmail = httpResponse_createdEmail.getContentAsString();
        EmailDocument result_createdEmail = mapper.readValue(str_createdEmail, EmailDocument.class);

        ReturnedEmailModel toSend = ReturnedEmailModel.builder()
                .sentId(result_createdEmail.getId())
                .recipient("anishGovind@ucsd.edu")
                .command(CREATE_EMAIL_COMMAND)
                .entirePrompt(result_createdEmail.getEntirePrompt())
                .build();

        mvc.perform(post("/api/email/returned/send/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(toSend))
        );


        EmailModel emailModel2 = EmailModel.builder()
                .prompt("create email to Louie do you want to go to geisel later")
                .build();
        ResultActions createdEmail2 = mvc.perform(post("/api/email/post/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(emailModel2))
        );

        var httpResponse_createdEmail2 = createdEmail2.andReturn().getResponse();
        String str_createdEmail2 = httpResponse_createdEmail2.getContentAsString();
        EmailDocument result_createdEmail2 = mapper.readValue(str_createdEmail2, EmailDocument.class);

        ReturnedEmailModel toSend2 = ReturnedEmailModel.builder()
                .sentId(result_createdEmail2.getId())
                .recipient("LouieCai@ucsd.edu")
                .command(CREATE_EMAIL_COMMAND)
                .entirePrompt(result_createdEmail2.getEntirePrompt())
                .build();

        mvc.perform(post("/api/email/returned/send/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(toSend2))
        );

        ResultActions listOfAllSentEmails = mvc.perform(get("/api/email/returned/get/all/" + userID)
                .contentType(MediaType.APPLICATION_JSON)
        );
        var httpResponse = listOfAllSentEmails.andReturn().getResponse();
        String str = httpResponse.getContentAsString();
        List<ReturnedEmailDocument> result = mapper.readValue(str, new TypeReference<>() {});

        assertThat(result).size().isEqualTo(3);

        ReturnedEmailDocument returnedEmail1 = result.get(0);
        ReturnedEmailDocument returnedEmail2 = result.get(1);
        ReturnedEmailDocument returnedEmail3 = result.get(2);

        assertThat(returnedEmail1.getEntirePrompt()).isEqualTo("create email to nick do you want to go to geisel later");
        assertThat(returnedEmail2.getEntirePrompt()).isEqualTo("create email to Anish do you want to go to geisel later");
        assertThat(returnedEmail3.getEntirePrompt()).isEqualTo("create email to Louie do you want to go to geisel later");

        mvc.perform(delete("/api/email/returned/delete/all/" + userID));

        assertThat(returnedEmailRepository.findAll().isEmpty());
    }

    @Test
    public void sendEmailTest() {

    }
}
