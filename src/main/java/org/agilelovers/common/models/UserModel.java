package org.agilelovers.common.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    @ApiModelProperty(notes = "Username of the user", required = true)
    private String username;

    @ApiModelProperty(notes = "Password of the user", required = true)
    private String password;
}
