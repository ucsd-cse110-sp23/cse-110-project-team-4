package org.agilelovers.common.documents;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Document("sayit-questions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiIgnore
public class QuestionDocument {
    @ApiModelProperty(notes = "Unique ID generated by MongoDB")
    @Id
    private String id;

    @ApiModelProperty(notes = "Timestamp this question was created at")
    @CreatedDate
    private Date createdAt;

    @ApiModelProperty(notes = "User ID associated with this question", required = true)
    @NotNull
    @NotBlank
    private String userId;

    @ApiModelProperty(notes = "The question transcribed by OpenAI Whisper", required = true)
    @NotNull
    @NotBlank
    private String entirePrompt;

    @ApiModelProperty(notes = "The answer to the question generated by ChatGPT", required = true)
    @NotNull
    @NotBlank
    private String answer;
}