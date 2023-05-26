package org.agilelovers.server.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Document("sayit-commands")
@Data
@Builder
@AllArgsConstructor
public class CommandDocument {
    @Id
    private String id;

    @NotNull
    @NotBlank
    private String userId;

    @NotNull
    @NotBlank
    private String command;

    private String output;
}
