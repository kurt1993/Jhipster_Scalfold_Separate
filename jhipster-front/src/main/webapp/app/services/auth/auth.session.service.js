(function () {
    'use strict';

    angular.module('smartinvestplusApp')
           .factory('AuthServerProvider', AuthServerProvider);

    AuthServerProvider.$inject = ['$http', '$cookies'];

    function AuthServerProvider($http, $cookies) {
        var service = {
            getToken: getToken,
            hasValidToken: hasValidToken,
            login: login,
            logout: logout
        };

        return service;

        function getToken() {
            return $cookies.get('JWT');
        }

        function hasValidToken() {
            var token = this.getToken();
            return !!token;
        }

        function login(credentials) {
            var data = 'j_username=' + encodeURIComponent(credentials.username) +
                       '&j_password=' + encodeURIComponent(credentials.password);

            return $http.post('api/authenticate', data, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).success(function (response) {
                return response;
            });
        }

        function logout() {
            // logout from the server
            $http.post('api/logout').success(function (response) {
                // to get a new csrf token call the api
                $http.get('api/current-account');
                return response;
            });
        }
    }
})();
