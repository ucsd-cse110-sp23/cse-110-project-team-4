package org.agilelovers.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.agilelovers.server.Server;
import org.agilelovers.server.user.models.SecureUser;
import org.agilelovers.server.user.models.UserEmailConfigDocument;
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
public class UserEmailConfigControllerTest {

    private final String API_KEY;

    private final static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserEmailRepository userEmailRepository;

    private SecureUser user;
    private UserEmailConfigDocument configDocument;

    private String id;

    public UserEmailConfigControllerTest() {
        this.API_KEY = Dotenv.load().get("API_SECRET");
    }

    @Before
    public void setUp() throws Exception {
        user = SecureUser.builder()
                .username("louie@cai.com")
                .password("password")
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))
        );

        id = userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .orElseThrow(TestAbortedException::new).getId();

        configDocument = UserEmailConfigDocument.builder()
                .userID(id)
                .firstName("Louie")
                .lastName("Cai")
                .email("louie@cai.com")
                .emailPassword("password")
                .displayName("Louie Cai")
                .smtpHost("smtp.gmail.com")
                .tlsPort("587")
                .build();
    }

    /**
     * Reset the database after each test so its state is clean
     */
    @After
    public void resetDb() {
        userEmailRepository.deleteAll();
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
        mvc.perform(post("/api/emailconfig/{uid}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
        );

        List<UserEmailConfigDocument> found = userEmailRepository.findAll();
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
        mvc.perform(post("/api/emailconfig/{uid}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
        );

        ResultActions grabUser =  mvc.perform(get("/api/emailconfig/{uid}", id)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                        );

        //get HttpResponse object
        var httpResponse = grabUser.andReturn().getResponse();
        //turn HttpResponse JSON content into string to pass into JsonUtil.fromJson
        String str = httpResponse.getContentAsString();

        UserEmailConfigDocument result = mapper.readValue(str, UserEmailConfigDocument.class);

        //Perform assertions to verify the retrieved user
        assertThat(result).extracting(UserEmailConfigDocument::getEmail).isEqualTo(configDocument.getEmail());
    }

    @Test
    public void getBadUserIdTest() throws Exception {
        mvc.perform(post("/api/emailconfig/{uid}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
        );

        mvc.perform(get("/api/emailconfig/{uid}", "badId")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void saveBadEmailTest() throws Exception {
        configDocument.setEmail("badEmail");

        mvc.perform(post("/api/emailconfig/{uid}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
                ).andExpect(status().is(406));
    }

    @Test
    public void saveBadTLSPortTest() throws Exception {
        configDocument.setTlsPort("badPort");

        mvc.perform(post("/api/emailconfig/{uid}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
                ).andExpect(status().is(406));
    }

    @Test
    public void saveBadSMTPHostTest() throws Exception {
        configDocument.setSmtpHost("badHost");

        mvc.perform(post("/api/emailconfig/{uid}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(configDocument))
                ).andExpect(status().is(406));
    }
}
