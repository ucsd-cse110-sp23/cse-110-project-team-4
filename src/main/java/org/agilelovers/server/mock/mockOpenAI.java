package org.agilelovers.server.mock;

import org.agilelovers.server.common.OpenAIClient;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class mockOpenAI extends OpenAIClient {
    public mockOpenAI(){}

    @Override
    public String getAnswer(String question) {
        return "Nicholas is 6ft tall";
    }

    @Override
    public String getTranscription(MultipartFile file) {
        return "Question, How tall is Nicholas Lam";
    }
}
