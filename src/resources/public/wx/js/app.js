// Ionic Starter App

var alert, confirm;

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
angular.module('starter', ['ionic', 'starter.services', 'starter.directives',
    'starter.loading', 'starter.welcome', 'starter.login', 'starter.loginSuccess',
    'starter.main', 'starter.information', 'starter.informationEdit', 'starter.couponManage','starter.myOrder',
    'starter.webPageContainer', 'starter.myPoint', 'starter.pointHistory'])

    .config(function ($stateProvider, $urlRouterProvider, $ionicConfigProvider, $httpProvider, $ionicLoadingConfig) {
        //配置全局头部
        //$httpProvider.defaults.transformRequest = function (data) {
        //  if (data === undefined) {
        //    return data;
        //  }
        //  return $.param(data);
        //};
        //以下3行处理浏览器缓存问题
        $httpProvider.defaults.headers.common['Cache-Control'] = 'no-cache, no-store, must-revalidate';
        $httpProvider.defaults.headers.common['Pragma'] = 'no-cache';
        $httpProvider.defaults.headers.common['Expires'] = '0';
        //处理post请求服务器无法获取参数问题
        //$httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded; charset=UTF-8';
        //添加自定义拦截器,可以在http请求发起,完成,出错等事件发生时进行处理
        //$httpProvider.interceptors.push('HttpInterceptor');
        //定义loading框
        //$ionicLoadingConfig.template = '<ion-spinner icon="lines" class="spinner-gwhere"></ion-spinner>';
        //$ionicLoadingConfig.noBackdrop = true;
        //禁止侧滑后退事件
        //$ionicConfigProvider.views.swipeBackEnabled(false);
        //取消过场动画
        $ionicConfigProvider.views.transition("none");
        //返回时forward页面也缓存
        //$ionicConfigProvider.views.forwardCache(true);
        //Android将tabs固定在底部
        $ionicConfigProvider.platform.android.tabs.position('standard');
        //解决Android scroll失效问题
        $ionicConfigProvider.scrolling.jsScrolling(true);

        //配置路由
        $stateProvider
            .state('loading', {
                url: '/loading',
                templateUrl: 'templates/loading.html',
                controller: 'loadingCtrl'
            })
            .state('welcome', {
                url: '/welcome',
                templateUrl: 'templates/welcome.html',
                controller: 'welcomeCtrl'
            })
            .state('login', {
                url: '/login',
                templateUrl: 'templates/login.html',
                controller: 'loginCtrl'
            })
            .state('loginSuccess', {
                url: '/loginSuccess',
                templateUrl: 'templates/loginSuccess.html',
                controller: 'loginSuccessCtrl'
            })
            .state('main', {
                url: '/main',
                templateUrl: 'templates/main.html',
                controller: 'mainCtrl'
            })
            .state('information', {
                url: '/information',
                templateUrl: 'templates/information.html',
                controller: 'informationCtrl'
            })
            .state('informationEdit', {
                url: '/informationEdit',
                templateUrl: 'templates/informationEdit.html',
                controller: 'informationEditCtrl'
            })
            .state('couponManage', {
                url: '/couponManage',
                templateUrl: 'templates/couponManage.html',
                controller: 'CouponManageCtrl'
            })
            .state('myOrder', {
                url: '/myOrder',
                templateUrl: 'templates/myOrder.html',
                controller: 'MyOrderCtrl'
            })
            .state('webPageContainer', {
                url: '/webPageContainer',
                templateUrl: 'templates/webPageContainer.html',
                controller: 'webPageContainerCtrl',
                params: {
                    url: null
                }
            })
            .state('myPoint' , {
                url: '/myPoint',
                templateUrl: 'templates/myPoint.html',
                controller: 'MyPointCtrl'
            })
            .state('pointHistory' , {
                url: '/pointHistory',
                templateUrl: 'templates/pointHistory.html',
                controller: 'PointHistoryCtrl'
            });

    })

    .run(function ($state, $ionicPopup) {
        //$state.go('loading', {}, {});
    })

    .controller('indexCtrl', function ($scope, $http, $state, $timeout) {
        //$timeout(function () {
        //    $state.go('loading', {}, {});
        //}, 0);
    })
;
