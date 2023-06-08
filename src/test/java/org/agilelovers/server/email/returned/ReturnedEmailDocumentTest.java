package org.agilelovers.server.email.returned;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.agilelovers.common.models.EmailConfigModel;
import org.agilelovers.common.models.SecureUserModel;
import org.agilelovers.server.Server;
import org.agilelovers.server.email.config.EmailConfigRepository;
import org.agilelovers.server.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

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

    private String id;

    public ReturnedEmailDocumentTest() {
        Dotenv env = Dotenv.load();
        this.API_KEY = env.get("API_SECRET");
        this.EMAIL_USERNAME = env.get("EMAIL_USERNAME");
        this.EMAIL_PASSWORD = env.get("EMAIL_PASSWORD");
        this.SMTP_HOST = env.get("SMTP_HOST");
        this.TLS_PORT = env.get("TLS_PORT");
    }

    @Before
    public void setUp() {

    }

    @Test
    public void getAllReturnedEmailsTest() {

    }

    @Test
    public void getReturnedEmailTest() {

    }

    @Test
    public void deleteReturnedEmailTest() {

    }

    @Test
    public void deleteAllReturnedEmailsTest() {

    }

    @Test
    public void sendEmailTest() {

    }

}
