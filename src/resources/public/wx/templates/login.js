angular.module('starter.login', [])
    .controller('loginCtrl', function ($scope, $http, $ionicHistory, $timeout, BrandInfo, Userinfo, UserAction) {

        $scope.imgPath = imgPath;
        $scope.brandInfo = null;
        $scope.userinfo = null;
        $scope.vcodeBtnDisabled = false;
        $scope.vcodeBtnText = '获取验证码';
        $scope.validateCodeInfo = {};
        $scope.isLoading = false;
        $scope.inputData = {
            mobile: '',
            validateCode: ''
        };
        $scope.validator = {
            mobileErrMsg: '',
            validateCodeErrMsg: '',
            validateMobile: function () {
                while($scope.inputData.mobile.indexOf(' ')>=0) {
                    $scope.inputData.mobile = $scope.inputData.mobile.replace(' ', '');
                }
                if(!(/^1\d{10}$/.test($scope.inputData.mobile))) {
                    $scope.validator.mobileErrMsg = '请输入正确的手机号';
                    return false;
                } else {
                    $scope.validator.mobileErrMsg = '';
                    return true;
                }
            },
            validateValidateCode: function () {
                if($scope.inputData.mobile!=$scope.validateCodeInfo.mobile
                    || $scope.inputData.validateCode!=$scope.validateCodeInfo.validateCode) {
                    $scope.validator.validateCodeErrMsg = '验证码不正确';
                    return false;
                } else {
                    $scope.validator.validateCodeErrMsg = '';
                    return true;
                }
            }
        };

        $scope.$on("$ionicView.enter", function () {
            UserAction.did(UserAction.getActionType().ENTER_PAGE, {page: 'login'});
        });

        var refreshVcodeBtnText = function (wait) {
            if(wait<=0) {
                $scope.vcodeBtnDisabled = false;
                $scope.vcodeBtnText = '重新发送';
            } else {
                $scope.vcodeBtnDisabled = true;
                $scope.vcodeBtnText = '重新发送('+wait+')';
                $timeout(function () {
                    refreshVcodeBtnText(wait-1);
                }, 1000);
            }
        };

        //获取验证码
        $scope.getValidateCode = function () {
            //未拿到品牌信息不能发送
            if(!$scope.brandInfo) {
                return;
            }
            //不能连续发送
            if($scope.vcodeBtnDisabled) {
                return;
            }
            //校验手机号格式
            if(!$scope.validator.validateMobile()) {
                return;
            }
            //判断手机号是否已被绑定
            $http.post(path + 'remoteInterface/invoke.do',
                {
                    url: $scope.brandInfo.config.baseRestUrl + 'wxuser/queryUserByMobile',
                    mobile: $scope.inputData.mobile
                })
                .success(function (data, status, headers, config) {
                    if(data) {
                        //等待30秒才允许再次发送
                        refreshVcodeBtnText(30);
                        //发送验证码
                        $http.post(path + 'remoteInterface/invoke.do',
                            {
                                url: $scope.brandInfo.config.baseRestUrl + 'user/sendValidateCode',
                                mobile: $scope.inputData.mobile
                            })
                            .success(function (data, status, headers, config) {
                                if(data) {
                                    $scope.validateCodeInfo = {
                                        mobile: $scope.inputData.mobile,
                                        validateCode: data.validateCode,
                                        expireTime: new Date().getTime()+30*60*1000
                                    };
                                }
                            })
                            .error(function (data, status, headers, config) {
                                console.log(data);
                            });
                    } else {
                        weui.alert('如有疑问请联系客服', {title: '该手机号已被绑定!'});
                    }
                })
                .error(function (data, status, headers, config) {
                    console.log(data);
                });
        };

        //注册
        $scope.login = function () {
            //未拿到品牌信息不能提交
            if(!$scope.brandInfo) {
                return;
            }
            //如果正在提交,组织再次提交
            if($scope.isLoading) {
                return;
            }
            //校验验证码
            if(!$scope.validator.validateMobile() || !$scope.validator.validateValidateCode()) {
                return;
            }
            $scope.isLoading = true;
            $http.post(path + 'remoteInterface/invoke.do',
                {
                    url: $scope.brandInfo.config.baseRestUrl + 'user/register',
                    openId: $scope.userinfo.wxInfo.openid,
                    validateCode: $scope.inputData.validateCode,
                    mobile: $scope.inputData.mobile
                })
                .success(function (data, status, headers, config) {
                    $scope.isLoading = false;
                    if(data) {
                        Userinfo.reload(function () {
                            goWithParams('informationEdit');
                        });
                    }
                })
                .error(function (data, status, headers, config) {
                    $scope.isLoading = false;
                    console.log(data);
                });
        };

        BrandInfo.get(function (brandInfo) {
            $scope.brandInfo = brandInfo;
        });

        Userinfo.get(function (userinfo) {
            $scope.userinfo = userinfo;
        });

    });
