package org.agilelovers.server.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.agilelovers.common.models.EmailConfigModel;
import org.agilelovers.server.Server;
import org.agilelovers.server.email.config.EmailConfigRepository;
import org.agilelovers.common.models.SecureUserModel;
import org.agilelovers.common.documents.EmailConfigDocument;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
public class EmailConfigControllerTest {

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

    private String id;

    public EmailConfigControllerTest() {
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
                .firstName("cse110")
                .lastName("testing")
                .email(this.EMAIL_USERNAME)
                .emailPassword(this.EMAIL_PASSWORD)
                .displayName("CSE110")
                .smtpHost(this.SMTP_HOST)
                .tlsPort(this.TLS_PORT)
                .build();
    }

    /**
     * Reset the database after each test so its state is clean
     */
    @After
    public void resetDb() {
        emailConfigRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * UNIT TEST
     * 1. post user to DB
     * 2. post email config to DB
     * Assert: email config is saved to DB
     * @throws Exception
     */
    @Test
    public void saveEmailConfigTest() throws Exception {
        mvc.perform(post("/api/email/config/save/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
        );

        List<EmailConfigDocument> found = emailConfigRepository.findAll();
        assertThat(found.size()).isEqualTo(1);
        assertThat(found.get(0).getFirstName()).isEqualTo(configDocument.getFirstName());
    }

    /**
     * UNIT TEST
     * 1. post email config to DB
     * 2. get email config from DB
     * Assert: email config is retrieved from DB
     * @throws Exception
     */
    @Test
    public void getEmailConfigTest() throws Exception {
        mvc.perform(post("/api/email/config/save/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
        );

        ResultActions grabUser =  mvc.perform(get("/api/email/config/get/" + id)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                        );

        //get HttpResponse object
        var httpResponse = grabUser.andReturn().getResponse();
        //turn HttpResponse JSON content into string to pass into JsonUtil.fromJson
        String str = httpResponse.getContentAsString();

        EmailConfigDocument result = mapper.readValue(str, EmailConfigDocument.class);

        //Perform assertions to verify the retrieved user
        assertThat(result).extracting(EmailConfigDocument::getEmail).isEqualTo(configDocument.getEmail());
    }

    @Test
    public void getBadUserIdTest() throws Exception {
        mvc.perform(post("/api/email/config/save/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
        );

        mvc.perform(get("/api/email/config/badId")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void saveBadEmailTest() throws Exception {
        configDocument.setEmail("badEmail");

        mvc.perform(post("/api/email/config/save/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
                ).andExpect(status().is(406));
    }

    @Test
    public void saveBadTLSPortTest() throws Exception {
        configDocument.setTlsPort("badPort");

        mvc.perform(post("/api/email/config/save/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
                ).andExpect(status().is(406));
    }

    @Test
    public void saveBadSMTPHostTest() throws Exception {
        configDocument.setSmtpHost("badHost");

        mvc.perform(post("/api/email/config/save/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
                ).andExpect(status().is(406));
    }

    @Test
    public void saveBadEmailPasswordTest() throws Exception {
        configDocument.setEmailPassword("badPassword");

        mvc.perform(post("/api/email/config/save/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
                ).andExpect(status().is(406));
    }

    @Test
    public void saveNullDisplayNameTest() throws Exception {
        configDocument.setDisplayName(null);

        mvc.perform(post("/api/email/config/save/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
                ).andExpect(status().is(406));
    }

    @Test
    public void saveNullLastNameTest() throws Exception {
        configDocument.setLastName(null);

        mvc.perform(post("/api/email/config/save/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
                ).andExpect(status().is(406));
    }

    @Test
    public void saveNullFirstNameTest() throws Exception {
        configDocument.setFirstName(null);

        mvc.perform(post("/api/email/config/save/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
                ).andExpect(status().is(406));
    }

}
