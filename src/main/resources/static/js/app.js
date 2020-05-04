/*
 * Copyright  Michał Młodawski (SimpleMethod)(c) 2020.
 */
(function (angular) {
    'use strict';
    var app = angular.module("myApp", []);

    let api = null;

    app.controller('selectAPI', ['$scope', function ($scope) {
        $scope.data = {
            singleSelect: null
        };

        $scope.save = function () {
            api = $scope.data.singleSelect;
            console.log("%cWybrano api: "+api, "font-family:helvetica; font-size:20px; color:white;");
        }

    }]);

    app.controller('userInfo', function ($scope, $http) {
        $scope.master = {};
        $scope.get = function (user) {
            $scope.master = angular.copy(user);
            $http({
                url: 'http://localhost:8090/' + api + '/users/'+$scope.master.email,
                method: 'GET',
                contentType: 'application/json',
                headers: {
                    'API': 'abc2137'
                },
            }).then(
                function (response) {
                    $scope.userList = response.data;
                    $scope.status = response.status;
                }, function (response) {
                    status(response.status);
                }
            );
        };
    });

    app.controller('usersList', function ($scope, $http) {
        $scope.open = function () {
            $http({
                url: 'http://localhost:8090/' + api + '/users/',
                method: 'GET',
                contentType: 'application/json',
                headers: {
                    'API': 'abc2137'
                },
            }).then(
                function (response) {
                    $scope.userList = response.data;
                    status(response.status);
                }, function (response) {
                    status(response.status);
                }
            );
        };
    });

    app.controller('createUser', function ($scope, $http) {
        $scope.master = {};
        $scope.create = function (user) {
            $scope.master = angular.copy(user);
            var item = {};
            item ["email"] = $scope.master.email;
            item ["password"] = $scope.master.password;
            $http({
                url: 'http://localhost:8090/' + api + '/users/',
                data: JSON.stringify(item),
                method: 'PUT',
                contentType: 'application/json',
                headers: {
                    'API': 'abc2137'
                },
            }).then(
                function (response) {
                    $scope.status = response.status;
                    alert("Dodano użytkownika do tabeli!");
                }, function (response) {
                    status(response.status);
                }
            );
        };
    });


    app.controller('updatePassword', function ($scope, $http) {
        $scope.master = {};
        $scope.update = function (user) {
            $scope.master = angular.copy(user);
            var item = {};
            item ["email"] = $scope.master.email;
            item ["password"] = $scope.master.password;
            $http({
                url: 'http://localhost:8090/' + api + '/users/',
                data: JSON.stringify(item),
                method: 'POST',
                contentType: 'application/json',
                headers: {
                    'API': 'abc2137'
                },
            }).then(
                function (response) {
                    $scope.status = response.status;
                    alert("Hasło zostało zmienione!");
                }, function (response) {
                    status(response.status);
                }
            );
        };
    });

    app.controller('deleteUser', function ($scope, $http) {
        $scope.master = {};
        $scope.delete = function (user) {
            $scope.master = angular.copy(user);
            $http({
                url: 'http://localhost:8090/' + api + '/users/'+$scope.master.email,
                method: 'DELETE',
                contentType: 'application/json',
                headers: {
                    'API': 'abc2137'
                },
            }).then(
                function (response) {
                    $scope.status = response.status;
                    alert("Użytkownik został usunięty!");
                }, function (response) {
                    status(response.status);
                }
            );
        };
    });


    app.controller('movieInfo', function ($scope, $http) {
        $scope.master = {};
        $scope.get = function (cinema) {
            $scope.master = angular.copy(cinema);
            $http({
                url: 'http://localhost:8090/' + api + '/movie/'+$scope.master.id,
                method: 'GET',
                contentType: 'application/json',
                headers: {
                    'API': 'abc2137'
                },
            }).then(
                function (response) {
                    $scope.movieList = response.data;
                    $scope.status = response.status;
                }, function (response) {
                    status(response.status);
                }
            );
        };
    });

    app.controller('movieList', function ($scope, $http) {
        $scope.open = function () {
            $http({
                url: 'http://localhost:8090/' + api + '/movie/',
                method: 'GET',
                contentType: 'application/json',
                headers: {
                    'API': 'abc2137'
                },
            }).then(
                function (response) {
                    $scope.moviesList = response.data;
                    status(response.status);
                }, function (response) {
                    status(response.status);
                }
            );
        };
    });

    app.controller('movieUser', function ($scope, $http) {
        $scope.master = {};
        $scope.open = function (cinema) {
            $scope.master = angular.copy(cinema);
            $http({
                url: 'http://localhost:8090/' + api + '/movie/user/'+  $scope.master.email,
                method: 'GET',
                contentType: 'application/json',
                headers: {
                    'API': 'abc2137'
                },
            }).then(
                function (response) {
                    $scope.moviesList = response.data;
                    status(response.status);
                }, function (response) {
                    status(response.status);
                }
            );
        };
    });


    app.controller('movieBetweenDate', function ($scope, $http) {
        $scope.master = {};
        $scope.open = function (cinema) {
            $scope.master = angular.copy(cinema);
            var startdatee=Date.parse( $scope.master.startrange)/1000;
            var enddate=Date.parse( $scope.master.endrange)/1000;
            $http({
                url: 'http://localhost:8090/' + api + '/movie/'+startdatee+'/'+enddate,
                method: 'GET',
                contentType: 'application/json',
                headers: {
                    'API': 'abc2137'
                },
            }).then(
                function (response) {
                    $scope.moviesList = response.data;
                    $scope.status = response.status;
                }, function (response) {
                    status(response.status);
                }
            );
        };
    });


    app.controller('createMovie', function ($scope, $http) {
        $scope.master = {};
        $scope.create = function (user) {
            $scope.master = angular.copy(user);
            var item = {};
            item ["ID"] = $scope.master.id;
            item ["hallNumber"] = $scope.master.hallNumber;
            item ["date"] = Date.parse($scope.master.date)/1000;
            item ["movieName"] = $scope.master.movieName;
            item ["movieDirector"] = $scope.master.movieDirector;
            item ["year"] = $scope.master.year;
            item ["lengthInMinutes"] = $scope.master.lengthInMinutes;
            item ["seat"] =null;
            $http({
                url: 'http://localhost:8090/' + api + '/movie',
                data: JSON.stringify(item),
                method: 'PUT',
                contentType: 'application/json',
                headers: {
                    'API': 'abc2137'
                },
            }).then(
                function (response) {
                    $scope.status = response.status;
                    alert("Dodano film do tabeli!");
                }, function (response) {
                    status(response.status);
                }
            );
        };
    });

    app.controller('postReservation', function ($scope, $http) {
        $scope.master = {};
        $scope.open = function (cinema) {
            $scope.master = angular.copy(cinema);
            $http({
                url: 'http://localhost:8090/' + api + '/movie/'+$scope.master.email+'/'+$scope.master.movieid+"/"+$scope.master.seatnumber,
                method: 'POST',
                contentType: 'application/json',
                headers: {
                    'API': 'abc2137'
                },
            }).then(
                function (response) {
                    $scope.status = response.status;
                    alert("Dodano rezerwację!");
                }, function (response) {
                    status(response.status);
                }
            );
        };
    });

    app.controller('deleteReservation', function ($scope, $http) {
        $scope.master = {};
        $scope.open = function (cinema) {
            $scope.master = angular.copy(cinema);
            $http({
                url: 'http://localhost:8090/' + api + '/movie/'+$scope.master.email+'/'+$scope.master.movieid+"/"+$scope.master.seatnumber,
                method: 'DELETE',
                contentType: 'application/json',
                headers: {
                    'API': 'abc2137'
                },
            }).then(
                function (response) {
                    $scope.status = response.status;
                    alert("Anulowano rezerwację!");
                }, function (response) {
                    status(response.status);
                }
            );
        };
    });

    app.controller('TimestampCtrl', ['$scope', function($scope) {
        $scope.toTimestamp = function(date) {
            return new Date(date * 1000);
        };
    }]);
})(window.angular);

function status(number) {
    switch (number) {
        case 404:
            alert('Brak danych!');
            console.log("%cBrak danych [404]", "font-family:helvetica; font-size:50px; font-weight:bold; color:red; -webkit-text-stroke:1px white;");
            break;
        case 403:
            alert('Brak dostępu!');
            console.log("%cBrak dostępu! [403]", "font-family:helvetica; font-size:50px; font-weight:bold; color:red; -webkit-text-stroke:1px white;");
            break;
        case 409:
            alert('Konflikt danych!');
            console.log("%cKonflikt danych! [409]", "font-family:helvetica; font-size:50px; font-weight:bold; color:red; -webkit-text-stroke:1px white;");
            break;
        case 500:
            alert('Błąd serwera!');
            console.log("%cBład serwera [500]", "font-family:helvetica; font-size:50px; font-weight:bold; color:red; -webkit-text-stroke:1px white;");
            break;
        case 400:
            alert('Złe zapytanie!');
            console.log("%cZłe zapytanie! [400]", "font-family:helvetica; font-size:50px; font-weight:bold; color:red; -webkit-text-stroke:1px white;");
            break;
        case 416:
            alert('Dane spoza zakresu!');
            console.log("%cDane spoza zakresu [416]", "font-family:helvetica; font-size:50px; font-weight:bold; color:red; -webkit-text-stroke:1px white;");
            break;
    }
}