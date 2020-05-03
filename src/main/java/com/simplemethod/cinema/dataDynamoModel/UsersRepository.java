/*
 * Copyright  Michał Młodawski (SimpleMethod)(c) 2020.
 */

package com.simplemethod.cinema.dataDynamoModel;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableScan
public interface UsersRepository  extends CrudRepository<UsersModel, String> {
    UsersModel findByEmail(String email);
    List<UsersModel> findAllByEmail(String email);
    void deleteByEmail(String email);
    void deleteByPassword(String password);
}
