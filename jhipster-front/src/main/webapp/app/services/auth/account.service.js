(function () {
    'use strict';

    angular.module('smartinvestplusApp')
           .factory('Account', Account);

    Account.$inject = ['$resource'];

    function Account($resource) {
        return $resource('api/current-account', {}, {
            'get': {
                method: 'GET',
                params: {},
                isArray: false,
                interceptor: {
                    response: function (response) {
                        // expose response
                        return response;
                    }
                }
            }
        });
    }
})();
