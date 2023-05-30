package org.agilelovers.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.agilelovers.server.question.QuestionRepository;
import org.agilelovers.server.user.UserDocument;
import org.agilelovers.server.user.UserRepository;
import org.apache.catalina.User;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentest4j.TestAbortedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

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

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @After
    public void resetDb() {
        userRepository.deleteAll();
    }

//    @Test
//    public void retrieveUserInformation() throws Exception {
//        // Create a user for testing
//        String username = "testing@get.com";
//        String password = "getplaintext";
//
//        UserDocument user = UserDocument.builder()
//                .username(username)
//                .password(password)
//                .build();
//
//        mvc.perform(post("/api/users")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(JsonUtil.toJson(user))
//        );
//
//        MvcResult mvcResult = mvc.perform(get("/api/users")
//                        .contentType(MediaType.APPLICATION_JSON))
//                        .andReturn();
//
//        String responseContent = mvcResult.getResponse().getContentAsString();
//        UserDocument result = JsonUtil.fromJson(responseContent, UserDocument.class);
//
//
//        // Perform assertions to verify the retrieved user
//        assertThat(result).extracting(UserDocument::getUsername).isEqualTo(username);
//        assertThat(result).extracting(UserDocument::getPassword).isEqualTo(password);
//    }


    @Test
    public void createUserWithValidInputs() throws Exception {
        UserDocument user = UserDocument.builder()
                .username("testing@test.com")
                .password("plaintext")
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
