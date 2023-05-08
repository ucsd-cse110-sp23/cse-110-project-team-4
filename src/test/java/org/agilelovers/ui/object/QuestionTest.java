package org.agilelovers.ui.object;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

//temp comment
class QuestionTest {
    String title = "title";
    String question = "question";
    String answer = "answer";
    Question testQuestion = new Question(title  , question, answer);
    @Test
    void testQuestion() {
        Assertions.assertThat(testQuestion.question()).isEqualTo("question");
    }

    @Test
    void testAnswer(){
        Assertions.assertThat(testQuestion.answer()).isEqualTo("answer");
    }

    @Test
    void testTitle(){
        Assertions.assertThat(testQuestion.toString()).isEqualTo("title");
    }

    @Test
    void setTestAnswer(){
        testQuestion.setAnswer("newAnswer");
        Assertions.assertThat(testQuestion.answer()).isEqualTo("newAnswer");
    }

    @Test
    void setTestQuestion(){
        testQuestion.setQuestion("newQuestion");
        Assertions.assertThat(testQuestion.question()).isEqualTo("newQuestion");
    }

    @Test
    void toTestString(){
        Assertions.assertThat(testQuestion.toString()).isEqualTo("title");
    }
    @Test
    void setTestTitle(){
        testQuestion.setTitle("newTitle");
        Assertions.assertThat(testQuestion.toString()).isEqualTo("newTitle");
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
        Assertions.assertThat(question.toString()).isEqualTo("Title");
    }

    @Test
    void testToStringWithNullAnswer() {
        Question question = new Question("Title", "Question", null);
        Assertions.assertThat(question.toString()).isEqualTo("Title");
    }



}
