package org.agilelovers.common.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailModel {
    @ApiModelProperty(notes = "The query transcribed by OpenAI Whisper", required = true)
    private String prompt;
}
