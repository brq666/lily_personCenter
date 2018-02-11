/**
 * 日历控件
 *
 * Created by Virushi on 2016/4/7.
 *
 * calenderConfig中的数据
 * format: 时间格式,默认为 yyyy-MM-dd
 * ngRequired: form验证, 默认为 true
 * closeText: 关闭按钮文字, 默认为 关闭
 * currentText: 当前按钮文字, 默认为 当前
 * clearText: 重置按钮文字, 默认为 重置
 * showButtonBar: 是否显示按钮 默认为 true
 * dateOptions中数据
 *     minMode: 最小视图, 默认为 day(可选:year,month)
 *     maxMode: 最大视图, 默认为 day(可选:year,month)
 *     minDate: 最小日期, 默认为 无. 可用new Date()限制,之前的日期显示为灰色,但是可选
 *     maxDate: 最大视图, 默认为 无. 可用new Date('2099, 12, 31')限制,之后的日期显示为灰色,但是可选
 *     startingDay: 月视图每行开始星期, 默认为0,星期天
 * beforeChange: 变化之前 callback 函数，默认返回 false
 * afterChange: 变化之后 callback 函数，默认返回 false
 *
 * attr:
 * label: 前置标签
 * readOnly: dom加上此属性即为只读，按钮不可点击
 * result: 双向绑定数值,用户自定
 * labelWidth: 前置标签标签宽度
 * seat: 控件在collapse控件中占据行宽，默认为 1
 */
define([
    'script/utils/angular-locale_zh-cn'
], function () {
    module.addRequires([]);

    module.directive('ncCalender', function() {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                label: '@', calenderConfig: '=', result: '=', labelWidth: '@', seat: '@'
            },
            template: "<div class=\"form-group nc-collapse-item\">" +
            "               <label class=\"control-label nc-label\" ng-show='label != null'>{{label}}</label>" +
            "               <div class=\"nc-collapse-item-content input-group nc-calender\">" +
            "                   <input type=\"text\" readonly class=\"form-control\" uib-datepicker-popup=\"{{calenderConfig.format == null ? 'yyyy-MM-dd' : calenderConfig.format}}\"" +
            "                       ng-model=\"calResult\"" +
            "                       is-open=\"isOpen\"" +
            "                       datepicker-options=\"calenderConfig.dateOptions == null ? calenderConfig.ownDateOptions : calenderConfig.dateOptions\"" +
            "                       ng-required=\"calenderConfig.ngRequired == null ? true : calenderConfig.ngRequired\"" +
            "                       close-text=\"{{calenderConfig.closeText == null ? '关闭' : calenderConfig.closeText}}\"" +
            "                       current-text=\"{{calenderConfig.currentText == null ? '当前' : calenderConfig.currentText}}\"" +
            "                       clear-text=\"{{calenderConfig.clearText == null ? '重置' : calenderConfig.clearText}}\"" +
            "                       show-button-bar=\"calenderConfig.showButtonBar == null ? true : calenderConfig.showButtonBar\"" +
            "                       alt-input-formats=\"calenderConfig.altInputFormats\"/>" +
            "                   <span class=\"input-group-btn\">" +
            "                       <button type=\"button\" class=\"btn nc-calender-btn\" ng-click=\"isOpen = !isOpen\">" +
            "                           <img src=\"./img/4.png\">" +
            "                       </button>" +
            "                   </span>" +
            "               </div>" +
            "           </div>",
            link: function (scope, element, attr) {
                if (scope.labelWidth != null) {
                    element.find(".nc-label").css("width", scope.labelWidth);
                    element.find(".nc-label").attr("labelWidth", scope.labelWidth);
                }
                if (scope.seat != null) {
                    element.attr("seat", scope.seat);
                }
                if (attr.hasOwnProperty("readonly") && "readonly" in attr) {
                    element.find("button").attr("disabled", "disabled");
                    element.find(".nc-label").css("color", "#8B8686");
                    setTimeout(function(){
                        element.find("input").css("color", "#8B8686").css("cursor", "no-drop");
                    },0)
                }
            },
            controller:function ($scope, $timeout) {
                if ($scope.calenderConfig.hasOwnProperty('defaultData')) {
                    $scope.calResult = $scope.calenderConfig.defaultData;
                } else {
                    $scope.calResult = $scope.result;
                }
                $scope.refreshWatch = function(){
                    if ($scope.calenderWatch != null) {
                        $scope.calenderWatch();
                    }
                    $scope.calenderWatch = $scope.$watch("calResult", function(newVal, oldVal){
                        if(newVal!=oldVal && !($scope.calenderConfig.beforeChange == null ? true : $scope.calenderConfig.beforeChange(newVal, oldVal))){
                            $scope.calResult = angular.copy(oldVal);
                            newVal = oldVal;
                            $scope.refreshWatch();
                        } else {
                            $scope.result = $scope.calResult;
                        }
                        if ($scope.calenderConfig.afterChange != null && (newVal != oldVal)) {
                            $scope.calenderConfig.afterChange(newVal, oldVal);
                            $scope.refreshWatch();
                        }
                    });
                };

                $scope.refreshWatch();

                $scope.$watch("result", function(newVal, oldVal){
                    if (newVal!=oldVal && newVal != $scope.calResult) {
                        $scope.calResult = newVal;
                        $scope.refreshWatch();
                    }
                })
            }
        }
    })
});