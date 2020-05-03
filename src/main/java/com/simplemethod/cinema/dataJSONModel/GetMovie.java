/*
 * Copyright  Michał Młodawski (SimpleMethod)(c) 2020.
 */

package com.simplemethod.cinema.dataJSONModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "movieID",
})
public class GetMovie {

    @NotEmpty
    @JsonProperty("movieID")
    public String movieID;

    public String getShow() {
        return movieID;
    }

    public GetMovie()
    {
    }
}
