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
        Assertions.assertThat(testQuestion.getQuestion()).isEqualTo("question");
    }

    @Test
    void testAnswer() {
        Assertions.assertThat(testQuestion.getAnswer()).isEqualTo("answer");
    }

    @Test
    void testToString() {
        Assertions.assertThat(testQuestion.toString()).hasToString("question");
    }

    @Test
    void setTestAnswer() {
        testQuestion.setAnswer("newAnswer");
        Assertions.assertThat(testQuestion.getAnswer()).isEqualTo("newAnswer");
    }

    @Test
    void setTestQuestion() {
        testQuestion.setQuestion("newQuestion");
        Assertions.assertThat(testQuestion.getQuestion()).isEqualTo("newQuestion");
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
        Assertions.assertThat(emptyQuestion.getQuestion()).isEmpty();
    }

    @Test
    void testEmptyAnswer() {
        Question emptyAnswer = new Question();
        Assertions.assertThat(emptyAnswer.getAnswer()).isEmpty();
    }

    @Test
    void testEmptyTitle() {
        Question emptyTitle = new Question();
        Assertions.assertThat(emptyTitle.toString()).isEmpty();
    }

    @Test
    void testSetNullAnswer() {
        testQuestion.setAnswer(null);
        Assertions.assertThat(testQuestion.getAnswer()).isNull();
    }

    @Test
    void testSetNullQuestion() {
        testQuestion.setQuestion(null);
        Assertions.assertThat(testQuestion.getQuestion()).isNull();
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