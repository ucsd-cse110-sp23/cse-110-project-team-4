package org.agilelovers.server.mock;

import org.agilelovers.ui.object.Question;

public class mockRecording {
    public mockRecording() {
    }

    public void startRecording() {
    }

    public Question endRecording() {
        return new Question("MockedQuestionID", "MockedUserID", "Question, How tall is Nicholas Lam", "Nicholas is 6ft tall");
    }
}
