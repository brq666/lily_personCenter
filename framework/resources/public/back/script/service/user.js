define([
    'lib/cryptojslib/components/mode-ecb'
], function () {
    module.service('userService', function ($rootScope, $http, $state) {
        this.getMenus = function (callback) {
            $http.get("../resource/getMenuResources.do").success(function (data) {
                if (data instanceof Array) {
                    callback(data);
                }
            });
        };
        this.user = JSON.parse(localStorage.getItem("user"));
    })
});