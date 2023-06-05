package org.agilelovers.server;

import org.agilelovers.server.mock.mockOpenAI;
import org.agilelovers.server.question.QuestionDocument;
import org.agilelovers.server.question.QuestionRepository;
import org.agilelovers.server.user.UserDocument;
import org.agilelovers.server.user.UserRepository;
import org.agilelovers.ui.object.Question;
import org.agilelovers.server.mock.mockRecording;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
public class QuestionControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private mockOpenAI fakeAi = new mockOpenAI();

    private mockRecording mockRecord = new mockRecording();

    @After
    public void resetDb() {
        questionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void AskingAQuestion() throws Exception {

        String username = "testing@test.com";
        String password = "plaintext";

        UserDocument user = UserDocument.builder()
                .username(username)
                .password(password)
                .build();

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user))
        );

        UserDocument foundUser = userRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(TestAbortedException::new);

        assertThat(foundUser).extracting(UserDocument::getUsername).isEqualTo(username);
        assertThat(foundUser).extracting(UserDocument::getEmail).isNull();

        String id = foundUser.getId();

        mockRecord.startRecording();
        Question mockedQuestionObject = mockRecord.endRecording();
        String question = mockedQuestionObject.getResponse();

        mvc.perform(post("/api/questions/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(question))
        );

        Optional<QuestionDocument> foundQuestion = questionRepository.findById(id);
        assertThat(foundQuestion.map(QuestionDocument::getQuestion).toString())
                .isEqualTo("Optional[\"Question, How tall is Nicholas Lam\"]");
    }

}
