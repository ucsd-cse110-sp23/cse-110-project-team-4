package org.agilelovers.ui;

import org.agilelovers.ui.object.Question;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuestionTest {
    String id = "id";
    String userid = "userid";
    String question = "question";
    String answer = "answer";
    Question testQuestion;


    @BeforeEach
    void setUp() {
        testQuestion = new Question(id, userid, question, answer);
    }

    @Test
    void testQuestion() {
        Assertions.assertThat(testQuestion.getPromptCommand()).isEqualTo("question");
    }

    @Test
    void testAnswer() {
        Assertions.assertThat(testQuestion.getResponse()).isEqualTo("answer");
    }

    @Test
    void testToString() {
        Assertions.assertThat(testQuestion.toString()).hasToString("question");
    }

    @Test
    void setTestAnswer() {
        testQuestion.setResponse("newAnswer");
        Assertions.assertThat(testQuestion.getResponse()).isEqualTo("newAnswer");
    }

    @Test
    void setTestQuestion() {
        testQuestion.setPromptCommand("newQuestion");
        Assertions.assertThat(testQuestion.getPromptCommand()).isEqualTo("newQuestion");
    }

    @Test
    void toTestString() {
        Assertions.assertThat(testQuestion.toString()).hasToString("question");
    }

    @Test
    void setIdTest() {
        testQuestion.setId("New Question ID");
        Assertions.assertThat(testQuestion.toString()).hasToString("question");
        Assertions.assertThat(testQuestion.getId()).isEqualTo("New Question ID");
    }

    @Test
    void testEmptyQuestion() {
        Question emptyQuestion = new Question();
        Assertions.assertThat(emptyQuestion.getPromptCommand()).isEmpty();
    }

    @Test
    void testEmptyAnswer() {
        Question emptyAnswer = new Question();
        Assertions.assertThat(emptyAnswer.getResponse()).isEmpty();
    }

    @Test
    void testEmptyTitle() {
        Question emptyTitle = new Question();
        Assertions.assertThat(emptyTitle.toString()).isEmpty();
    }

    @Test
    void testSetNullAnswer() {
        testQuestion.setResponse(null);
        Assertions.assertThat(testQuestion.getResponse()).isNull();
    }

    @Test
    void testSetNullQuestion() {
        testQuestion.setPromptCommand(null);
        Assertions.assertThat(testQuestion.getPromptCommand()).isNull();
    }

    @Test
    void testSetNullTitle() {
        testQuestion.setId(null);
        Assertions.assertThat(testQuestion.toString()).hasToString("question");
    }

    @Test
    void testToStringWithNullQuestion() {
        Question question = new Question("ID", "USERID",null, "ANSWER");
        Assertions.assertThat(question.toString()).isNull();
    }

    @Test
    void testToStringWithNullAnswer() {
        Question question = new Question("ID", "USERID","QUESTION", null);
        Assertions.assertThat(question.toString()).hasToString("QUESTION");
    }
}