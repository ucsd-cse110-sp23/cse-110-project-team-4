package org.agilelovers.ui.object;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.agilelovers.ui.controller.MainController;
import org.agilelovers.mock.ui.controller.MockMainController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;

class QuestionTest extends ApplicationTest {
    MockMainController controller;
    String title = "title";
    String question = "question";
    String answer = "answer";
    Question testQuestion;

    @Override
    public void start(Stage stage) throws IOException {
        var fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/MockMain.fxml"));
        Parent root = fxmlLoader.load();
        MainController.instance = fxmlLoader.getController();
        this.controller = (MockMainController) MainController.instance;
        Scene scene = new Scene(root);
        stage.setScene(scene);
        System.out.println("start() called and finished");
    }

    @BeforeEach
    void setUp() {
        testQuestion = new Question(title, question, answer);
    }

    @Test
    void testQuestion() {
        Assertions.assertThat(testQuestion.question()).isEqualTo("question");
    }

    @Test
    void testAnswer() {
        Assertions.assertThat(testQuestion.answer()).isEqualTo("answer");
    }

    @Test
    void testTitle() {
        Assertions.assertThat(testQuestion.toString()).hasToString("title");
    }

    @Test
    void setTestAnswer() {
        testQuestion.setAnswer("newAnswer");
        Assertions.assertThat(testQuestion.answer()).isEqualTo("newAnswer");
    }

    @Test
    void setTestQuestion() {
        testQuestion.setQuestion("newQuestion");
        Assertions.assertThat(testQuestion.question()).isEqualTo("newQuestion");
    }

    @Test
    void toTestString() {
        Assertions.assertThat(testQuestion.toString()).hasToString("title");
    }

    @Test
    void setTestTitle() {
        testQuestion.setTitle("newTitle");
        Assertions.assertThat(testQuestion.toString()).hasToString("newTitle");
    }

    @Test
    void testEmptyQuestion() {
        Question emptyQuestion = new Question();
        Assertions.assertThat(emptyQuestion.question()).isEmpty();
    }

    @Test
    void testEmptyAnswer() {
        Question emptyAnswer = new Question();
        Assertions.assertThat(emptyAnswer.answer()).isEmpty();
    }

    @Test
    void testEmptyTitle() {
        Question emptyTitle = new Question();
        Assertions.assertThat(emptyTitle.toString()).isEmpty();
    }

    @Test
    void testSetNullAnswer() {
        testQuestion.setAnswer(null);
        Assertions.assertThat(testQuestion.answer()).isNull();
    }

    @Test
    void testSetNullQuestion() {
        testQuestion.setQuestion(null);
        Assertions.assertThat(testQuestion.question()).isNull();
    }

    @Test
    void testSetNullTitle() {
        testQuestion.setTitle(null);
        Assertions.assertThat(testQuestion.toString()).isNull();
    }

    @Test
    void testToStringWithNullQuestion() {
        Question question = new Question("Title", null, "Answer");
        Assertions.assertThat(question.toString()).hasToString("Title");
    }

    @Test
    void testToStringWithNullAnswer() {
        Question question = new Question("Title", "Question", null);
        Assertions.assertThat(question.toString()).hasToString("Title");
    }
}