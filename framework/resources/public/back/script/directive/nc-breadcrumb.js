/**
 * breadcrumd
 *
 * Created by Virushi on 2017/5/10.
 */
define([], function () {
    module.addRequires([]);
    module.directive('ncBreadcrumb', function() {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                menus: '=menus'
            },
            template:
                "<div class=\"my-breadcrumb\" ng-show=\"showFlag\">" +
                "   <ul>" +
                "       <li ng-repeat=\"item in items\">" +
                "           <a href=\"{{item.router}}\" ng-click=\"clickItem(item, $index)\">{{item.name}}</a>" +
                "       </li>" +
                "   </ul>" +
                "</div>",
            controller: function ($rootScope, $scope, $timeout) {
                //是否显示面包屑的标记
                $scope.showFlag = true;
                $scope.items = localStorage.getItem("breadcrumbResources") == null ? [] : JSON.parse(localStorage.getItem("breadcrumbResources"));
                $scope.menuResources = [];
                $scope.menuResources1 = [];

                $scope.$watch('menus', function (newVal, oldVal) {
                    if (newVal && newVal.length > 0) {
                        getMenuRouter($scope.menus);
                        getMenuRouter1($scope.menus);
                    }

                });

                $rootScope.$on("$stateChangeSuccess", function(event, toState, toParams, fromState, fromParams) {
                    //非welcome页，显示面包屑导航
                    if (toState.name.indexOf('welcome') == -1) {
                        setItems(toState.name);
                        localStorage.setItem("breadcrumbResources", JSON.stringify($scope.items));
                        $scope.showFlag = true;
                    } else {
                        $scope.items = [];
                        localStorage.removeItem("breadcrumbResources");
                        $scope.showFlag = false;
                    }
                });

                function getMenuRouter(menus) {
                    angular.forEach(menus, function(data, index, array) {
                        $scope.menuResources.push(data);
                        if (data.children && data.children.length > 0) {
                            getMenuRouter(data.children);
                        }
                    });
                }

                function getMenuRouter1(menus) {
                    angular.forEach(menus, function(data, index, array) {
                        $scope.menuResources1.push(data.routerPath);
                    });
                }

                function getRouterName(router) {
                    var name = '';
                    angular.forEach($scope.menuResources, function(data, index, array) {
                        if (data.routerPath == router) {
                            name= data.name;
                        }
                    });
                    return name;
                }

                function setItems(router) {
                    $scope.router = '#'+ router;
                    var item = {name: getRouterName(router), router: $scope.router};
                    if (!$scope.items.some(hasItem) && $scope.menuResources1.indexOf(router) == -1) {
                        $scope.items.push(item);
                    } else if ($scope.items.some(hasItem)) {
                        angular.forEach($scope.items, function(data, index, array) {
                            if (array[array.length - 1].router != $scope.router) {
                                $scope.items.pop();
                            } else {
                                return false;
                            }
                        })
                    } else {
                        $scope.items = [];
                        checkItem($scope.menus, router);
                    }
                }

                function hasItem(element, index, array) {
                    return element.router == $scope.router;
                }

                function checkItem(datas, router) {
                    angular.forEach(datas, function(data, index, array){
                        if (data.routerPath == router) {
                            $scope.items.push({name: data.name, router: '#' + data.routerPath});
                            return false;
                        } else {
                            if (data.children && data.children.length > 0) {
                                checkItem(data.children, router);
                            }
                        }
                    })
                }
            }
        }
    })
});

