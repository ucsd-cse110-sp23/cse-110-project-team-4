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


    /*
    TODO: need to add more "non-trivial" tests to cover
     */

}
