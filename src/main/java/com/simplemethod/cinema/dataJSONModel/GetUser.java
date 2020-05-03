/*
 * Copyright  Michał Młodawski (SimpleMethod)(c) 2020.
 */

package com.simplemethod.cinema.dataJSONModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "email",
})

@Component
public class GetUser {

    @NotEmpty
    @JsonProperty("email")
    public String email;

    public GetUser(){

    }
}
