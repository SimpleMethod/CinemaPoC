# CinemaPoC
Poof of the concept of simple cinema database management using Hazelcast and Amazon DynamoDB.

![Java CI with Maven](https://github.com/SimpleMethod/CinemaPoC/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master&event=push)

## 🚀 Features
1. Add users.
2. Change password for users (SHA-256 + sole).
3. Remove users.
4. View users information.
5. View movies information.
6. View user reservations for movie.
7. Adds new movies.
8. Add user reservations.
9. Delete a users reservation. 
10. Errors handling and data validation.

## 🕸️ REST Documentation:
[REST API](https://documenter.getpostman.com/view/7673549/SzmYA2eQ?version=latest)

## 📖 Documentation:
[JavaDoc](https://github.com/SimpleMethod/CinemaPoC/tree/master/apidocs)

## 🎥 Short video of presentation:
https://youtu.be/zpj2zNMYlUs

## 🔨 Start-up instructions:
1. A graphic interface is available at http://localhost:8090/ .
2. Using DynamoDB requires that you have your own Amazon Web Services IAM keys, which should be placed in the application.properties file. 
3. After starting, it is necessary to initialize table using endpoints: 
http://localhost:8090/1.0/initcinema
http://localhost:8090/1.0/initusers
Using HTTP header with API key and abc2137 value. 

## 🗺 Class diagram:
![CinemaPackage](https://raw.githubusercontent.com/SimpleMethod/CinemaPoC/master/PackageCinema.png)
