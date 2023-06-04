package org.agilelovers.server.email;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Document("sayit-emails")
@Data
@Builder
@AllArgsConstructor
public class EmailDocument {
    @ApiModelProperty(notes = "Unique ID generated by MongoDB")
    @Id
    private String id;

    @ApiModelProperty(notes = "Timestamp this question was created at")
    @CreatedDate
    private Date createdDate;

    @ApiModelProperty(notes = "User ID associated with this question", required = true)
    @NotNull
    @NotBlank
    private String userId;

    @ApiModelProperty(notes = "The email prompt used to generate the body", required = true)
    @NotNull
    @NotBlank
    private String prompt;

    @ApiModelProperty(notes = "The body of the email", required = true)
    @NotNull
    @NotBlank
    private String body;
}