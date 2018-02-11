angular.module('starter.services', [])

    .factory('HttpInterceptor', ["$injector", function ($injector) {
        var _showLoading = true;
        var showLoading = function (showLoading) {
            _showLoading = showLoading;
        };
        return {
            'showLoading': showLoading,
            'request': function (config) {
                if (_showLoading) {
                    $injector.get('$ionicLoading').show();
                }
                return config;
            },
            'response': function (response) {
                if (_showLoading) {
                    $injector.get('$ionicLoading').hide();
                }
                return response;
            },
            'requestError': function (rejection) {
                if (_showLoading) {
                    $injector.get('$ionicLoading').hide();
                }
                return rejection;
            },
            'responseError': function (rejection) {
                if (_showLoading) {
                    $injector.get('$ionicLoading').hide();
                }
                return rejection;
            }
        };
    }])

    .service("platformUtil", function () {
        var ua = window.navigator.userAgent.toLowerCase();
        this.getPlatform = function () {
            if (ua.indexOf('android') != -1) {
                return "android";
            }
            if (ua.indexOf('iphone') != -1 || ua.indexOf('ipad') != -1) {
                return "ios";
            }
            if (ua.indexOf("Window NT")) {
                return "windows";
            }
            return null;
        };
        this.isIos = function () {
            return this.getPlatform() == "ios";
        };
        this.isAndroid = function () {
            return this.getPlatform() == "android";
        };
        this.isWeChat = function () {
            return ua.indexOf('micromessenger') != -1;
        }
    })

    .factory('Clock', ["$http", "$interval", function ($http, $interval) {
        var _timeDiffWithServer = 0;
        var setTime = function () {
            $http.get(path + 'sys/p/getTime.do', [])
                .success(function (data, status, headers, config) {
                    _timeDiffWithServer = data - new Date().getTime();
                })
                .error(function (data, status, headers, config) {
                    console.log(data);
                });
        };
        setTime();
        $interval(setTime, 10*60*1000);
        return {
            getTime: function (userId, userIdType) {
                return new Date(new Date().getTime() + _timeDiffWithServer);
            }
        };
    }])

    .factory('UserAction', ["$http", "$interval", "Clock", function ($http, $interval, Clock) {
        var _userId;
        var _userIdType;
        var _actionType = {
            ENTER_PAGE: "ENTER_PAGE",
            CLICK: "CLICK"
        };
        var _userActions = JSON.parse(localStorage.getItem("userActions"));
        if(!_userActions) {
            _userActions = [];
        }
        var sentUserAction = function () {
            if(_userActions.length>0 && _userId) {
                var actions = _userActions.splice(0, _userActions.length);
                localStorage.setItem("userActions", JSON.stringify(_userActions));
                $http.post(path + 'ac/saveUserAction.do', actions)
                    .success(function (data, status, headers, config) {

                    })
                    .error(function (data, status, headers, config) {
                        _userActions = actions.concat(_userActions);
                        localStorage.setItem("userActions", JSON.stringify(_userActions));
                    });
            }
        };
        //每十秒提交一次
        $interval(sentUserAction, 10*1000);
        return {
            setUser: function (userId, userIdType) {
                if(!userId || !userIdType){
                    return;
                }
                _userId = userId;
                _userIdType = userIdType;
                //把之前没有userId时记录的用户行为补上userId信息
                for(var i=0; i<_userActions.length; i++) {
                    if(!_userActions[i].userId) {
                        _userActions[i].userId = _userId;
                        _userActions[i].userIdType = _userIdType;
                    }
                }
            },
            did: function (type, content) {
                var action = {};
                action.userId = _userId;
                action.userIdType = _userIdType;
                action.actionSrc = "wx_pc";
                action.actionTime = Clock.getTime();
                action.actionType = type;
                action.actionContent = JSON.stringify(content);
                _userActions.push(action);
                localStorage.setItem("userActions", JSON.stringify(_userActions));
            },
            getActionType: function () {
                return _actionType;
            }
        };
    }])

    .factory('BrandInfo', ["$http", "$location", function ($http, $location) {
        var _brandInfo;
        var _fns = [];
        var _brandCode = getURLParams($location.url()).brandCode;
        if(_brandCode) {
            $http.get(path + 'brand/getBrandConfig.do?code='+_brandCode)
                .success(function (data, status, headers, config) {
                    if(data) {
                        _brandInfo = data;
                        $('body').append(
                            '<style type="text/css">' +
                            '    .btn_brand {' +
                            '        background: ' + _brandInfo.config.secondaryColor + ';' +
                            '    }' +
                            '    .weui-vcode-btn {' +
                            '        color: ' + _brandInfo.config.secondaryColor + ';' +
                            '    }' +
                            '    .bkg_brand {' +
                            '        background: ' + _brandInfo.config.majorColor + ';' +
                            '    }' +
                            '    .brand-menu {' +
                            '        color: ' + (_brandInfo.config.isInverseMenu=="1" ? '#ffffff' : _brandInfo.config.secondaryColor) + ';' +
                            '        background: ' + (_brandInfo.config.isInverseMenu=="1" ? _brandInfo.config.majorColor : '#ffffff') + ';' +
                            '    }' +
                            '    .brand-header {' +
                            '        color: ' + (_brandInfo.config.isInverseHeader=="1" ? '#ffffff' : _brandInfo.config.secondaryColor) + ';' +
                            '        background: ' + (_brandInfo.config.isInverseHeader=="1" ? _brandInfo.config.majorColor : '#ffffff') + ';' +
                            '    }' +
                            '    .card-text {' +
                            '        color: ' + _brandInfo.config.cardTextColor + ';' +
                            '    }' +
                            '</style>');
                        angular.forEach(_fns, function (fn) {
                            fn(_brandInfo);
                        });
                        _fns = [];
                    }
                })
                .error(function (data, status, headers, config) {
                    console.log(data);
                });
        }
        return {
            getCode: function () {
                return _brandCode;
            },
            get: function (fn) {
                if (typeof _brandInfo === 'undefined') {
                    if(fn) {
                        _fns.push(fn);
                    }
                } else {
                    if(fn) {
                        fn(_brandInfo);
                    }
                }
            }
        };
    }])

    .factory('EnumInfo', ["$http", 'BrandInfo', function ($http, BrandInfo) {
        var _brandInfo;
        var _enumInfo;
        var _fns = [];
        BrandInfo.get(function (brandInfo) {
            _brandInfo = brandInfo;
            var _url = _brandInfo.config.baseRestUrl + 'uni/card/get';
            $http.post(path + 'remoteInterface/invoke.do',
                {
                    url: _url
                })
                .success(function (data, status, headers, config) {
                    if (data) {
                        _enumInfo = data;
                        angular.forEach(_fns, function (fn) {
                            fn(_enumInfo);
                        });
                        _fns = [];
                    }
                })
                .error(function (data, status, headers, config) {
                    console.log(data);
                });
        });
        return {
            get: function (fn) {
                if (typeof _enumInfo === 'undefined') {
                    if (fn) {
                        _fns.push(fn);
                    }
                } else {
                    if (fn) {
                        fn(_enumInfo);
                    }
                }
            }
        };
    }])

    .factory('AddressArea', ["$http", "$location", "$timeout", function ($http) {
        var _addressArea;
        var _fns = [];
        var maxDeep = function (areas) {
            if(!areas || areas.length==0) {
                return 0;
            }
            var max = 10;
            for(var i=0; i<areas.length; i++) {
                var area = areas[i];
                var t = maxDeep(area.children);
                if(t>max) {
                    max = t;
                }
            }
            return max+1;
        };
        $http.get(path + 'wx/data/address-area.json')
            .success(function (data, status, headers, config) {
                _addressArea = data;
                angular.forEach(_fns, function (fn) {
                    fn(_addressArea);
                });
                _fns = [];
            })
            .error(function (data, status, headers, config) {
                console.log(data);
            });
        return {
            get: function (fn) {
                if (typeof _addressArea === 'undefined') {
                    if(fn) {
                        _fns.push(fn);
                    }
                } else {
                    if(fn) {
                        fn(_addressArea);
                    }
                }
            }
        };
    }])

    .factory('Userinfo', ['$http', '$timeout', '$location', 'BrandInfo', 'AddressArea', 'UserAction', function ($http, $timeout, $location, BrandInfo, AddressArea, UserAction) {
        var _userinfo;
        var _wxUserinfo;
        var _brandInfo;
        var _fns = [];
        //获取优惠券信息
        var loadCouponInfo = function (success, error) {
            $http.post(path + 'remoteInterface/invoke.do',
                {
                    url: _brandInfo.config.baseRestUrl + 'unicoupon/getlist',
                    plat_code: 'weixin',
                    customerno: _userinfo.openId,
                    coupon_status: 'SENT'
                })
                .success(function (data, status, headers, config) {
                    if (data) {
                        _userinfo.coupons = data;
                        _userinfo.couponNum = 0;
                        var currentTime = new Date().format("yyyy-MM-dd");
                        for (var i = 0; i < _userinfo.coupons.length; i++) {
                            if (_userinfo.coupons[i].end_time.substr(0, 10) >= currentTime) {
                                _userinfo.couponNum++;
                            }
                        }
                    }
                    if (success) {
                        success(data);
                    }
                })
                .error(function (data, status, headers, config) {
                    if (error) {
                        error(data);
                    }
                });
        };
        //获取卡及积分信息
        var loadCardInfo = function (success, error) {
            $http.post(path + 'remoteInterface/invoke.do',
                {
                    url: _brandInfo.config.baseRestUrl + 'uni/pointInfo/get',
                    plat_code: 'weixin',
                    customerno: _userinfo.openId,
                    point_type_id: _brandInfo.config.pointTypeId,
                    card_type_id: _brandInfo.config.cardTypeId
                })
                .success(function (data, status, headers, config) {
                    if (data) {
                        _userinfo.card = data[0];
                        $http.post(path + 'remoteInterface/invoke.do',
                            {
                                url: _brandInfo.config.baseRestUrl + 'user/queryInvalidPointByOpenId',
                                plat_code: 'weixin',
                                customerno: _userinfo.openId
                            })
                            .success(function (data, status, headers, config) {
                                if (data) {
                                    $.extend(_userinfo.card, data);
                                    if (success) {
                                        success(data);
                                    }
                                }
                            })
                            .error(function (data, status, headers, config) {
                                if (error) {
                                    error(data);
                                }
                            });
                    }
                })
                .error(function (data, status, headers, config) {
                    if (error) {
                        error(data);
                    }
                });
        };
        //处理地址信息,解析addressArea
        var handleAddress = function (success) {
            AddressArea.get(function (addressAreas) {
                var i;
                var area;
                var areas = addressAreas;
                var addressArea = "";
                if(_userinfo.province && areas) {
                    area = null;
                    for(i=0; i<areas.length; i++) {
                        if(areas[i].value==_userinfo.province) {
                            area = areas[i];
                            break;
                        }
                    }
                    if(area) {
                        addressArea += area.label + " ";
                        areas = area.children;
                    } else {
                        areas = null;
                    }
                }
                if(_userinfo.city && areas) {
                    area = null;
                    for(i=0; i<areas.length; i++) {
                        if(areas[i].value==_userinfo.city) {
                            area = areas[i];
                            break;
                        }
                    }
                    if(area) {
                        addressArea += area.label + " ";
                        areas = area.children;
                    } else {
                        areas = null;
                    }
                }
                if(_userinfo.district && areas) {
                    area = null;
                    for(i=0; i<areas.length; i++) {
                        if(areas[i].value==_userinfo.district) {
                            area = areas[i];
                            break;
                        }
                    }
                    if(area) {
                        addressArea += area.label + " ";
                        areas = area.children;
                    } else {
                        areas = null;
                    }
                }
                _userinfo.addressArea = addressArea.trim();
                if (success) {
                    success();
                }
            });
            //if(!_userinfo.address) {
            //    if(success) {
            //        success();
            //    }
            //}
            //AddressArea.get(function (addressAreas) {
            //    var addressArea = '';
            //    var addressDetail = _userinfo.address;
            //    var areas = addressAreas;
            //    while(areas && areas.length>0) {
            //        var area = null;
            //        for(var i=0; i<areas.length; i++) {
            //            //如果如果一个区域只有一个子区域,用户可能直接填写子区域名称
            //            if(areas[i].children && areas[i].children.length==1) {
            //                //判断是否能够完整匹配,如果匹配上了,只取第一个
            //                if(addressDetail.indexOf(areas[i].label+areas[i].children[0].label)==0) {
            //                    area = areas[i];
            //                    break;
            //                }
            //                //否则,先匹配较长的再匹配较短的,如果一样长先匹配下级再匹配上级,如果有一个匹配上了,自动补齐另一部分
            //                else {
            //                    var childrenFirst = areas[i].children[0].label.length>=areas[i].label.length;
            //                    if(childrenFirst) {
            //                        if(addressDetail.indexOf(areas[i].children[0].label)==0) {
            //                            area = areas[i];
            //                            addressDetail = area.label+addressDetail;
            //                            break;
            //                        } else if(addressDetail.indexOf(areas[i].label)==0) {
            //                            area = areas[i];
            //                            break;
            //                        }
            //                    } else {
            //                        if (addressDetail.indexOf(areas[i].label) == 0) {
            //                            area = areas[i];
            //                            break;
            //                        } else if(addressDetail.indexOf(areas[i].children[0].label)==0) {
            //                            area = areas[i];
            //                            addressDetail = area.label+addressDetail;
            //                            break;
            //                        }
            //                    }
            //                }
            //            }
            //            //否则,普通情况只判断当前areas[i]
            //            else if(addressDetail.indexOf(areas[i].label)==0) {
            //                area = areas[i];
            //                break;
            //            }
            //        }
            //        if(area) {
            //            addressArea += area.label + ' ';
            //            addressDetail = addressDetail.substr(area.label.length);
            //            areas = area.children;
            //        } else {
            //            areas = null;
            //        }
            //    }
            //    _userinfo.addressArea = addressArea.trim();
            //    _userinfo.addressDetail = addressDetail;
            //    if(success) {
            //        success();
            //    }
            //});
        };
        //获取用户基本信息
        var loadUserInfo = function (success, error) {
            $http.post(path + 'remoteInterface/invoke.do',
                {
                    url: _brandInfo.config.baseRestUrl + 'wxuser/queryUserByOpenId',
                    openId: _wxUserinfo.openid
                })
                .success(function (data, status, headers, config) {
                    if(data) {
                        if(typeof _userinfo === 'undefined') {
                            _userinfo = data[0];
                            _userinfo.wxInfo = _wxUserinfo;
                        } else {
                            $.extend(_userinfo, data[0]);
                        }
                    } else {
                        _userinfo = {name: _wxUserinfo.nickname};
                        if(_wxUserinfo.sex=="1") {
                            _userinfo.sex = "男";
                        } else if(_wxUserinfo.sex=="2") {
                            _userinfo.sex = "女";
                        }
                        _userinfo.wxInfo = _wxUserinfo;
                    }
                    //判断用户是否登录,资料是否填写完整
                    _userinfo.isLogin = false;
                    _userinfo.isInfoComplete = false;
                    if(_userinfo.mobile) {
                        _userinfo.isLogin = true;
                        if(_userinfo.name && _userinfo.sex && _userinfo.birthday) {
                            _userinfo.isInfoComplete = true;
                        }
                    }
                    //处理地址信息
                    handleAddress(function () {
                        if (success) {
                            success(data);
                        }
                    });
                })
                .error(function (data, status, headers, config) {
                    if (error) {
                        error(data);
                    }
                });
        };
        //获取数云用户信息
        var load = function () {
            //组装用户基本信息
            loadUserInfo(
                function () {
                    //组装卡及积分信息
                    loadCardInfo(
                        function () {
                            //组装优惠券信息
                            loadCouponInfo(
                                function () {
                                    angular.forEach(_fns, function (fn) {
                                        fn(_userinfo);
                                    });
                                    _fns = [];
                                },
                                function (data) {
                                    console.log(data);
                                }
                            );
                        },
                        function (data) {
                            console.log(data);
                        }
                    );
                },
                function (data) {
                    console.log(data);
                }
            );
        };
        //获取微信用户信息
        var code = getURLParams($location.url()).code;
        BrandInfo.get(function (brandInfo) {
            _brandInfo = brandInfo;
            $http.post(path + 'remoteInterface/invoke.do',
                {
                    url: _brandInfo.config.baseRestUrl + 'user/fetchUserToken',
                    code: code
                })
                .success(function (data, status, headers, config) {
                    if(data) {
                        var openid = data[0].openid;
                        //设置用户行为收集的用户id
                        UserAction.setUser(openid, "openid");
                        //获取用户微信信息并加载用户信息
                        $http.post(path + 'remoteInterface/invoke.do',
                            {
                                url: _brandInfo.config.baseRestUrl + 'user/queryUserInfoForWechat',
                                openid: openid
                            })
                            .success(function (data, status, headers, config) {
                                if(data) {
                                    _wxUserinfo = data[0];
                                    load();
                                }
                            })
                            .error(function (data, status, headers, config) {
                                console.log(data);
                            });
                    }
                })
                .error(function (data, status, headers, config) {
                    console.log(data);
                });
        });
        return {
            get: function (fn) {
                if (typeof _userinfo === 'undefined') {
                    if(fn) {
                        _fns.push(fn);
                    }
                } else {
                    if(fn) {
                        fn(_userinfo);
                    }
                }
            },
            reload: function(fn) {
                _fns.push(fn);
                load();
            },
            save: function (userinfo, success, error) {
                var data = {
                    mobile: userinfo.mobile,
                    openId: userinfo.openId,
                    name: userinfo.name,
                    sex: userinfo.sex,
                    birthday: userinfo.birthday,
                    job: userinfo.job,
                    mail: userinfo.mail,
                    province: userinfo.province,
                    city: userinfo.city,
                    district: userinfo.district,
                    address: userinfo.address,
                    zipCode: userinfo.zipCode
                };
                data.url = _brandInfo.config.baseRestUrl + "wxuser/modifyWxUser";
                $http.post(path + 'remoteInterface/invoke.do', data)
                    .success(function (data, status, headers, config) {
                        if(data && data.status=='用户数据更新成功') {
                            if(success) {
                                load();
                                success();
                            }
                        } else {
                            if(error) {
                                error(data.status);
                            }
                        }
                    })
                    .error(function (data, status, headers, config) {
                        console.log(data);
                        if(error) {
                            error(data.status);
                        }
                    });
            }
        };
    }]);
