angular.module('starter.loginSuccess', [])
    .controller('loginSuccessCtrl', function ($scope, $http, $ionicHistory, BrandInfo, UserAction) {

        $scope.imgPath = imgPath;
        $scope.brandInfo = null;

        $scope.$on("$ionicView.enter", function () {
            UserAction.did(UserAction.getActionType().ENTER_PAGE, {page: 'loginSuccess'});
        });

        BrandInfo.get(function (brandInfo) {
            $scope.brandInfo = brandInfo;
        });

        $scope.goMain = function () {
            goWithParams('main');
        };

    });
