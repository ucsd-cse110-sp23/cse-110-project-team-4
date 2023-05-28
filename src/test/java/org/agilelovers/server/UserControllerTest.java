package org.agilelovers.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.agilelovers.server.question.QuestionRepository;
import org.agilelovers.server.user.UserDocument;
import org.agilelovers.server.user.UserRepository;
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

    @Test
    public void createUser() throws Exception {
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
    public void createUserAndChangeEmail() throws Exception {

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
