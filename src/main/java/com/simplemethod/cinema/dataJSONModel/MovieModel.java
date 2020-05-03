/*
 * Copyright  Michał Młodawski (SimpleMethod)(c) 2020.
 */

package com.simplemethod.cinema.dataJSONModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@JsonPropertyOrder({
        "ID",
        "hallNumber",
        "date",
        "movieName",
        "movieDirector",
        "year",
        "lengthInMinutes",
        "seat"

})
public class MovieModel {


    @NotEmpty
    @JsonProperty("ID")
    public String movieID;
    @NotNull
    @JsonProperty("hallNumber")
    public Integer movieHallNumber;
    @NotNull
    @JsonProperty("date")
    public Long showData;
    @NotEmpty
    @JsonProperty("movieName")
    public String movieName;
    @NotEmpty
    @JsonProperty("movieDirector")
    public String movieDirector;
    @NotNull
    @JsonProperty("year")
    public Integer movieYear;
    @NotNull
    @JsonProperty("lengthInMinutes")
    public Integer movieLength;

    @JsonProperty("seat")
    public Map<String, String> hallSeat;

    public MovieModel(@NotEmpty String showID, @NotEmpty Integer showNumber, @NotEmpty Long showData, @NotEmpty String movieName, @NotEmpty String movieDirector, @NotEmpty Integer movieYear, @NotEmpty Integer movieLength, @NotEmpty Map<String, String> hallSeat) {
        this.movieID = showID;
        this.movieHallNumber = showNumber;
        this.showData = showData;
        this.movieName = movieName;
        this.movieDirector = movieDirector;
        this.movieYear = movieYear;
        this.movieLength = movieLength;
        this.hallSeat = hallSeat;
    }

    public MovieModel()
    {

    }
}
