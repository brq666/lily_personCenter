$module = function () {
    var route = ['script/init/config/route-config'],
        directive = [
            'script/directive/nc-input',
            'script/directive/nc-grid',
            'script/directive/nc-finishRender',
            'script/directive/nc-radio',
            'script/directive/nc-select',
            'script/directive/nc-collapse',
            'script/directive/nc-calender',
            'script/directive/nc-select-tree',
            'script/directive/nc-sidr',
            'script/directive/nc-minimizable',
            'script/directive/nc-menu',
            'script/directive/nc-breadcrumb',
            'script/gw-directives'
        ],
        service = [
            'script/service/user',
            'script/service/common'
        ],
        filter = [];
    return route.concat(directive).concat(service).concat(filter);
}();