package org.agilelovers.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.agilelovers.ui.object.Question;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;

class ControllerTest extends ApplicationTest{

    private Controller controller;
    private String title1 = "title1";
    private String title2 = "title2";
    private String question1 = "question1";
    private String question2 = "question2";
    private String answer1 = "answer1";
    private String answer2 = "answer2";

    private Question testQuestion1;
    private Question testQuestion2;

    @Override
    public void start(Stage stage) throws IOException {
        var fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/Main.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        //Scene scene = new Scene(root); //??
        //stage.setScene(scene);
        //stage.show();
        System.out.println("start() called and finished");
    }
    @BeforeEach
    void setup() throws IOException {
        testQuestion1 = new Question(title1, question1, answer1);
        testQuestion2 = new Question(title2, question2, answer2);

        controller.addQuestion(testQuestion1);
        controller.addQuestion(testQuestion2);
        controller.initHistoryList();
        controller.test = true;
        System.out.println("setup() called and finished");
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
        Assertions.assertThat(controller.getAnswerLabel()).isNotNull();
    }

    @Test
    void testClearAllButton() {
        ListView historyList = controller.getHistoryList();
        clickOn("#clearAllButton");
        verifyThat(historyList, hasItems(0));
    }

    @Test
    void testDeleteQuestion() {
        clickOn("#historyList").clickOn(testQuestion2.toString());
        clickOn("#deleteButton");
        //sleep(1000);
        Assertions.assertThat(controller.getHistoryList().getItems()).containsExactly(testQuestion1);
        Assertions.assertThat(controller.getHistoryList().getItems().size()).isEqualTo(1);
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
        Assertions.assertThat(newQuestion.question()).isEqualTo("question2");
    }

    @Test
    void testRecordButton() {
        clickOn("#recordButton");
        Assertions.assertThat(lookup("#recordButton").queryButton().getText()).isEqualTo("Stop Recording");
        clickOn("#recordButton");
        Assertions.assertThat(lookup("#recordButton").queryButton().getText()).isEqualTo("New Question");
    }
}
