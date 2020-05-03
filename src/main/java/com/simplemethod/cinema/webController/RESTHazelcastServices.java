package com.simplemethod.cinema.webController;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.simplemethod.cinema.dataJSONModel.MovieModel;
import com.simplemethod.cinema.dataJSONModel.UsersModel;
import com.simplemethod.cinema.hazelcastModel.CinemaModel;
import com.simplemethod.cinema.hazelcastModel.UserModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map;

@RestController
public class RESTHazelcastServices {

    @Value("${api.key}")
    private String key;

    @Value("${password.key}")
    private String hash;


    /**
     * Pobieranie informacji na temat użytkownika.
     *
     * @param userID Identyfikator użytkownika.
     * @param apiKey Nagłówek HTTP z kodem autoryzacji.
     * @return Model użytkownika w postaci email, hasło oraz status kodu HTTP.
     */
    @GetMapping(value = "/1.1/users/{userID}", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<UserModel> getUser(@Valid @PathVariable String userID, @Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key) && userID != null) {
            HazelcastClient.shutdownAll();
            HazelcastInstance client = OpenConnection();
            UserModel result = new UserModel();
            IMap<String, String> userModelIList = client.getMap("users");
            for (Map.Entry<String, String> entry : userModelIList.entrySet()) {
                if (userID.equals(entry.getKey())) {
                    result.setEmail(entry.getKey());
                    result.setPassword(entry.getValue());
                }
            }

            if (result.getEmail() == null) {
                HazelcastClient.shutdownAll();
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            HazelcastClient.shutdownAll();
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        HazelcastClient.shutdownAll();
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);

    }

    /**
     * Pobieranie informacji na temat użytkowników.
     *
     * @param apiKey Nagłówek HTTP z kodem autoryzacji.
     * @return Mapa z modelem użytkowników oraz status kodu HTTP.
     */
    @GetMapping(value = "/1.1/users/", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<Iterable<UserModel>> getUsers(@Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key)) {
            HazelcastInstance client = OpenConnection();
            IMap<String, String> userModelIList = client.getMap("users");
            List<UserModel> userModels= new ArrayList();
            userModelIList.entrySet().forEach(entry->{
                userModels.add(new UserModel(entry.getKey(),entry.getValue()));
            });

            if (userModelIList.size() == 0) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(userModels, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Dodawanie nowego użytkownika.
     *
     * @param usersModel Model użytkownika w postaci JSON.
     * @param apiKey     Nagłówek HTTP z kodem autoryzacji.
     * @return Model użytkownika w postaci email, hasło oraz status kodu HTTP.
     * @throws NoSuchAlgorithmException
     */
    @PutMapping(value = "/1.1/users", consumes = "application/json", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> setUsers(@Valid @RequestBody UsersModel usersModel, @Valid @RequestHeader("API") String apiKey) throws NoSuchAlgorithmException {
        if (apiKey.equals(key)) {
            HazelcastClient.shutdownAll();
            HazelcastInstance client = OpenConnection();
            IMap<String, String> usersMap = client.getMap("users");

            if (usersMap.size() == 0) {
                usersMap.put(usersModel.email, StringHash(usersModel.password));
                HazelcastClient.shutdownAll();
                return new ResponseEntity<>(HttpStatus.OK);
            }
            int status = 0;
            String email = usersModel.email;
            for (String name : usersMap.keySet()) {
                String key = name.toString();
                if (key.equals(email)) {
                    status = -1;
                    HazelcastClient.shutdownAll();
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
            }
            if (status == 0) {
                usersMap.put(usersModel.email, StringHash(usersModel.password));
                HazelcastClient.shutdownAll();
                return new ResponseEntity<>(HttpStatus.OK);
            }

            HazelcastClient.shutdownAll();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        HazelcastClient.shutdownAll();
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Aktualizowanie hasła użytkownika.
     *
     * @param usersModel Model użytkownika w postaci JSON.
     * @param apiKey     Nagłówek HTTP z kodem autoryzacji.
     * @return Status kodu HTTP.
     * @throws NoSuchAlgorithmException
     */
    @PostMapping(value = "/1.1/users", consumes = "application/json", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UsersModel usersModel, @Valid @RequestHeader("API") String apiKey) throws NoSuchAlgorithmException {
        if (apiKey.equals(key)) {
            HazelcastInstance client = OpenConnection();
            IMap<String, String> userModelIList = client.getMap("users");
            String email = usersModel.email;
            for (String name : userModelIList.keySet()) {
                String key = name.toString();
                if (key.equals(email)) {
                    userModelIList.replace(usersModel.email, StringHash(usersModel.password));
                    HazelcastClient.shutdownAll();
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
            HazelcastClient.shutdownAll();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
    @DeleteMapping(value = "/1.1/users/{userID}", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> deleteUsers(@Valid @PathVariable String userID, @Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key)) {
            HazelcastInstance client = OpenConnection();
            IMap<String, String> userModelIList = client.getMap("users");
            for (Map.Entry<String, String> entry : userModelIList.entrySet()) {
                if (userID.equals(entry.getKey())) {
                    userModelIList.remove(userID);
                    HazelcastClient.shutdownAll();
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
            HazelcastClient.shutdownAll();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
    @GetMapping(value = "/1.1/movie/{id}", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<CinemaModel> getMovies(@Valid @PathVariable String id, @Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key) && id != null) {
            HazelcastClient.shutdownAll();
            HazelcastInstance client = OpenConnection();
            CinemaModel result = new CinemaModel();
            IMap<String, CinemaModel> cinemaModelIMap = client.getMap("cinema");
            for (Map.Entry<String, CinemaModel> entry : cinemaModelIMap.entrySet()) {
                if (id.equals(entry.getKey())) {
                    result = entry.getValue();
                    result.setID(entry.getKey());
                }
            }
            if (result.getID() == null) {
                HazelcastClient.shutdownAll();
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            HazelcastClient.shutdownAll();
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
    @GetMapping(value = "/1.1/movie/", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<List<CinemaModel>> getMovie(@Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key)) {
            HazelcastInstance client = OpenConnection();
            IMap<String, CinemaModel> cinemaModelIMap = client.getMap("cinema");
            if (cinemaModelIMap.size() == 0) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            List<CinemaModel> userModels= new ArrayList();
            cinemaModelIMap.entrySet().forEach(entry->{
                userModels.add(new CinemaModel(entry.getValue().getID(),entry.getValue().getHall(),entry.getValue().getDate(),entry.getValue().getMovieName(),entry.getValue().getMovieDirector(),entry.getValue().getYear(),entry.getValue().getLengthInMinutes(),entry.getValue().getSeats()));
            });

            return new ResponseEntity<>(userModels, HttpStatus.OK);
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
    @GetMapping(value = "/1.1/movie/{startdate}/{enddate}", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<List<CinemaModel>> getMovie(@Valid @PathVariable Long startdate, @PathVariable Long enddate, @Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key)) {
            HazelcastInstance client = OpenConnection();
            List<CinemaModel> cinemaModels = new ArrayList();
            IMap<String, CinemaModel> cinemaModelIMap = client.getMap("cinema");
            for (Map.Entry<String, CinemaModel> entry : cinemaModelIMap.entrySet()) {
                CinemaModel cinemaModel = entry.getValue();
                if (timestamp(cinemaModel.getDate(), startdate, enddate)) {
                    cinemaModels.add(cinemaModel);
                }
            }
            if (cinemaModels.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(cinemaModels, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Dodawanie nowego filmu.
     * @param movieModel Model filmu w postaci JSON.
     * @param apiKey    Nagłówek HTTP z kodem autoryzacji.
     * @return Status kodu HTTP.
     */
    @PutMapping(value = "/1.1/movie", consumes = "application/json", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> setMovie(@Valid @RequestBody MovieModel movieModel, @Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key)) {

            CinemaModel result = new CinemaModel();
            result.setID(movieModel.movieID);
            result.setHall(movieModel.movieHallNumber);
            result.setDate(movieModel.showData);
            result.setMovieName(movieModel.movieName);
            result.setMovieDirector(movieModel.movieDirector);
            result.setYear(movieModel.movieYear);
            result.setLengthInMinutes(movieModel.movieLength);
            if (movieModel.hallSeat == null) {
                Map<String, String> seats = new HashMap<>();
                for (int i = 1; i <= 20; i++) {
                    seats.put(Integer.toString(i), "-1");
                }
                result.setSeats(seats);
            } else {
                result.setSeats(movieModel.hallSeat);
            }

            HazelcastInstance client = OpenConnection();
            IMap<String, CinemaModel> cinemaModelIMap = client.getMap("cinema");

            if (cinemaModelIMap.size() == 0) {
                cinemaModelIMap.put(movieModel.movieID, result);
                HazelcastClient.shutdownAll();
                return new ResponseEntity<>(HttpStatus.OK);
            }

            int status = 0;
            String email = movieModel.movieID;
            for (String name : cinemaModelIMap.keySet()) {
                String key = name.toString();
                if (key.equals(email)) {
                    status = -1;
                    HazelcastClient.shutdownAll();
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
            }
            if (status == 0) {
                cinemaModelIMap.put(movieModel.movieID, result);
                HazelcastClient.shutdownAll();
                return new ResponseEntity<>(HttpStatus.OK);
            }
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
    @PostMapping(value = "/1.1/movie/{userID}/{movieID}/{seatsNumber}", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<CinemaModel> seatsReservation(@Valid @PathVariable String userID, @Valid @PathVariable String movieID, @Valid @PathVariable String seatsNumber, @Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key) || movieID == null || seatsNumber == null || userID == null) {
            HazelcastInstance client = OpenConnection();
            IMap<String, CinemaModel> cinemaModelIMap = client.getMap("cinema");
            for (String name : cinemaModelIMap.keySet()) {
                if (name.equals(movieID)) {
                    CinemaModel cinemaModel = cinemaModelIMap.get(name);
                    Map<String, String> reservedSeats = cinemaModel.getSeats();
                    assert seatsNumber != null;
                    if(Integer.parseInt(seatsNumber)>reservedSeats.size())
                    {
                        HazelcastClient.shutdownAll();
                        return new ResponseEntity<>(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
                    }
                    if (reservedSeats.get(seatsNumber).equals("-1")) {
                        reservedSeats.replace(seatsNumber, userID);
                        cinemaModel.setSeats(reservedSeats);
                        cinemaModelIMap.replace(movieID, cinemaModel);
                        return new ResponseEntity<>(cinemaModel, HttpStatus.OK);
                    }
                    else {
                        HazelcastClient.shutdownAll();
                        return new ResponseEntity<>(HttpStatus.CONFLICT);
                    }
                }
            }
            HazelcastClient.shutdownAll();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        HazelcastClient.shutdownAll();
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Usuwanie rezerwacji filmu.
     * @param userID Identyfikator użytkownika.
     * @param movieID Identyfikator filmu.
     * @param seatsNumber Numer siedzenia.
     * @param apiKey    Nagłówek HTTP z kodem autoryzacji.
     * @return Status kodu HTTP.
     */
    @DeleteMapping(value = "/1.1/movie/{userID}/{movieID}/{seatsNumber}", produces = "application/json", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<CinemaModel> RemoveReservation(@Valid @PathVariable String userID, @Valid @PathVariable String movieID, @Valid @PathVariable String seatsNumber, @Valid @RequestHeader("API") String apiKey) {
        if (apiKey.equals(key) || movieID == null || seatsNumber == null || userID == null) {
            HazelcastInstance client = OpenConnection();
            IMap<String, CinemaModel> cinemaModelIMap = client.getMap("cinema");

            for (String name : cinemaModelIMap.keySet()) {
                if (name.equals(movieID)) {
                    CinemaModel cinemaModel = cinemaModelIMap.get(name);
                    Map<String, String> reservedSeats = cinemaModel.getSeats();
                    assert seatsNumber != null;
                    if(Integer.parseInt(seatsNumber)>reservedSeats.size())
                    {
                        HazelcastClient.shutdownAll();
                        return new ResponseEntity<>(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
                    }
                    if (reservedSeats.get(seatsNumber).equals(userID)) {
                        reservedSeats.replace(seatsNumber, "-1");
                        cinemaModel.setSeats(reservedSeats);
                        cinemaModelIMap.replace(movieID, cinemaModel);
                        return new ResponseEntity<>(cinemaModel, HttpStatus.OK);
                    }
                    else {
                        HazelcastClient.shutdownAll();
                        return new ResponseEntity<>(HttpStatus.CONFLICT);
                    }
                }
            }
            HazelcastClient.shutdownAll();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        HazelcastClient.shutdownAll();
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /** Haszowanie danych metodą SHA-256 + sola.
     * @param data Dane do haszowania.
     * @return Zhaszowane dane
     * @throws NoSuchAlgorithmException
     */
    public String StringHash(String data) throws NoSuchAlgorithmException {
        String hashData = data + hash;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return Base64.getEncoder().encodeToString(digest.digest(hashData.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Wyszukiwanie daty w formacie timestamp w podanym zakresie.
     * @param i Wartość szukana.
     * @param starValue Początkowy zakres.
     * @param stopValue Końcowy zakres.
     * @return Status wyszukania w podanym zakresie.
     */
    public boolean timestamp(long i, long starValue, long stopValue) {
        return (i >= starValue && i <= stopValue);
    }

    /**
     * Otwiwranie połączenia.
     * @return Instancja Hazelcasta.
     */
    HazelcastInstance OpenConnection() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setClusterName("dev");
        clientConfig.getNetworkConfig().addAddress("127.0.0.1");
        return HazelcastClient.newHazelcastClient(clientConfig);
    }
}
