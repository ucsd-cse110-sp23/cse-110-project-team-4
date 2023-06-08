package org.agilelovers.server.email.returned;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.agilelovers.common.documents.EmailDocument;
import org.agilelovers.common.documents.ReturnedEmailDocument;
import org.agilelovers.common.documents.UserDocument;
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

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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

    private SecureUserModel user;
    private EmailConfigModel configDocument;
    private EmailModel emailModel;
    private String id;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private EmailRepository emailRepositoryMock;

    @Mock
    private ReturnedEmailRepository returnedEmailRepository;

    @Mock
    private ReturnedEmailController returnedEmailController;

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
        user = SecureUserModel.builder()
                .username("cse110-test@anishgovind.com")
                .password("password")
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/user/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))
        );

        id = userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword())
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

        mvc.perform(post("/api/email/config/save/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
        );

        UserDocument userDocument = userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .orElseThrow(TestAbortedException::new);

        emailModel = EmailModel.builder()
                .prompt("Test Prompt")
                .build();

        EmailDocument emailDocument = EmailDocument.builder()
                .id(id)
                .createdDate(new Date("06/08/2023"))
                .userId(userDocument.getId())
                .body("I am so sad")
                .entirePrompt(emailModel.getPrompt())
                .build();

        mvc.perform(post("/api/email/post/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(emailModel))
        );

        ReturnedEmailModel returnedEmailModel = ReturnedEmailModel.builder()
                .sentId(emailDocument.getId())
                .recipient("Sadness")
                .command("SEND_QUESTION")
                .entirePrompt("why am i so sad?")
                .build();

        mvc.perform(post("/api/email/returned/send/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(returnedEmailModel))
        );

        //MockitoAnnotations.openMocks(this);
        //returnedEmailController = new ReturnedEmailController(userRepositoryMock, emailRepositoryMock, returnedEmailRepository);
    }

    @After
    public void reset(){
        emailConfigRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getAllReturnedEmailsTest() throws Exception {
        ResultActions temp = mvc.perform(get("/api/email/returned/get/all/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                );

        var httpResponse = temp.andReturn().getResponse();
        //turn HttpResponse JSON content into string to pass into JsonUtil.fromJson
        String str = httpResponse.getContentAsString();

        List<ReturnedEmailDocument> result = mapper.readValue(str, new TypeReference<>() {
        });

        assertThat(result).isNotNull();
        assertThat(result).size().isEqualTo(1);
    }

    @Test
    public void getReturnedEmailTest() {

    }

    @Test
    public void deleteReturnedEmailTest() throws Exception {
        mvc.perform(post("/api/email/returned/delete/" + id)
                .contentType(MediaType.APPLICATION_JSON)
        );

        assertThat(returnedEmailRepository.findAll()).isEmpty();
    }

    @Test
    public void deleteAllReturnedEmailsTest() throws Exception {
        emailModel = EmailModel.builder()
                .prompt("Test Prompt 123")
                .build();

        mvc.perform(post("/api/email/post/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(emailModel))
        );

        mvc.perform(post("/api/email/returned/delete/all/" + id)
                .contentType(MediaType.APPLICATION_JSON)
        );

        assertThat(returnedEmailRepository.findAll()).isEmpty();
    }

    @Test
    public void sendEmailTest() {

    }
}
