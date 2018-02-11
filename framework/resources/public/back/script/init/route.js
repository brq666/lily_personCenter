define($module, function (route) {
    angular.module('app.route', ['oc.lazyLoad', 'ui.router'])
        .config(function ($stateProvider, $urlRouterProvider) {

            angular.forEach(route, function (value, key) {
                if ('_default' === key) {
                    $urlRouterProvider.otherwise(value);
                    return;
                }
                $stateProvider.state(key, {
                    url: value.url,
                    params: value.params,
                    views: {
                        'container': {
                            templateUrl: value.templateUrl,
                            controller: value.ctrl
                        }
                    },
                    resolve: value.ctrlUrl ? {
                        loadMyCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                            return $ocLazyLoad.load(value.ctrlUrl);
                        }]
                    } : null
                });
            });
        });
});