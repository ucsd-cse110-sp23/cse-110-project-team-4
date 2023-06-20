package org.agilelovers.common.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecureUserModel {
    @ApiModelProperty(notes = "Username of the user", required = true)
    private String username;

    @ApiModelProperty(notes = "Password of the user", required = true)
    private String password;

    @ApiModelProperty(notes = "The secret required to use this API", required = true)
    private String apiPassword;
}
