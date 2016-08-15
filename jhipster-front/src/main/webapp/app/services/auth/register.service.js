(function () {
    'use strict';

    angular
        .module('smartinvestplusApp')
        .factory('Register', Register);

    Register.$inject = ['$resource'];

    function Register ($resource) {
        return $resource('api/register', {}, {});
    }
})();
