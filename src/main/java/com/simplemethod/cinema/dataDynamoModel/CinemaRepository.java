/*
 * Copyright  Michał Młodawski (SimpleMethod)(c) 2020.
 */

package com.simplemethod.cinema.dataDynamoModel;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableScan
public interface CinemaRepository  extends CrudRepository<CinemaModel, String> {
    CinemaModel findByID(String ID);
    List<CinemaModel> findAllByMovieName(String movieName);
    List<CinemaModel> findAllByDateBetween(Long startDate, Long stopDate);
}