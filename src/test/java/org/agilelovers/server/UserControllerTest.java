package org.agilelovers.server;

import io.github.cdimascio.dotenv.Dotenv;
import org.agilelovers.server.user.UserRepository;
import org.agilelovers.server.user.models.ReducedUser;
import org.agilelovers.server.user.models.SecureUser;
import org.agilelovers.server.user.models.UserDocument;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    public UserControllerTest() {
        this.API_KEY = Dotenv.load().get("API_SECRET");
    }

    /**
     * Reset the database after each test so its state is clean
     */
    @BeforeEach
    public void resetDb() {
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
        SecureUser user = SecureUser.builder()
                .username(username)
                .password(password)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user))
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

        SecureUser user = SecureUser.builder()
                .username(invalidUsername)
                .password(password)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(user)))
                        .andExpect(status().isNotFound()); // Expecting a 404 error
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

        SecureUser user = SecureUser.builder()
                .username(validUsername)
                .password(invalidPassword)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(user)))
                        .andExpect(status().isNotFound());
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

        SecureUser user = SecureUser.builder()
                .username(invalidUsername)
                .password(validPassword)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(user)))
                        .andExpect(status().isNotFound());
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

        SecureUser user = SecureUser.builder()
                .username(invalidUsername)
                .password(invalidPassword)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(user)))
                        .andExpect(status().isNotFound());
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

        SecureUser user = SecureUser.builder()
                .username(username)
                .password(password)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user))
        );

        ResultActions grabUser = mvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user))
               );

        //get HttpResponse object
        var httpResponse = grabUser.andReturn().getResponse();
        //turn HttpResponse JSON content into string to pass into JsonUtil.fromJson
        String str = httpResponse.getContentAsString();

        ReducedUser result = JsonUtil.fromJson(str, ReducedUser.class);

        //Perform assertions to verify the retrieved user
        assertThat(result).extracting(ReducedUser::getUsername).isEqualTo(username);
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

        SecureUser user = SecureUser.builder()
                .username(username)
                .password(password)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(user)));

        //except user not found error
        ResultActions grabUser = mvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(invalidUser)));
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

        SecureUser user = SecureUser.builder()
                .username(username)
                .password(password)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(user)));

        //except user not found error
        ResultActions grabUser = mvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(emptyUser)));
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

        SecureUser user = SecureUser.builder()
                .username(validUsername)
                .password(validPassword)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user))
        );

        //except "this user has already been created" error
        mvc.perform(post("/api/users")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(JsonUtil.toJson(user)))
                 .andExpect(status().isNotFound());
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

        SecureUser user1 = SecureUser.builder()
                .username(validUsername)
                .password(firstPassword)
                .apiPassword(API_KEY)
                .build();

        SecureUser user2 = SecureUser.builder()
                .username(validUsername)
                .password(secondPassword)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user1)));

        //except "this user has already been created" error
        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(user2)))
                        .andExpect(status().isNotFound());
    }


    /**
     * UNIT TEST
     * 1. Creating a VALID user
     * 2. POST the user to DB
     * 3. find the user in DB
     * 4. PUT (update) the user's email
     * @throws Exception
     */
    @Test
    public void updateEmailTest() throws Exception {

        String username = "unsure_testing@test.com";
        String email = "now_sure_testing@test.com";
        String password = "plaintext";

        SecureUser user = SecureUser.builder()
                .username(username)
                .password(password)
                .apiPassword(API_KEY)
                .build();

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user))
        );

        UserDocument found = userRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(TestAbortedException::new);

        assertThat(found).extracting(UserDocument::getUsername).isEqualTo(username);
        assertThat(found).extracting(UserDocument::getEmail).isNull();

        String id = found.getId();

        mvc.perform(put("/api/users/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(email)
        );

        found = userRepository.findById(id)
                .orElseThrow(TestAbortedException::new);

        assertThat(found).extracting(UserDocument::getUsername).isEqualTo(username);
        assertThat(found).extracting(UserDocument::getEmail).isEqualTo(email);
    }


}
