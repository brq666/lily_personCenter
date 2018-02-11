/**
 * ng-repeat渲染完毕后执行相应的事件
 *
 * Created by Virushi on 2016/4/6.
 */
define([], function () {
    module.addRequires([]);

    module.directive('ncFinishRender', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attr) {
                if (scope.$last === true) {
                    element.ready(function () {
                        //nc-radio 布局
                        if (attr.ncFinishRender == 'horizontal') {
                            $(".radio-item").css("float", "left")
                        }
                    });
                }
            }
        }
    })
});