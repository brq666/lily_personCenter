angular.module('starter.loading', [])
    .controller('loadingCtrl', function ($scope, $http, $location, $timeout, BrandInfo, Userinfo, UserAction) {

        $scope.imgPath = imgPath;
        $scope.brandCode = BrandInfo.getCode();

        $scope.$on("$ionicView.enter", function () {
            UserAction.did(UserAction.getActionType().ENTER_PAGE, {page: 'loading'});
        });

        BrandInfo.get(function () {
            Userinfo.get(function (userinfo) {
                if(userinfo.isInfoComplete) {
                    goWithParams('main');//如果用户资料完善,跳转到主页
                } else if(userinfo.isLogin) {
                    goWithParams('informationEdit');//如果用户资料不完善,但已注册,跳转到完善资料页
                } else {
                    goWithParams('welcome');//如果用户未注册,跳转到欢迎页
                }
            });
        });

    });
