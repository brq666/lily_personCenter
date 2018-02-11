requirejs.config({
        waitSeconds: 0,
        paths: {
            angular: 'lib/angular/angular.min',
            ocLazyLoad: 'lib/oclazyload/dist/ocLazyLoad.min',
            router: 'lib/angular-ui-router/release/angular-ui-router.min',
            cookies: 'lib/angular-cookies/angular-cookies.min',
            bootstrap: 'lib/angular-bootstrap/ui-bootstrap-tpls.min',
            jquery: 'lib/jquery/dist/jquery.min',
            bootstrap_js: 'lib/bootstrap-3.3.7-dist/js/bootstrap.min',
            bootbox: 'lib/bootbox.min',
            contextmenu: 'lib/bootstrap-contextmenu/bootstrap-contextmenu',
            app: 'script/init/app',
            route: 'script/init/route',
            exportModule: 'script/init/module',
            moduleConfig: 'script/init/config/module-config',

            jquery_transit: 'script/lib/jquery.transit',
            ng_file_upload: 'script/lib/ng-file-upload',

            utils: 'script/utils',

            path: 'script/common/path'

        },
        shim: {
            'angular': {
                deps: ['jquery'],
                exports: 'angular'
            },
            'ocLazyLoad': ['angular'],
            'router': ['angular'],
            'cookies': ['angular'],
            'bootstrap': ['angular'],
            'exportModule': ['angular'],
            'route': ['exportModule', 'moduleConfig'],
            'bootstrap_js': ['jquery'],
            'bootbox': ['jquery', 'bootstrap_js'],
            'contextmenu': ['jquery'],
            'ng_file_upload': ['angular'],
            'app': ['jquery', 'angular', 'ng_file_upload', 'ocLazyLoad', 'router', 'cookies', 'bootstrap', 'route', 'bootbox',
                    'contextmenu', 'path', 'jquery_transit', 'utils']
        }
    }
);

require([
        'app'
    ], function () {
        angular.bootstrap(document, ["app"]);
    }
);

Array.prototype.remove = function (index) {
    if (isNaN(index) || index > this.length) {
        return false;
    }
    this.splice(index, 1);
};

if (!Array.prototype.indexOf) {
    Array.prototype.indexOf = function (elt) {
        var len = this.length >>> 0;
        var from = Number(arguments[1]) || 0;
        from = (from < 0)
            ? Math.ceil(from)
            : Math.floor(from);
        if (from < 0)
            from += len;
        for (; from < len; from++) {
            if (from in this &&
                this[from] === elt)
                return from;
        }
        return -1;
    };
}

Date.prototype.format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S": this.getMilliseconds()
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
};

var encode = function (json) {
    if (!json) {
        return '';
    }
    var tmps = [];
    for (var key in json) {
        tmps.push(key + '=' + json[key]);
    }
    return tmps.join('&');
};