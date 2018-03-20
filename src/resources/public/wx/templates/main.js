angular.module('starter.main', [])
    .controller('mainCtrl', function ($scope, $http, $ionicHistory, BrandInfo, Userinfo, UserAction, EnumInfo) {

        $scope.imgPath = imgPath;
        $scope.brandInfo = null;
        $scope.banners = null;
        $scope.userinfo = null;
        $scope.gradeType = {};
        $scope.clientWidth = document.body.clientWidth;

        /**
         * 加载枚举值，并拼装成映射map
         */
        // EnumInfo.get(function (enumInfo) {
        //     if (enumInfo[0].grade && enumInfo[0].grade.length > 0) {
        //         for (var i = 0; i < enumInfo[0].grade.length; i++) {
        //             var obj = enumInfo[0].grade[i];
        //             $scope.gradeType[obj.gradeType] = obj.gradeName;
        //         }
        //     }
        // });

        $scope.$on("$ionicView.enter", function () {
            UserAction.did(UserAction.getActionType().ENTER_PAGE, {page: 'main'});
        });

        $scope.getWelcomeText = function () {
            var welcomeText = "";
            var currentHour = new Date().getHours();
            if(currentHour>=4 && currentHour<11) {
                welcomeText += "早上好 ! ";
            }
            if(currentHour>=11 && currentHour<13) {
                welcomeText += "中午好 ! ";
            }
            if(currentHour>=13 && currentHour<17) {
                welcomeText += "中午好 ! ";
            }
            if(currentHour>=17 || currentHour<4) {
                welcomeText += "晚上好 ! ";
            }
            welcomeText += $scope.userinfo.name;
            return welcomeText;
        };

        $scope.goInformation = function () {
            goWithParams('information');
        };

        $scope.goCoupon = function () {
            goWithParams('couponManage');
        };

        $scope.goPoint = function () {
            goWithParams('myPoint');
        };

        $scope.goUrl = function (url) {
            var protocol = url.split("://")[0];
            if(protocol=='pc') {
                goWithParams(url.split("://")[1]);
            } else if(protocol=='tel') {
                window.location.href = "tel:" + url.split("://")[1];
            } else {
                window.location.href = url;
            }
        };

        BrandInfo.get(function (brandInfo) {
            $scope.brandInfo = brandInfo;
            // 首页可配置接口暂使用本地mock数据
            $http.get(path + 'wx/data/getResourceSet.json?setId='+brandInfo.config.bannerRsId)
            // $http.get(path + 'brand/getResourceSet.do?setId='+brandInfo.config.bannerRsId)
                .success(function (data, status, headers, config) {
                    if(data) {
                        $scope.banners = data;
                    }
                })
                .error(function (data, status, headers, config) {
                    console.log(data);
                });
        });

        Userinfo.get(function (userinfo) {
            $scope.userinfo = userinfo;
        });

    });
