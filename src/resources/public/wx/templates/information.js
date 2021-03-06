angular.module('starter.information', [])
    .controller('informationCtrl', function ($scope, $http, $ionicHistory, BrandInfo, Userinfo, UserAction, EnumInfo) {

        $scope.imgPath = imgPath;
        $scope.brandInfo = null;
        $scope.userinfo = null;
        $scope.gradeType = {};
        $scope.clientWidth = document.body.clientWidth;

        /**
         * 加载枚举值，并拼装成映射map
         */
        EnumInfo.get(function (enumInfo) {
            if (enumInfo[0].grade && enumInfo[0].grade.length > 0) {
                for (var i = 0; i < enumInfo[0].grade.length; i++) {
                    var obj = enumInfo[0].grade[i];
                    $scope.gradeType[obj.gradeType] = obj.gradeName;
                }
            }
        });

        $scope.$on("$ionicView.enter", function () {
            UserAction.did(UserAction.getActionType().ENTER_PAGE, {page: 'information'});
        });

        $scope.goEdit = function () {
            goWithParams('informationEdit');
        };

        BrandInfo.get(function (brandInfo) {
            $scope.brandInfo = brandInfo;
        });

        Userinfo.get(function (userinfo) {
            $scope.userinfo = userinfo;
        });

    });
