package com.simplemethod.cinema.hazelcastModel;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component
public class CinemaModel implements Serializable {
    String ID;
    Integer hall;
    Long date;
    String movieName;
    String movieDirector;
    int year;
    int lengthInMinutes;
    Map<String, String> seats;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Integer getHall() {
        return hall;
    }

    public void setHall(Integer hall) {
        this.hall = hall;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieDirector() {
        return movieDirector;
    }

    public void setMovieDirector(String movieDirector) {
        this.movieDirector = movieDirector;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getLengthInMinutes() {
        return lengthInMinutes;
    }

    public void setLengthInMinutes(int lengthInMinutes) {
        this.lengthInMinutes = lengthInMinutes;
    }

    public Map<String, String> getSeats() {
        return seats;
    }

    public void setSeats(Map<String, String> seats) {
        this.seats = seats;
    }

    public CinemaModel(String ID, Integer hall, Long date, String movieName, String movieDirector, int year, int lengthInMinutes, Map<String, String> seats) {
        this.ID = ID;
        this.hall = hall;
        this.date = date;
        this.movieName = movieName;
        this.movieDirector = movieDirector;
        this.year = year;
        this.lengthInMinutes = lengthInMinutes;
        this.seats = seats;
    }

    public CinemaModel(Integer hall, Long date, String movieName, String movieDirector, int year, int lengthInMinutes, Map<String, String> seats) {
        this.hall = hall;
        this.date = date;
        this.movieName = movieName;
        this.movieDirector = movieDirector;
        this.year = year;
        this.lengthInMinutes = lengthInMinutes;
        this.seats = seats;
    }

    public void CinemaModel(Integer hall, Long date, String movieName, String movieDirector, int year, int lengthInMinutes, Map<String, String> seats) {
        this.hall = hall;
        this.date = date;
        this.movieName = movieName;
        this.movieDirector = movieDirector;
        this.year = year;
        this.lengthInMinutes = lengthInMinutes;
        this.seats = seats;
    }

    public CinemaModel() {
    }
}
