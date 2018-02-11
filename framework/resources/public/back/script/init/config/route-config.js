define([], function () {
        return {
            '_default': '/brandManage',
            welcome: {
                url: '/welcome',
                templateUrl: './view/welcome.html'
            },
            interfaceManage: {
                url: '/interfaceManage',
                templateUrl: './authority/interfaceManage.html',
                ctrl: 'interfaceManageCtrl',
                ctrlUrl: './authority/interfaceManage.js'
            },
            resourceManage: {
                url: '/resourceManage',
                templateUrl: './authority/resourceManage.html',
                ctrl: 'resourceManageCtrl',
                ctrlUrl: './authority/resourceManage.js'
            },
            roleManage: {
                url: '/roleManage',
                templateUrl: './authority/roleManage.html',
                ctrl: 'roleManageCtrl',
                ctrlUrl: './authority/roleManage.js'
            },
            userManage: {
                url: '/userManage',
                templateUrl: './authority/userManage.html',
                ctrl: 'userManageCtrl',
                ctrlUrl: './authority/userManage.js'
            },
            categoryManage: {
                url: '/categoryManage',
                templateUrl: './system/categoryManage.html',
                ctrl: 'categoryManageCtrl',
                ctrlUrl: './system/categoryManage.js'
            },
            brandManage: {
                url: '/brandManage',
                templateUrl: './brand/brandManage.html',
                ctrl: 'brandManageCtrl',
                ctrlUrl: './brand/brandManage.js'
            },
            brandPCenterSetting: {
                url: '/brandPCenterSetting',
                params: {
                    "code": null
                },
                templateUrl: './brand/brandPCenterSetting.html',
                ctrl: 'brandPCenterSettingCtrl',
                ctrlUrl: './brand/brandPCenterSetting.js'
            }
        }
    }
);
