angular.module('starter.informationEdit', [])
    .controller('informationEditCtrl', function ($scope, $http, $ionicHistory, Userinfo, AddressArea, UserAction) {

        $scope.imgPath = imgPath;
        $scope.inputData = {};
        $scope.userinfo = null;
        $scope.isLoading = false;

        $scope.$on("$ionicView.enter", function () {
            UserAction.did(UserAction.getActionType().ENTER_PAGE, {page: 'informationEdit'});
            Userinfo.get(function (userinfo) {
                $scope.userinfo = userinfo;
                $.extend($scope.inputData, userinfo);
            });
        });

        $scope.openSexPicker = function () {
            weui.picker([{
                label: '女',
                value: '女'
            }, {
                label: '男',
                value: '男'
            }], {
                defaultValue: $scope.inputData.sex ? [$scope.inputData.sex] : ['女'],
                onConfirm: function onConfirm(result) {
                    $scope.inputData.sex = result[0].label;
                    $scope.$apply();
                },
                id: 'jobPicker'
            });
        };

        $scope.openBirthdayPicker = function () {
            if($scope.userinfo.isInfoComplete) {
                return;
            }
            weui.datePicker({
                start: '1950-01-01',
                end: '2010-12-31',
                cron: '* * *',
                defaultValue: $scope.inputData.birthday ? $scope.inputData.birthday.split("-") : [1990, 7, 15],
                onConfirm: function onConfirm(result) {
                    var year = result[0].label.replace("年", "");
                    var month = result[1].label.replace("月", "");
                    var day = result[2].label.replace("日", "");
                    $scope.inputData.birthday = year+'-'+month+'-'+day;
                    $scope.$apply();
                },
                id: 'birthdayPicker'
            });
        };

        $scope.openJobPicker = function () {
            weui.picker([{
                label: '公务员、政府/事业机构雇员',
                value: '公务员、政府/事业机构雇员'
            }, {
                label: '专业人士（如律师/医生/老师/咨询顾问）',
                value: '专业人士（如律师/医生/老师/咨询顾问）'
            }, {
                label: '企业中高层管理人员',
                value: '企业中高层管理人员'
            }, {
                label: '企业一般职员',
                value: '企业一般职员'
            }, {
                label: '自由职业',
                value: '自由职业'
            }, {
                label: '家庭主妇',
                value: '家庭主妇'
            }, {
                label: '学生',
                value: '学生'
            }, {
                label: '其他',
                value: '其他'
            }], {
                defaultValue: $scope.inputData.job ? [$scope.inputData.job] : ['企业一般职员'],
                onConfirm: function onConfirm(result) {
                    $scope.inputData.job = result[0].label;
                    $scope.$apply();
                },
                id: 'jobPicker'
            });
        };

        // 级联选择器
        $scope.openAddressPicker = function () {
            AddressArea.get(function (addressAreas) {
                weui.picker(addressAreas, {
                    depth: 3,
                    defaultValue: $scope.inputData.province ? [$scope.inputData.province, $scope.inputData.city, $scope.inputData.district] : ['310000', '310100', '310104'],
                    onConfirm: function onConfirm(result) {
                        $scope.inputData.province = "";
                        $scope.inputData.city = "";
                        $scope.inputData.district = "";
                        var addressArea = "";
                        for(var i=0; i<result.length; i++) {
                            if(i==0) {
                                $scope.inputData.province = result[i].value;
                            } else if(i==1) {
                                $scope.inputData.city = result[i].value;
                            } else if(i==2) {
                                $scope.inputData.district = result[i].value;
                            }
                            addressArea += result[i].label+' ';
                        }
                        $scope.inputData.addressArea = addressArea.trim();
                        $scope.$apply();
                    },
                    id: 'addressPicker'
                });
            });
        };

        $scope.save = function () {
            //如果正在提交,组织再次提交
            if($scope.isLoading) {
                return;
            }
            if(!$scope.inputData.name) {
                weui.alert('如有疑问请联系客服', {title: '姓名不能为空'});
                return;
            }
            if(!$scope.inputData.sex) {
                weui.alert('如有疑问请联系客服', {title: '性别不能为空'});
                return;
            }
            if(!$scope.inputData.birthday) {
                weui.alert('如有疑问请联系客服', {title: '生日不能为空'});
                return;
            }
            $scope.isLoading = true;
            Userinfo.save(
                $scope.inputData,
                function () {
                    $scope.isLoading = false;
                    //如果信息填写完整,说明之前已经绑定过,否则就在第一次访问的绑定流程中
                    if($scope.userinfo.isInfoComplete) {
                        window.history.back();
                    } else {
                        goWithParams('loginSuccess');
                    }
                },
                function(message) {
                    $scope.isLoading = false;
                    weui.alert('如有疑问请联系客服', {title: message});
                }
            );
        };

    });
