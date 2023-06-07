package org.agilelovers.server;

import io.github.cdimascio.dotenv.Dotenv;
import org.agilelovers.server.user.models.ReducedUser;
import org.agilelovers.server.user.models.UserDocument;
import org.agilelovers.server.user.models.SecureUser;
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

    @After
    public void resetDb() {
        userRepository.deleteAll();
    }

    @Test
    public void retrieveUserInformation() throws Exception {
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


    @Test
    public void createUserWithValidInputs() throws Exception {
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

    @Test
    public void CreateUserWithInvalidInput_NoAtSignUsername() throws Exception {
        String invalidUsername = "nosign.com";
        String password = "plaintext";

        UserDocument user = UserDocument.builder()
                .username(invalidUsername)
                .password(password)
                .build();

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(status().isNotFound()); // Expecting a 404 error
    }

    @Test
    public void CreateUserWithInvalidInput_noPassword() throws Exception {
        String validUsername = "good@username.com";
        String invalidPassword = "";

        UserDocument user = UserDocument.builder()
                .username(validUsername)
                .password(invalidPassword)
                .build();

        mvc.perform(post("/api/users")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(JsonUtil.toJson(user)))
                 .andExpect(status().isNotFound() // Expecting a 404 error
        );
    }

//    @Test
//    public void createTwoUsers_SameUsername() throws Exception {
//        String validUsername = "good@username.com";
//        String validPassword = "greatpasswordnothackableatall";
//
//        UserDocument user = UserDocument.builder()
//                .username(validUsername)
//                .password(validPassword)
//                .build();
//
//        mvc.perform(post("/api/users")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(JsonUtil.toJson(user))
//        );
//
//        mvc.perform(post("/api/users")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(JsonUtil.toJson(user)))
//                 .andExpect(status().isNotFound() // Expecting a 404 error
//        );
//
//    }



    @Test
    public void createUserAndChangeEmailWithValidInputs() throws Exception {

        String username = "unsure_testing@test.com";
        String email = "now_sure_testing@test.com";
        String password = "plaintext";

        UserDocument user = UserDocument.builder()
                .username(username)
                .password(password)
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
