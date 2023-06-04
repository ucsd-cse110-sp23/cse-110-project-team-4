package org.agilelovers.server.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Document("sayit-emails")
@Data
@Builder
@AllArgsConstructor
public class EmailDocument {
    @Id
    private String id;

    @NotNull
    @NotBlank
    private String userId;

    @NotNull
    @NotBlank
    private String prompt;

    @NotNull
    @NotBlank
    private String body;
}
