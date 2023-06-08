package org.agilelovers.server.question;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.agilelovers.common.documents.QuestionDocument;
import org.agilelovers.common.models.QuestionModel;
import org.agilelovers.common.models.ReducedUserModel;
import org.agilelovers.common.models.SecureUserModel;
import org.agilelovers.server.Server;
import org.agilelovers.server.assistant.AssistantController;
import org.agilelovers.server.common.OpenAIClient;
import org.agilelovers.server.user.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


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
    private final String API_KEY;
    private final String EMAIL_USERNAME;
    private final String EMAIL_PASSWORD;
    private final static ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionRepository questionRepository;
    private SecureUserModel user;

    public QuestionControllerTest(){
        Dotenv env = Dotenv.load();
        this.API_KEY = env.get("API_SECRET");
        this.EMAIL_USERNAME = env.get("EMAIL_USERNAME");
        this.EMAIL_PASSWORD = env.get("EMAIL_PASSWORD");
    }

    /**
     * Create a user before each test
     */
    @Before
    public void setup () throws Exception{
        user = SecureUserModel.builder()
                .username(this.EMAIL_USERNAME)
                .password(this.EMAIL_PASSWORD)
                .apiPassword(this.API_KEY)
                .build();

        mvc.perform(post("/api/user/sign_up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user))
        );
    }

    /**
     * Reset the database after each test so its state is clean
     */
    @After
    public void resetDb() {
        questionRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * UNIT TEST
     * 1. Creates a question and adds it to the database
     * Assert that the question is in the database
     * @throws Exception
     */
    @Test
    public void test_CreateASingleQuestion() throws Exception {
        ResultActions userFromGet = mvc.perform(get("/api/user/sign_in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)));

        var httpResponse = userFromGet.andReturn().getResponse();
        //turn HttpResponse JSON content into string to pass into JsonUtil.fromJson
        String str = httpResponse.getContentAsString();
        ReducedUserModel result = mapper.readValue(str, ReducedUserModel.class);
        String userId = result.getId();

        String prompt_1 = "question How far away is the sun from the Earth";
        QuestionModel prompt_model1 = QuestionModel.builder()
                .prompt(prompt_1)
                .build();

        mvc.perform(post("/api/question/post/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(prompt_model1)));

        ResultActions grabAllQuestionsInDatabase = mvc.perform(get("/api/question/get/all/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
        );

        var mvcResult = grabAllQuestionsInDatabase.andReturn().getResponse();
        String responseContent = mvcResult.getContentAsString();
        List<QuestionDocument> listOfQuestions = mapper.readValue(responseContent,
                new TypeReference<List<QuestionDocument>>() {});

        assertThat(listOfQuestions.size() == 1);
        assertThat(listOfQuestions.get(0)).extracting(QuestionDocument::getEntirePrompt)
                .isEqualTo("question How far away is the sun from the Earth");
    }

    /**
     * UNIT TEST
     * 1. Creates 3 questions and adds it to the database
     * Assert that all three questions are in the database
     * Assert that all questions in the database match the original questions
     * @throws Exception
     */
    @Test
    public void test_GetAllQuestions() throws Exception {
        ResultActions userFromGet = mvc.perform(get("/api/user/sign_in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)));

        var httpResponse = userFromGet.andReturn().getResponse();
        //turn HttpResponse JSON content into string to pass into JsonUtil.fromJson
        String str = httpResponse.getContentAsString();
        ReducedUserModel result = mapper.readValue(str, ReducedUserModel.class);
        String userId = result.getId();;

        String prompt_1 = "question How far away is the sun from the Earth";
        QuestionModel prompt_model1 = QuestionModel.builder()
                        .prompt(prompt_1)
                        .build();

        mvc.perform(post("/api/question/post/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(prompt_model1)));

        String prompt_2 = "question How many apples should I eat a day";
        QuestionModel prompt_model2 = QuestionModel.builder()
                .prompt(prompt_2)
                .build();

        mvc.perform(post("/api/question/post/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(prompt_model2)));

        String prompt_3 = "question How many grams does the average potato weigh";
        QuestionModel prompt_model3 = QuestionModel.builder()
                .prompt(prompt_3)
                .build();

        mvc.perform(post("/api/question/post/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(prompt_model3)));

        ResultActions grabAllQuestionsInDatabase = mvc.perform(get("/api/question/get/all/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
        );
        var mvcResult = grabAllQuestionsInDatabase.andReturn().getResponse();
        String responseContent = mvcResult.getContentAsString();
        List<QuestionDocument> listOfQuestions = mapper.readValue(responseContent,
                                                new TypeReference<List<QuestionDocument>>() {});

        assertThat(listOfQuestions.get(0)).extracting(QuestionDocument::getEntirePrompt)
                .isEqualTo("question How far away is the sun from the Earth");
        assertThat(listOfQuestions.get(1)).extracting(QuestionDocument::getEntirePrompt)
                .isEqualTo("question How many apples should I eat a day");
        assertThat(listOfQuestions.get(2)).extracting(QuestionDocument::getEntirePrompt)
                .isEqualTo("question How many grams does the average potato weigh");
    }

    /**
     * UNIT TEST
     * 1. Creates 1 question and adds it to the database
     * 2. Deletes all questions from the database
     * Assert that the database is empty
     * @throws Exception
     */
    @Test
    public void test_deleteAQuestion() throws Exception {
        ResultActions userFromGet = mvc.perform(get("/api/user/sign_in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)));

        var httpResponse = userFromGet.andReturn().getResponse();
        //turn HttpResponse JSON content into string to pass into JsonUtil.fromJson
        String str = httpResponse.getContentAsString();
        ReducedUserModel result = mapper.readValue(str, ReducedUserModel.class);
        String userId = result.getId();

        String prompt_1 = "question How far away is the sun from the Earth";
        QuestionModel prompt_model1 = QuestionModel.builder()
                .prompt(prompt_1)
                .build();

        mvc.perform(post("/api/question/post/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(prompt_model1)));

        ResultActions grabAllQuestionsInDatabase = mvc.perform(get("/api/question/get/all/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
        );

        var mvcResult = grabAllQuestionsInDatabase.andReturn().getResponse();
        String responseContent = mvcResult.getContentAsString();
        List<QuestionDocument> listOfQuestions = mapper.readValue(responseContent,
                new TypeReference<List<QuestionDocument>>() {});

        assertThat(listOfQuestions.size() == 1);
        assertThat(listOfQuestions.get(0)).extracting(QuestionDocument::getEntirePrompt)
                .isEqualTo("question How far away is the sun from the Earth");

        mvc.perform(delete("/api/question/delete/" + userId)
                .content(listOfQuestions.get(0).getId())
        );

        assertThat(listOfQuestions.size() == 0);
    }

    /**
     * UNIT TEST
     * 1. Creates 3 questions and adds it to the database
     * 2. Deletes all questions from the database
     * Assert that the database is empty
     * @throws Exception
     */
    @Test
    public void test_DeleteAllQuestionsFromSpecificUser() throws Exception {
        ResultActions userFromGet = mvc.perform(get("/api/user/sign_in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)));

        var httpResponse = userFromGet.andReturn().getResponse();
        //turn HttpResponse JSON content into string to pass into JsonUtil.fromJson
        String str = httpResponse.getContentAsString();
        ReducedUserModel result = mapper.readValue(str, ReducedUserModel.class);
        String userId = result.getId();;

        String prompt_1 = "question How far away is the sun from the Earth";
        QuestionModel prompt_model1 = QuestionModel.builder()
                .prompt(prompt_1)
                .build();

        mvc.perform(post("/api/question/post/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(prompt_model1)));

        String prompt_2 = "question How many apples should I eat a day";
        QuestionModel prompt_model2 = QuestionModel.builder()
                .prompt(prompt_2)
                .build();

        mvc.perform(post("/api/question/post/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(prompt_model2)));

        String prompt_3 = "question How many grams does the average potato weigh";
        QuestionModel prompt_model3 = QuestionModel.builder()
                .prompt(prompt_3)
                .build();

        mvc.perform(post("/api/question/post/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(prompt_model3)));

        ResultActions grabAllQuestionsInDatabase = mvc.perform(get("/api/question/get/all/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
        );
        var mvcResult = grabAllQuestionsInDatabase.andReturn().getResponse();
        String responseContent = mvcResult.getContentAsString();
        List<QuestionDocument> listOfQuestions = mapper.readValue(responseContent,
                new TypeReference<List<QuestionDocument>>() {});

        assertThat(listOfQuestions.size() == 3);

        mvc.perform(delete("/api/question/delete/all/" + userId));
        assertThat(listOfQuestions.size() == 0);
    }

}
