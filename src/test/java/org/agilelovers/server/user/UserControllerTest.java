package org.agilelovers.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.agilelovers.server.Server;
import org.agilelovers.server.email.config.EmailConfigRepository;
import org.agilelovers.common.models.ReducedUserModel;
import org.agilelovers.common.models.SecureUserModel;
import org.agilelovers.server.email.config.EmailConfigDocument;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
public class UserControllerTest {

    private final String API_KEY;

    private final static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailConfigRepository emailConfigRepository;

    public UserControllerTest() {
        this.API_KEY = Dotenv.load().get("API_SECRET");
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
     * 1. Creates a user and POST it to DB
     * Assert: the user is created and stored in the DB
     * @throws Exception
     */
    @Test
    public void createUserTest() throws Exception {
        String username = "testing@test.com";
        String password = "getplaintext";
        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/user/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))
        );

        List<UserDocument> found = userRepository.findAll();
        assertThat(found).extracting(UserDocument::getUsername).containsOnly(
                "testing@test.com");
    }

    /**
     * 1. Creates INVALID user
     * 2. Tries to POST to DB
     * Assert: Expects an invalid post request
     * @throws Exception
     */
    @Test
    public void createInvalidUserTest() throws Exception {
        String invalidUsername = "nosign.com";
        String password = "plaintext";

        SecureUserModel user = SecureUserModel.builder()
                .username(invalidUsername)
                .password(password)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/user/sign_up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().is(406)); // Expecting a 404 error
    }

    /**
     * 1. Creates a user with a valid username but invalid password
     * 2. Tries to POST to DB
     * Assert: Expects an invalid post request
     * @throws Exception
     */
    @Test
    public void createNoPasswordUserTest() throws Exception {
        String validUsername = "good@username.com";
        String invalidPassword = "";

        SecureUserModel user = SecureUserModel.builder()
                .username(validUsername)
                .password(invalidPassword)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/user/sign_up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().is(406));
    }

    /**
     * 1. Creates a user with a valid password but invalid username
     * 2. Tries to POST to DB
     * Assert: Expects an invalid post request
     * @throws Exception
     */
    @Test
    public void createNoUserTest() throws Exception {
        String invalidUsername = "";
        String validPassword = "greatpasswordnothackableatall";

        SecureUserModel user = SecureUserModel.builder()
                .username(invalidUsername)
                .password(validPassword)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/user/sign_up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().is(406));
    }

    /**
     * 1. Creates a user with a invalid password AND invalid username
     * 2. Tries to POST to DB
     * Assert: Expects an invalid post request
     * @throws Exception
     */
    @Test
    public void createNoUserNoPasswordTest() throws Exception {
        String invalidUsername = "";
        String invalidPassword = "";

        SecureUserModel user = SecureUserModel.builder()
                .username(invalidUsername)
                .password(invalidPassword)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/user/sign_up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().is(406));
    }

    /**
     * UNIT TEST
     * 1. Creates a user and POST it to DB
     * 2. GET the user from DB
     * @throws Exception
     */
    @Test
    public void getUserTest() throws Exception {
        // Create a user for testing
        String username = "testing@get.com";
        String password = "getplaintext";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/user/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))
        );

        ResultActions grabUser = mvc.perform(get("/api/user/sign_in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))
        );

        //get HttpResponse object
        var httpResponse = grabUser.andReturn().getResponse();
        //turn HttpResponse JSON content into string to pass into JsonUtil.fromJson
        String str = httpResponse.getContentAsString();

        ReducedUserModel result = mapper.readValue(str, ReducedUserModel.class);

        //Perform assertions to verify the retrieved user
        assertThat(result).extracting(ReducedUserModel::getUsername).isEqualTo(username);
    }

    /**
     * 1. Creates a user and POST it to DB
     * 2. GET the WRONG user from DB
     * Asserts: Expects a user not found error
     * @throws Exception
     */
    @Test
    public void getInvalidUserTest() throws Exception {
        String username = "testing@test.com";
        String invalidUser = "wrong@user.com";
        String password = "getplaintext";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/user/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)));

        //except user not found error
        ResultActions grabUser = mvc.perform(get("/api/user/sign_in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(invalidUser)));

        var httpResponse = grabUser.andReturn().getResponse();
        //turn HttpResponse JSON content into string to pass into JsonUtil.fromJson
        String str = httpResponse.getContentAsString();

        assertThat(str).isEqualTo("");
    }

    /**
     * 1. Creates a user and POST it to DB
     * 2. GET the empty username from DB
     * Asserts: Expects a user not found error
     * @throws Exception
     */
    @Test
    public void getEmptyUserTest() throws Exception {
        String username = "testing@test.com";
        String password = "getplaintext";
        String emptyUser = "";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/user/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))
        );

        //except user not found error
        ResultActions grabUser = mvc.perform(get("/api/user/sign_in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(emptyUser))
        );

        var httpResponse = grabUser.andReturn().getResponse();
        //turn HttpResponse JSON content into string to pass into JsonUtil.fromJson
        String str = httpResponse.getContentAsString();

        assertThat(str).isEqualTo("");
    }

    /**
     * 1. Creates a user and POST it to DB
     * 2. tries to POST the same username to DB
     * Asserts: user already exists error
     * @throws Exception
     */
    @Test
    public void createSameUserSamePasswordTest() throws Exception {
        String validUsername = "good@username.com";
        String validPassword = "greatpasswordnothackableatall";

        SecureUserModel user = SecureUserModel.builder()
                .username(validUsername)
                .password(validPassword)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/user/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))
        );

        //except "this user has already been created" error
        mvc.perform(post("/api/user/sign_up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().is(406));
    }

    /**
     * 1. Creates a user and POST it to DB
     * 2. tries to POST the same username to DB with a different password
     * Asserts: user already exists error
     * @throws Exception
     */
    @Test
    public void createSameUserDiffPasswordTest() throws Exception {
        String validUsername = "testing@test.com";
        String firstPassword = "greatpasswordnothackableatall";
        String secondPassword = "badpassword";

        SecureUserModel user1 = SecureUserModel.builder()
                .username(validUsername)
                .password(firstPassword)
                .apiPassword(API_KEY)
                .build();

        SecureUserModel user2 = SecureUserModel.builder()
                .username(validUsername)
                .password(secondPassword)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/user/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user1)));

        //except "this user has already been created" error
        mvc.perform(post("/api/user/sign_up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user2)))
                .andExpect(status().is(406));
    }
}

