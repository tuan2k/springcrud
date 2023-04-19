package com.metaverse.gamming.dto;

import com.metaverse.gamming.model.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserDataDTO {

    @ApiModelProperty(position = 0)
    private String username;

    @ApiModelProperty(position = 1)
    private String email;

    @ApiModelProperty(position = 2)
    private String password;

    @ApiModelProperty(position = 3)
    List<User> Users;

}
