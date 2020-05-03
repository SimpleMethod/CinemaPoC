/*
 * Copyright  Michał Młodawski (SimpleMethod)(c) 2020.
 */

package com.simplemethod.cinema.webController;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.hazelcast.client.HazelcastClient;
import com.simplemethod.cinema.dataDynamoModel.CinemaModel;
import com.simplemethod.cinema.dataDynamoModel.CinemaRepository;
import com.simplemethod.cinema.dataDynamoModel.UsersRepository;
import com.simplemethod.cinema.dataJSONModel.MovieModel;
import com.simplemethod.cinema.dataJSONModel.UsersModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RESTServices {

    @Value("${api.key}")
    private String key;

    @Value("${password.key}")
    private String hash;

    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    /**
     * Tworzenie tabeli dla użytkowników.
     * @param apiKey Nagłówek HTTP z kodem autoryzacji.
     * @return Status kodu HTTP.
     */
    @PutMapping(value = "/1.0/initusers", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> initUsers(@Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key)) {

            dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
            CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(com.simplemethod.cinema.dataDynamoModel.UsersModel.class);
            tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
            amazonDynamoDB.createTable(tableRequest);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Tworzenie tabeli dla kina.
     * @param apiKey Nagłówek HTTP z kodem autoryzacji.
     * @return Status kodu HTTP.
     */
    @PutMapping(value = "/1.0/initcinema", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> initCinema(@Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key)) {
            dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
            CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(CinemaModel.class);
            tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
            amazonDynamoDB.createTable(tableRequest);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }


    /**
     * Pobieranie informacji na temat użytkownika.
     *
     * @param userID Identyfikator użytkownika.
     * @param apiKey Nagłówek HTTP z kodem autoryzacji.
     * @return Model użytkownika w postaci email, hasło oraz status kodu HTTP.
     */
    @GetMapping(value = "/1.0/users/{userID}", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<com.simplemethod.cinema.dataDynamoModel.UsersModel> getUser(@Valid @PathVariable String userID, @Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key) && userID != null) {
            com.simplemethod.cinema.dataDynamoModel.UsersModel result = usersRepository.findByEmail(userID);
            if (result == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Pobieranie informacji na temat użytkowników.
     *
     * @param apiKey Nagłówek HTTP z kodem autoryzacji.
     * @return Mapa z modelem użytkowników oraz status kodu HTTP.
     */
    @GetMapping(value = "/1.0/users/", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<Iterable<com.simplemethod.cinema.dataDynamoModel.UsersModel>> getUsers(@Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key)) {
            Iterable<com.simplemethod.cinema.dataDynamoModel.UsersModel> result = usersRepository.findAll();
            if (result == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Dodawanie nowego użytkownika.
     *
     * @param usersModel Model użytkownika w postaci JSON.
     * @param apiKey     Nagłówek HTTP z kodem autoryzacji.
     * @return Model użytkownika w postaci email, hasło oraz status kodu HTTP.
     */
    @PutMapping(value = "/1.0/users", consumes = "application/json", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<com.simplemethod.cinema.dataDynamoModel.UsersModel> setUsers(@Valid @RequestBody UsersModel usersModel, @Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key)) {
            if (usersRepository.findByEmail(usersModel.email) != null) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            com.simplemethod.cinema.dataDynamoModel.UsersModel dynamoModel = new com.simplemethod.cinema.dataDynamoModel.UsersModel();
            try {
                dynamoModel.setPassword(StringHash(usersModel.password));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            dynamoModel.setEmail(usersModel.email);
            usersRepository.save(dynamoModel);
            com.simplemethod.cinema.dataDynamoModel.UsersModel checkUser = usersRepository.findByEmail(usersModel.email);
            if (usersRepository.findByEmail(usersModel.email) == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(checkUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Aktualizowanie hasła użytkownika.
     *
     * @param getUser Model użytkownika w postaci JSON.
     * @param apiKey     Nagłówek HTTP z kodem autoryzacji.
     * @return Status kodu HTTP.
     */
    @PostMapping(value = "/1.0/users", consumes = "application/json", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<com.simplemethod.cinema.dataDynamoModel.UsersModel> updatePassword(@Valid @RequestBody UsersModel getUser, @Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key)) {
            com.simplemethod.cinema.dataDynamoModel.UsersModel result = usersRepository.findByEmail(getUser.email);
            if (result == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            try {
                result.setPassword(StringHash(getUser.password));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            usersRepository.save(result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Usuwanie użytkownika.
     *
     * @param userID Identyfikator użytkownika.
     * @param apiKey Nagłówek HTTP z kodem autoryzacji.
     * @return Status kodu HTTP.
     */
    @DeleteMapping(value = "/1.0/users/{userID}", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> deleteUsers(@Valid @PathVariable String userID, @Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key)) {
            if (usersRepository.findByEmail(userID) != null) {
                usersRepository.deleteByEmail(userID);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Przeglądanie informacji o filmie.
     *
     * @param id     Identyfikator filmu.
     * @param apiKey Nagłówek HTTP z kodem autoryzacji.
     * @return Model filmu oraz status kodu HTTP.
     */
    @GetMapping(value = "/1.0/movie/{id}", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<CinemaModel> getMovies(@Valid @PathVariable String id, @Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key) && id != null) {
            CinemaModel result = cinemaRepository.findByID(id);
            if (result == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Przeglądanie dostępnych filmów.
     *
     * @param apiKey Nagłówek HTTP z kodem autoryzacji.
     * @return Mapa modelu filmów oraz status kodu HTTP.
     */
    @GetMapping(value = "/1.0/movie/", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<Iterable<CinemaModel>> getMovie(@Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key)) {
            Iterable<CinemaModel> result = cinemaRepository.findAll();
            if (result == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }




    /**
     * Wyświetlanie filmów z podanego zakresu.
     *
     * @param startdate Początkowy zakres wyszukiowania daty.
     * @param enddate   Końcowy zakres wyszukiwania daty.
     * @param apiKey    Nagłówek HTTP z kodem autoryzacji.
     * @return Lista modelu filmów oraz status kodu HTTP.
     */
    @GetMapping(value = "/1.0/movie/{startdate}/{enddate}", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<List<CinemaModel>> getMovie(@Valid @PathVariable Long startdate, @PathVariable Long enddate, @Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key)) {
            List<CinemaModel> result = cinemaRepository.findAllByDateBetween(startdate, enddate);
            if (result == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }


            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Dodawanie nowego filmu.
     * @param movieModel Model filmu w postaci JSON.
     * @param apiKey    Nagłówek HTTP z kodem autoryzacji.
     * @return Status kodu HTTP.
     */
    @PutMapping(value = "/1.0/movie", consumes = "application/json", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<CinemaModel> setMovie(@Valid @RequestBody MovieModel movieModel, @Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key)) {
            if (cinemaRepository.findByID(movieModel.movieID) != null) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            com.simplemethod.cinema.dataDynamoModel.CinemaModel dynamoModel = new com.simplemethod.cinema.dataDynamoModel.CinemaModel();
            dynamoModel.setID(movieModel.movieID);
            dynamoModel.setHall(movieModel.movieHallNumber);
            dynamoModel.setDate(movieModel.showData);
            dynamoModel.setMovieName(movieModel.movieName);
            dynamoModel.setMovieDirector(movieModel.movieDirector);
            dynamoModel.setYear(movieModel.movieYear);
            dynamoModel.setLengthInMinutes(movieModel.movieLength);

            if (movieModel.hallSeat == null) {
                Map<String, String> seats = new HashMap<>();
                for (int i = 1; i <= 20; i++) {
                    seats.put(Integer.toString(i), "-1");
                }
                dynamoModel.setSeats(seats);
            } else {
                dynamoModel.setSeats(movieModel.hallSeat);
            }
            cinemaRepository.save(dynamoModel);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Rezerwacja filmu.
     * @param userID Identyfikator użytkownika.
     * @param movieID Identyfikator filmu.
     * @param seatsNumber Numer siedzenia.
     * @param apiKey    Nagłówek HTTP z kodem autoryzacji.
     * @return Status kodu HTTP.
     */
    @PostMapping(value = "/1.0/movie/{userID}/{movieID}/{seatsNumber}", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<CinemaModel> seatsReservation(@Valid @PathVariable String userID, @Valid @PathVariable String movieID, @Valid @PathVariable String seatsNumber, @Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key) || movieID == null || seatsNumber == null || userID == null) {
            CinemaModel result = cinemaRepository.findByID(movieID);
            if (result == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Map<String, String> reservedSeats = result.getSeats();

            assert seatsNumber != null;
            if(Integer.parseInt(seatsNumber)>reservedSeats.size())
            {
                HazelcastClient.shutdownAll();
                return new ResponseEntity<>(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
            }

            if (reservedSeats.get(seatsNumber) != null) {
                if (reservedSeats.get(seatsNumber).equals("-1")) {
                    reservedSeats.replace(seatsNumber, userID);
                    cinemaRepository.save(result);
                    return new ResponseEntity<>(result, HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Rezerwacja filmu.
     * @param userID Identyfikator użytkownika.
     * @param movieID Identyfikator filmu.
     * @param seatsNumber Numer siedzenia.
     * @param apiKey    Nagłówek HTTP z kodem autoryzacji.
     * @return Status kodu HTTP.
     */
    @DeleteMapping(value = "/1.0/movie/{userID}/{movieID}/{seatsNumber}", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<CinemaModel> RemoveReservation(@Valid @PathVariable String userID, @Valid @PathVariable String movieID, @Valid @PathVariable String seatsNumber, @Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key) || movieID == null || seatsNumber == null || userID == null) {
            CinemaModel result = cinemaRepository.findByID(movieID);
            if (result == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Map<String, String> reservedSeats = result.getSeats();
            assert seatsNumber != null;
            if(Integer.parseInt(seatsNumber)>reservedSeats.size())
            {
                HazelcastClient.shutdownAll();
                return new ResponseEntity<>(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
            }
            if (reservedSeats.get(seatsNumber) != null) {
                if (reservedSeats.get(seatsNumber).equals(userID)) {
                    reservedSeats.replace(seatsNumber, "-1");
                    cinemaRepository.save(result);
                    return new ResponseEntity<>(result, HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /** Haszowanie danych metodą SHA-256 + sola.
     * @param data Dane do haszowania.
     * @return zhaszowane dane
     * @throws NoSuchAlgorithmException
     */
    public String StringHash(String data) throws NoSuchAlgorithmException {
        String hashData = data + hash;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return Base64.getEncoder().encodeToString(digest.digest(hashData.getBytes(StandardCharsets.UTF_8)));
    }
}
