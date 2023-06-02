package org.agilelovers.server.question;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Document("sayit-questions")
@Data
@Builder
@AllArgsConstructor
public class QuestionDocument {
    @ApiModelProperty(notes = "Unique ID generated by MongoDB")
    @Id
    private String id;

    @ApiModelProperty(notes = "User ID associated with this question", required = true)
    @NotNull
    @NotBlank
    private String userId;

    @ApiModelProperty(notes = "The question transcribed by OpenAI Whisper", required = true)
    @NotNull
    @NotBlank
    private String question;

    @ApiModelProperty(notes = "The answer to the question generated by ChatGPT", required = true)
    @NotNull
    @NotBlank
    private String answer;
}