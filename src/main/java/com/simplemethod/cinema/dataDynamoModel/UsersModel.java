/*
 * Copyright  Michał Młodawski (SimpleMethod)(c) 2020.
 */

package com.simplemethod.cinema.dataDynamoModel;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Cinema_users")
public class UsersModel {
    String email;
    String password;

    @DynamoDBHashKey
    @DynamoDBAttribute
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @DynamoDBAttribute
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UsersModel(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UsersModel() {
    }
}
