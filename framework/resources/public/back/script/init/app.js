define(['bootbox'], function (bootbox) {
    angular.module('app', ['oc.lazyLoad', 'ngCookies', 'app.route', 'app.component'])
        .factory('systemInterceptor', function ($q, $rootScope) {
            var requestLimit = 0;

            function requestStart() {
                requestLimit++;
                $rootScope.shadeDisplay = true;
            }

            function requestEnd() {
                requestLimit--;
                if (requestLimit <= 0) {
                    $rootScope.shadeDisplay = false;
                }
            }

            return {
                'request': function (config) {
                    requestStart();
                    return config;
                },
                'requestError': function (rejection) {
                    requestEnd();
                    return $q.reject(rejection);
                },
                'response': function (response) {
                    requestEnd();
                    return response;
                },
                'responseError': function (rejection) {
                    requestEnd();
                    if (rejection.status == 401) {
                        if (!rejection.config.disableMsg) {
                            window.location = './login.html';
                            return rejection;
                        } else {
                            return $q.reject(rejection);
                        }
                    } else if (rejection.status == 404 && !rejection.config.disableMsg) {
                        alert("请求地址不存在!");
                        return rejection;
                    } else if (rejection.status == -1 && !rejection.config.disableMsg) {
                        alert("请求异常!");
                        return $q.reject(  );
                    } else if (rejection.headers("ERROR_CODE") && !rejection.config.disableMsg) {
                        alert("系统异常:<span style='color: red'>" + decodeURIComponent(rejection.headers("ERROR_MSG")) + "</span><br>异常代码:<span style='color: red'>" + rejection.headers("ERROR_CODE") + "</span>");
                        return $q.reject(rejection)
                    } else if (!rejection.config.disableMsg) {
                        alert("服务器处理请求异常!");
                        return $q.reject(rejection);
                    }
                }
            };
        })
        .config(function ($httpProvider) {
            $httpProvider.interceptors.push('systemInterceptor');
        })
        .controller('appCtrl', function ($rootScope, $scope, $state, $http, $timeout, $sce, userService, $stateParams) {

            $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
                var params = toParams.params == null ? JSON.parse(localStorage.getItem('params')) : toParams.params;
                localStorage.setItem('params', JSON.stringify(params));
                toParams.params = params;
            });

            $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
                $("#sidrMenu").find("a").removeClass("selected");
                var menuSelected = $("#sidrMenu").find('a[href="#' + toState.name + '"]');
                if (menuSelected.length == 0) {
                    setTimeout(function () {
                        $("#sidrMenu").find('a[href="#' + toState.name + '"]').addClass("selected");
                    }, 1000);
                } else {
                    menuSelected.addClass("selected");
                }
                //非floorDesign页，显示header
                $timeout(function() {
                    var containerWrap_margin_top = 120;
                    if (toState.name.indexOf('welcome') != -1) {
                        containerWrap_margin_top = 80;
                    }
                    if (toState.name.indexOf('floorDesign') == -1) {
                        $(".header-bar").css("top", "0");
                        $(".my-breadcrumb ").css("top", "80px");
                        $("#sidrMenu > .sidr").css("top", "80px");
                        $("#containerWrap").css("margin-top", containerWrap_margin_top + "px");
                        $("#containerWrap").css("top", containerWrap_margin_top + "px");
                    } else {
                        $(".header-bar").css("top", "-80px");
                        $(".my-breadcrumb ").css("top", "0");
                        $("#sidrMenu > .sidr").css("top", "0");
                        $("#containerWrap").css("margin-top", containerWrap_margin_top - 80 + "px");
                        $("#containerWrap").css("top", containerWrap_margin_top - 80 + "px");
                    }
                    //滚动条回到顶部
                    document.getElementsByTagName('body')[0].scrollTop = 0;
                },200)
            });
            $rootScope.$on('$stateChangeError', function () {
                $rootScope.lastHash = window.location.hash;
                $state.go('login');
            });

            userService.getMenus(function(menus) {
                $scope.menus = menus
            });

            $scope.breadcrumbResources = [
                {name: "品牌管理", routerPath: "brandManage"},
                {name: "品牌名微信个人中心配置", routerPath: "brandPCenterSetting"},
                {name: "接口管理", routerPath: "interfaceManage"},
                {name: "资源管理", routerPath: "resourceManage"},
                {name: "角色管理", routerPath: "roleManage"},
                {name: "用户管理", routerPath: "userManage"},
                {name: "大小类管理", routerPath: "categoryManage"}
            ];

            // 获取用户
            $scope.user = userService.user;

            /* 自定义alert
            * options:
            *   String: 即为提示语
            *   Object:
            *       title: 标题
            *       message: 内容
            *       type: 类型（默认：default | 成功/提交：success | 失败和警告：error）
            *       okLabel: 按钮文字
            * callback: 回调
            * */
            window.alert = function(options, callback) {
                var flag = typeof options == "object";
                bootbox.alert({
                    title:
                    "<div>" +
                    "   <img src='./img/sys/alert-" + (flag ? (options.type ? options.type : "default") : "default") + ".png'>" +
                    "   <br/>" +
                    "   <span>" + (flag ? (options.title ? options.title : options) :options) + "</span>" +
                    "</div>",
                    message: flag ? (options.message ? options.message : '<span></span>') : '<span></span>',
                    size: 'small',
                    buttons: {
                        ok: {
                            label: flag ? (options.okLabel ? options.okLabel : '确定') : '确定',
                            className: 'btn'
                        }
                    },
                    callback: function () {
                        if(callback){
                            $scope.$apply(function(){
                                callback();
                            })
                        }
                    }
                });
            };

            /* 自定义confirm
            * options:
            *   String: 即为提示语
            *   Object:
            *       title: 标题
            *       message: 内容
            *       type: 确定按钮样式（默认：primary | 警告：warning）
            *       confirmLabel: 确认按钮文字
            *       cancelLabel: 取消按钮文字
            * confirm_callback: 确认回调
            * cancel_callback: 取消回调
            * */
            window.confirm = function(options, confirm_callback, cancel_callback) {
                var flag = typeof options == "object";
                bootbox.confirm({
                    title: flag ? options.title : options,
                    message: flag ? (options.message ? options.message : '<span></span>') : '<span></span>',
                    buttons: {
                        confirm: {
                            label: flag ? (options.confirmLabel ? options.confirmLabel : '确定') : '确定',
                            className: 'btn btn-primary ' + (flag ? (options.type ? 'btn-' + options.type : '') : '')
                        },
                        cancel: {
                            label: flag ? (options.cancelLabel ? options.cancelLabel : '取消') : '取消',
                            className: 'btn'
                        }
                    },
                    size: 'small',
                    callback: function (result) {
                        if (result) {
                            if (confirm_callback) {
                                $scope.$apply(function(){
                                    confirm_callback();
                                })
                            }
                        } else {
                            if (cancel_callback) {
                                $scope.$apply(function(){
                                    cancel_callback();
                                })
                            }
                        }
                    }
                });
            };
        });
});