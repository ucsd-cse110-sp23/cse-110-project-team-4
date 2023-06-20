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
public class QuestionModel {
    @ApiModelProperty(notes = "The question transcribed by OpenAI Whisper", required = true)
    private String prompt;
}
