package org.agilelovers.server.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Document("sayit-questions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDocument {
    @Id
    private String id;

    @NotNull
    @NotBlank
    private String userId;

    @NotNull
    @NotBlank
    private String question;

    @NotNull
    @NotBlank
    private String answer;
}