angular.module('starter.myOrder', [])
    .controller('MyOrderCtrl', function ($scope, BrandInfo, Userinfo, $state, $ionicHistory, $http, UserAction) {

        $scope.brandInfo = null;
        $scope.userInfo = null;
        $scope.screenHeight = window.screen.height;
        $scope.allOrders = [];
        $scope.latestOrders = [];
        $scope.searchingFlag = false;

        $scope.searchCondition = {
            startTime: '',
            endTime: ''
        };

        $scope.$on("$ionicView.enter", function () {
            UserAction.did(UserAction.getActionType().ENTER_PAGE, {page: 'myOrder'});
        });

        var searchConditionFormat = {
            startTime: '',
            endTime: ''
        };

        $scope.pager = {
            pageIndex: 1,
            pageSize: 10
        };

        // 定义查询方法
        var searchMyOrder = function () {
            // 判断非空
            // 参数加载完成后，定义查询方法，这样不会报参数空指针错误
            if ($scope.userInfo == null || $scope.brandInfo == null) {
                return;
            }
            // 将searchingFlag标志置为true，表示正在查询
            $scope.searchingFlag = true;
            $http({
                method: 'POST',
                url: path + 'remoteInterface/invoke.do',
                data: {
                    url: $scope.brandInfo.config.baseRestUrl + 'user/queryOrderByUser',
                    plat_code: 'weixin',
                    page_size: $scope.pager.pageSize,
                    page_no: $scope.pager.pageIndex,
                    customerno: $scope.userInfo.openId,
                    start_time: searchConditionFormat.startTime,
                    end_time: searchConditionFormat.endTime
                }
            }).success(function (data, status, headers, config) {
                $scope.searchingFlag = false;
                // 成功时，直接将查询结果赋给latestOrders，用于判断是否有上拉刷新
                $scope.latestOrders = data;
                // 将查询结果依次拼接到allOrders
                for (var i = 0; i < data.length; i++) {
                    $scope.allOrders.push(data[i]);
                }
                // 广播加载成功,用于上拉刷新
                $scope.$broadcast('scroll.infiniteScrollComplete');
            }).error(function (data, status, headers, config) {
                console.log(data);
                $scope.searchingFlag = false;
            });
        };

        // 定义上拉刷新方法
        $scope.loadMore = function () {
            // 每次调用此方法，需将页码增加1
            $scope.pager.pageIndex++;
            searchMyOrder();
        };

        // 定义页面查询按钮点击方法
        $scope.searchOrders = function () {
            // 初始化参数变量
            $scope.allOrders = [];
            $scope.latestOrders = [];
            $scope.pager = {
                pageIndex: 1,
                pageSize: 10
            };
            searchMyOrder();
        };

        Userinfo.get(function (userInfo) {
            // 加载userInfo
            $scope.userInfo = userInfo;
            // 加载brandInfo
            BrandInfo.get(function (brandInfo) {
                $scope.brandInfo = brandInfo;
                // 页面首次查询数据
                searchMyOrder();
            });
        });

        $scope.openTimePicker = function (type) {
            weui.datePicker({
                start: '1950-01-01',
                end: '2020-12-31',
                cron: '* * *',
                defaultValue: [2018, 1, 1],
                onConfirm: function onConfirm(result) {
                    searchConditionFormat[type] = result[0].value + '-';
                    if (result[1].value < 10) {
                        searchConditionFormat[type] = searchConditionFormat[type] + '0' + result[1].value + '-';
                    } else {
                        searchConditionFormat[type] = searchConditionFormat[type] + result[1].value + '-';
                    }
                    if (result[2].value < 10) {
                        searchConditionFormat[type] = searchConditionFormat[type] + '0' + result[2].value;
                    } else {
                        searchConditionFormat[type] = searchConditionFormat[type] + result[2].value;
                    }
                    $scope.searchCondition[type] = searchConditionFormat[type];
                    $scope.$apply();
                },
                id: 'birthdayPicker'
            });
        };
    });
