angular.module('starter.welcome', [])
    .controller('welcomeCtrl', function ($scope, $http, $ionicHistory, BrandInfo, UserAction) {

        $scope.imgPath = imgPath;
        $scope.welcomeRs = [];

        $scope.$on("$ionicView.enter", function () {
            UserAction.did(UserAction.getActionType().ENTER_PAGE, {page: 'welcome'});
        });

        $scope.goLogin = function () {
            goWithParams('login');
        };

        BrandInfo.get(function (brandInfo) {
            $http.get(path + 'brand/getResourceSet.do?setId='+brandInfo.config.welcomeRsId)
                .success(function (data, status, headers, config) {
                    if(data) {
                        $scope.welcomeRs = data;
                    }
                })
                .error(function (data, status, headers, config) {
                    console.log(data);
                });
        });

    });
