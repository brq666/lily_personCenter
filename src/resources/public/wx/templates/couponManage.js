angular.module('starter.couponManage', [])
    .controller('CouponManageCtrl', function ($scope, BrandInfo, $http, Userinfo, UserAction) {
        $scope.imgPath = imgPath;
        $scope.brandInfo = null;
        $scope.userinfo = null;

        var allCoupons = [];
        $scope.availableCoupons = [];
        $scope.usedCoupons = [];
        $scope.expiredCoupons = [];
        var nowDate = new Date();
        $scope.searchingFlag = false;

        $scope.$on("$ionicView.enter", function () {
            UserAction.did(UserAction.getActionType().ENTER_PAGE, {page: 'couponManage'});
        });

        /**
         *  切换顶部导航
         */
        $('.weui-navbar__item').each(function (i) {
            $(this).click(function () {
                $('.weui-navbar__item').removeClass('weui-bar__item_on');
                $('.weui-navbar__item').css('color', '#000000');
                $('.weui-navbar__item').css('border-bottom', '0px');
                $(this).addClass('weui-bar__item_on');
                $(this).css('color', '#d14c4d');
                $(this).css('border-bottom', '2px #d14c4d solid');
                $('.weui-tab__panel').children().css('display', 'none');
                $('.weui-tab__panel').children().eq(i).css('display', 'block');
            })
        });

        Userinfo.get(function (userinfo) {
            $scope.userinfo = userinfo;
            BrandInfo.get(function (brandInfo) {
                $scope.brandInfo = brandInfo;
                $scope.searchingFlag = true;
                // $http({
                //     method: 'POST',
                //     url: path + 'remoteInterface/invoke.do',
                //     data: {
                //         url: $scope.brandInfo.config.baseRestUrl + 'unicoupon/getlist',
                //         plat_code: 'weixin',
                //         customerno: $scope.userinfo.openId
                //     }
                // })
                var _params=$.extend({
                    plat_code: 'weixin',
                    customerno: $scope.userinfo.openId
                },sysParams());
                $http({
                    method: 'GET',
                    url: $scope.brandInfo.config.baseRestUrl + 'unicoupon/getlist',
                    params:_params
                }).success(function (response, status, headers, config) {
                    $scope.searchingFlag = false;
                    allCoupons = response.jsondata;
                    angular.forEach(allCoupons, function (n, i) {
                        var obj = angular.copy(n);
                        var end_time = new Date(obj.end_time.substring(0, 10));
                        if (end_time < nowDate) {
                            $scope.expiredCoupons.push(n);
                        } else {
                            if (obj.couponstatus == 'SENT') {
                                $scope.availableCoupons.push(n);
                            } else if (obj.couponstatus == 'USED') {
                                $scope.usedCoupons.push(n)
                            }
                        }
                    });
                }).error(function (data, status, headers, config) {
                    console.log(data);
                    $scope.searchingFlag = false;
                });
            });
        });
    });
