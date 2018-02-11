/**
 * 输入框
 *
 * Created by Virushi on 2016/4/5.
 *
 * attr:
 * label: 前置标签
 * readOnly: dom加上此属性即为只读，按钮不可点击
 * result: 双向绑定数值,用户自定
 * labelWidth: 前置标签标签宽度
 * seat: 控件在collapse控件中占据行宽，默认为 1
 */

define([], function () {
    module.addRequires([]);
    module.directive('ncInput', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                label: '@', inputConfig: '=', result: '=', placeholder: '@', labelWidth: '@', seat: '@'
            },
            template: "<div class=\"form-group nc-collapse-item\">" +
                "          <label class=\"control-label nc-label\" ng-show='label != null'>{{label}}</label>" +
                "          <div class=\"nc-collapse-item-content\">" +
                "              <input ng-model='result' class='nc-input' placeholder='{{placeholder}}'>" +
                "          </div>" +
                "      </div>",
            link: function (scope, element, attr) {
                if (scope.labelWidth != null) {
                    element.find(".nc-label").css("width", scope.labelWidth);
                    element.find(".nc-label").attr("labelWidth", scope.labelWidth);
                }
                if (scope.seat != null) {
                    element.attr("seat", scope.seat);
                }
                if (attr.hasOwnProperty("readonly") && "readonly" in attr) {
                    element.find("input").attr("readOnly", "true");
                    element.find(".nc-label").css("color", "#8B8686");
                    setTimeout(function(){
                        element.find("input").css("color", "#8B8686").css("cursor", "no-drop");
                    },0)
                }
            },
            controller: function ($scope, $state) {
                if ($scope.inputConfig && $scope.inputConfig.hasOwnProperty("defaultData")) {
                    $scope.result = $scope.inputConfig.defaultData;
                }
            }
        }
    })
});