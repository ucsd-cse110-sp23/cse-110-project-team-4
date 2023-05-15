package org.agilelovers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.agilelovers.backend.MockDatabase;
import org.agilelovers.backend.SayItAssistant;
import org.agilelovers.ui.MockController;
import org.agilelovers.ui.object.Question;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasItems;

class ControllerTest extends ApplicationTest {

    @Mock
    private SayItAssistant assistant;
    private MockController controller;
    private String title1 = "title1";
    private String title2 = "title2";
    private String question1 = "question1";
    private String question2 = "question2";
    private String answer1 = "answer1";
    private String answer2 = "answer2";

    private MockDatabase mockDatabase;
    private List<Question> questions;
    private Question testQuestion1;
    private Question testQuestion2;

    @Override
    public void start(Stage stage) throws IOException {
        mockDatabase = new MockDatabase();
        var fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/MockMain.fxml"));
        Parent root = fxmlLoader.load();
        //Controller.instance = fxmlLoader.getController();
        //controller = (MockController) Controller.instance;
        controller = fxmlLoader.getController();
        Scene scene = new Scene(root); //??
        stage.setScene(scene);
        stage.show();
        System.out.println("start() called and finished");
    }
    @BeforeEach
    void setup() throws IOException {
        assistant = mock(SayItAssistant.class);
        questions = mockDatabase.obtainQuestions();
        System.out.println("setup() called and finished");
    }

    private void addQuestions() {
        testQuestion1 = new Question(title1, question1, answer1);
        testQuestion2 = new Question(title2, question2, answer2);
        controller.addQuestion(testQuestion1);
        questions.add(testQuestion1);
        controller.addQuestion(testQuestion2);
        questions.add(testQuestion2);
        controller.initHistoryList();
    }

    @Test
    void testInitHistoryList() {
        controller.initHistoryList();
        Assertions.assertThat(controller.getHistoryList()).isNotNull();
    }

    @Test
    void testRefreshLabels() {
        controller.refreshLabels();
        Assertions.assertThat(controller.getQuestionLabel()).isNotNull();
        Assertions.assertThat(controller.getAnswerTextArea()).isNotNull();
    }

    @Test
    void testClearAllButton() {
        ListView historyList = controller.getHistoryList();
        clickOn("#clearAllButton");
        verifyThat(historyList, hasItems(0));
    }

    @Test
    void testDeleteQuestion() {
        clickOn("#historyList").clickOn("title2");
        clickOn("#deleteButton");
        //sleep(1000);
        Assertions.assertThat(controller.getHistoryList().getItems().size()).isEqualTo(2);
        Assertions.assertThat(controller.getHistoryList().getItems().get(0).toString()).isEqualTo("title3");
        Assertions.assertThat(controller.getHistoryList().getItems().get(1).toString()).isEqualTo("title1");
    }

    @Test
    void testNewQuestion() {
        clickOn("#clearAllButton");
        // Simulate clicking the "Record" button, this is question3
        clickOn("#recordButton");
        // Wait for recording to start
        //sleep(2000);

        // Simulate stopping the recording
        clickOn("#recordButton");
        //sleep(1000);

        // Simulate clicking the "New Question" button, this is question4
        clickOn("#recordButton");
        // Wait for recording to start
        //sleep(2000);

        // Simulate stopping the recording
        clickOn("#recordButton");
        //sleep(1000);

        // Verify that the new question has been added to the history list
        ListView<Question> historyList = lookup("#historyList").query();
        Assertions.assertThat(historyList.getItems()).hasSize(2);
        Question newQuestion = historyList.getItems().get(1);
        Assertions.assertThat(newQuestion.question()).isEqualTo("question");
    }

    @Test
    void testRecordButton() {
        clickOn("#clearAllButton");
        clickOn("#recordButton");
        //sleep(500);
        Assertions.assertThat(lookup("#recordButton").queryButton().getText()).isEqualTo("Stop Recording");
        clickOn("#recordButton");
        //sleep(500);
        Assertions.assertThat(lookup("#recordButton").queryButton().getText()).isEqualTo("New Question");
    }

    //clicking on an existing question shouldn't change anything
    //covers sbst5
    @Test
    void testClickingOnExistingQuestion() {
        //clickOn("#clearAllButton");
        clickOn("#historyList").clickOn("title2");
        controller.refreshLabels();
        Assertions.assertThat(controller.getQuestionLabel().getText()).isEqualTo("question2");
        Assertions.assertThat(controller.getAnswerTextArea().getText()).isEqualTo("answer2");
        clickOn("#historyList").clickOn("title1");
        Assertions.assertThat(controller.getQuestionLabel().getText()).isEqualTo("question1");
        Assertions.assertThat(controller.getAnswerTextArea().getText()).isEqualTo("answer1");
        controller.refreshLabels();
        clickOn("#historyList").clickOn("title3");
        controller.refreshLabels();
        Assertions.assertThat(controller.getQuestionLabel().getText()).isEqualTo("question3");
        Assertions.assertThat(controller.getAnswerTextArea().getText()).isEqualTo("answer3");
    }

    @Test
    void testDeleteCurrentDisplayedQuestion() {
        clickOn("#historyList").clickOn("title1");
        controller.refreshLabels();
        clickOn("#deleteButton");
        controller.refreshLabels();
        sleep(1000);
        Assertions.assertThat(controller.getQuestionLabel().getText()).isEqualTo("");
        Assertions.assertThat(controller.getAnswerTextArea().getText()).isEqualTo("");
    }


    /*
     * New User uses SayIt Assistant and is unhappy with all the responses they receive (US1, US3, US4).
Open the “SayIt Assistant” app. You should see that there are no questions on the page since this is a new user.
Click the “+” to ask a new question.
Begin speaking my question out loud for the application to generate audio-to-text.
When finished speaking, click the “STOP” button.
Wait for “SayIt Assistant” to generate and display a response to the question. You should only see one question and response on the page at this time. You should see the question appear on the questions-list as well.
You are not happy with the response so you delete the question. You should see that there are no questions nor responses on the page anymore.
Click the “+” to ask another question.
You ask another question out loud for the application to generate audio-to-text.
Wait for “SayIt Assistant” to generate and display a response to the question. You should only see one question and response on the page at this time. You like this response to the question so you keep it.
Click the “+” to ask another question.
You ask another question out loud for the application to generate audio-to-text.
Suddenly, you’re worried someone may think you’re up to no good using this “SayIt Assistant '' app, so you want to clear your history. You click the “CLEAR ALL” button. Now, your page should have no questions nor responses being displayed.
You close the app.
     */
    @Test
    void testSBST4() {
        ListView<Question> historyList = lookup("#historyList").query();
        clickOn("#clearAllButton");
        controller.refreshLabels();
        clickOn("#recordButton"); //begin recording
        controller.refreshLabels();
        clickOn("#recordButton"); //stop recording
        controller.refreshLabels();
        Question newQuestion = historyList.getItems().get(0);
        Assertions.assertThat(newQuestion.toString()).isEqualTo("title");
        Assertions.assertThat(controller.getHistoryList().getItems().size()).isEqualTo(1);
        clickOn("#historyList").clickOn(historyList.getItems().get(0).toString());
        controller.refreshLabels();
        clickOn("#deleteButton");
        controller.refreshLabels();
        Assertions.assertThat(controller.getHistoryList().getItems()).isEmpty();
        clickOn("#recordButton"); //begin recording
        controller.refreshLabels();
        clickOn("#recordButton"); //stop recording
        controller.refreshLabels();
        newQuestion = historyList.getItems().get(0);
        Assertions.assertThat(newQuestion.toString()).isEqualTo("title");
        Assertions.assertThat(controller.getHistoryList().getItems().size()).isEqualTo(1);
        clickOn("#recordButton"); //begin recording
        controller.refreshLabels();
        clickOn("#recordButton"); //stop recording
        controller.refreshLabels();
        newQuestion = historyList.getItems().get(1);
        Assertions.assertThat(newQuestion.toString()).isEqualTo("title");
        Assertions.assertThat(controller.getHistoryList().getItems().size()).isEqualTo(2);
        clickOn("#clearAllButton");
        controller.refreshLabels();
        Assertions.assertThat(controller.getHistoryList().getItems()).isEmpty();
    }

    //second SBST6 test
    @Test
    void testSBST6() {
        ListView<Question> historyList = lookup("#historyList").query();
        clickOn("#historyList").clickOn("title1");
        controller.refreshLabels();
        Assertions.assertThat(controller.getQuestionLabel().getText()).isEqualTo("question1");
        Assertions.assertThat(controller.getAnswerTextArea().getText()).isEqualTo("answer1");
        clickOn("#deleteButton");
        controller.refreshLabels();
        Assertions.assertThat(controller.getQuestionLabel().getText()).isEqualTo("");
        Assertions.assertThat(controller.getAnswerTextArea().getText()).isEqualTo("");
        Assertions.assertThat(controller.getHistoryList().getItems().size()).isEqualTo(2);
        clickOn("#recordButton");
        controller.refreshLabels();
        clickOn("#recordButton");
        controller.refreshLabels();
        Assertions.assertThat(controller.getHistoryList().getItems().size()).isEqualTo(3);
        clickOn("#historyList").clickOn("title");
        controller.refreshLabels();
        Assertions.assertThat(controller.getQuestionLabel().getText()).isEqualTo("question");
        Assertions.assertThat(controller.getAnswerTextArea().getText()).isEqualTo("answer");
    }
}
