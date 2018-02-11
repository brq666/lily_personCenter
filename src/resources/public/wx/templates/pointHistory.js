angular.module('starter.pointHistory', [])
    .controller('PointHistoryCtrl', function ($scope, $http, $state, $ionicHistory, BrandInfo, Userinfo, EnumInfo, UserAction) {

        $scope.searchCondition = {
            startTime: '',
            endTime: ''
        };

        var searchConditionFormat = {
            startTime: '',
            endTime: ''
        };

        $scope.imgPath = imgPath;
        $scope.brandInfo = null;
        $scope.userInfo = null;
        $scope.enumInfoMap = {
            changeType: {},
            grade: {},
            pointType: {}
        };
        $scope.screenHeight = window.screen.height;

        // 所有积分记录
        $scope.allPoints = [];
        // 最近一次搜索的积分记录
        $scope.latestPoints = [];
        // 分页对象
        $scope.pager = {
            pageIndex: 1,
            pageSize: 10
        };
        $scope.searchingFlag = false;

        $scope.$on("$ionicView.enter", function () {
            UserAction.did(UserAction.getActionType().ENTER_PAGE, {page: 'pointHistory'});
        });

        /**
         * 加载枚举值，并拼装成映射map
         */
        EnumInfo.get(function (enumInfo) {
            if (enumInfo[0].changeType && enumInfo[0].changeType.length > 0) {
                for (var i = 0; i < enumInfo[0].changeType.length; i++) {
                    var obj = enumInfo[0].changeType[i];
                    $scope.enumInfoMap.changeType[obj.changeType] = obj.changeName;
                }
            }
            if (enumInfo[0].grade && enumInfo[0].grade.length > 0) {
                for (var i = 0; i < enumInfo[0].grade.length; i++) {
                    var obj = enumInfo[0].grade[i];
                    $scope.enumInfoMap.grade[obj.gradeType] = obj.gradeName;
                }
            }
            if (enumInfo[0].pointType && enumInfo[0].pointType.length > 0) {
                for (var i = 0; i < enumInfo[0].pointType.length; i++) {
                    var obj = enumInfo[0].pointType[i];
                    $scope.enumInfoMap.pointType[obj.pointType] = obj.pointName;
                }
            }
        });

        // 定义查询方法
        var searchPointHistory = function () {
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
                    url: $scope.brandInfo.config.baseRestUrl + 'uni/pointlog/get',
                    plat_code: 'weixin',
                    customerno: $scope.userInfo.openId,
                    point_type_id: $scope.brandInfo.config.pointTypeId,
                    card_type_id: $scope.brandInfo.config.cardTypeId,
                    start_time: searchConditionFormat.startTime,
                    end_time: searchConditionFormat.endTime,
                    page_size: $scope.pager.pageSize,
                    page_no: $scope.pager.pageIndex
                }
            }).success(function (data, status, headers, config) {
                $scope.searchingFlag = false;
                // 成功时，直接将查询结果赋给latestPoints，用于判断是否有上拉刷新
                $scope.latestPoints = data;
                // 将查询结果依次拼接到allPoints
                for (var i = 0; i < data.length; i++) {
                    $scope.allPoints.push(data[i]);
                }
                // 广播加载成功,用于上拉刷新
                $scope.$broadcast('scroll.infiniteScrollComplete');
            }).error(function (data, status, headers, config) {
                $scope.searchingFlag = false;
                console.log(data);
            });
        };

        // 定义上拉刷新方法
        $scope.loadMore = function () {
            // 每次调用此方法，需将页码增加1
            $scope.pager.pageIndex++;
            searchPointHistory();
        };

        // 定义页面查询按钮点击方法
        $scope.searchPoints = function () {
            // 初始化参数变量
            $scope.allPoints = [];
            $scope.latestPoints = [];
            $scope.pager = {
                pageIndex: 1,
                pageSize: 10
            };
            searchPointHistory();
        };

        Userinfo.get(function (userInfo) {
            // 加载userInfo
            $scope.userInfo = userInfo;
            // 加载brandInfo
            BrandInfo.get(function (brandInfo) {
                $scope.brandInfo = brandInfo;
                // 页面首次查询数据
                searchPointHistory();
            });
        });

        $scope.openTimePicker = function (type) {
            weui.datePicker({
                start: '1950-01-01',
                end: '2020-12-31',
                cron: '* * *',
                defaultValue: [2018, 1, 1],
                onConfirm: function onConfirm(result) {
                    // $scope.searchCondition[type] = result[0].value + ' | ' + result[1].value + ' | ' + result[2].value;
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
