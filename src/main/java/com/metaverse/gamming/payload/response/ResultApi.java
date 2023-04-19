package com.metaverse.gamming.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResultApi {

    private int statusCode;

    private String message;

}
