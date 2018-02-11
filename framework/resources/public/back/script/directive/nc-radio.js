/**
 * radio button
 *
 * Created by Virushi on 2016/4/5.
 *
 * radioConfig中的字段
 * data：具体内容（name、value）
 * selected：默认选中项（option中的value值）
 * orientation：控件方向，默认horizontal（vertical）
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

    module.directive('ncRadio', function() {

        function getRadioName() {
            return Math.floor((1 + Math.random()) * 0x10000)
                .toString(16)
                .substring(1);
        }

        return {
            restrict: 'E',
            replace: true,
            scope: {
                label: '@', radioConfig: '=', result: '=', labelWidth: '@', seat: '@'
            },
            template: "<div class=\"form-group nc-collapse-item\">" +
            "  <label class=\"control-label nc-label\" ng-show='label != null'>{{label}}</label>" +
            "  <div class=\"nc-collapse-item-content\">" +
            "    <div class=\"radio-item nc-radio-item\" ng-repeat='option in radioConfig.data'>" +
            "      <input type='radio' ng-model=\"radioConfig.radResult\" name='{{radioConfig.name}}' value='{{option.value}}' ng-disabled='radioConfig.disabled || option.disabled'" +
            "             nc-finish-render=\"{{radioConfig.orientation == null ? radioConfig.orientation= 'horizontal' : radioConfig.orientation}}\" class=\"nc-radio\">{{option.name}}" +
            "    </div>" +
            "  </div>" +
            "</div>",
            link: function (scope, element, attr) {
                if (scope.labelWidth != null) {
                    element.find(".nc-label").css("width", scope.labelWidth);
                    element.find(".nc-label").attr("labelWidth", scope.labelWidth);
                }
                if (scope.seat != null) {
                    element.attr("seat", scope.seat);
                }
                if (attr.hasOwnProperty("readonly") && "readonly" in attr) {
                    scope.radioConfig.disabled = true;
                    element.find(".nc-label").css("color", "#8B8686");
                    setTimeout(function(){
                        element.find("div > div").css("color", "#8B8686").css("cursor", "no-drop");
                    },0)
                }
            },
            controller: function ($scope, $state) {
                $scope.radioConfig.name = getRadioName();

                if ($scope.radioConfig.defaultData != null && $scope.radioConfig.defaultData != '') {
                    $scope.radioConfig.radResult = $scope.radioConfig.defaultData;
                } else {
                    if ($scope.result != null && $scope.result !== '') {
                        $scope.radioConfig.radResult = $scope.result;
                    } else {
                        $scope.radioConfig.radResult = $scope.radioConfig.data[0].value;
                    }
                }

                $scope.refreshWatch = function(){
                    if ($scope.radioWatch != null) {
                        $scope.radioWatch();
                    }
                    $scope.radioWatch = $scope.$watch("radioConfig.radResult", function(newVal, oldVal){
                        if(newVal!=oldVal && !($scope.radioConfig.beforeChange == null ? true : $scope.radioConfig.beforeChange(newVal, oldVal))){
                            $scope.radioConfig.radResult = angular.copy(oldVal);
                            newVal = oldVal;
                            $scope.refreshWatch();
                        } else {
                            $scope.result = $scope.radioConfig.radResult;
                        }
                        if ($scope.radioConfig.afterChange != null && (newVal != oldVal)) {
                            $scope.radioConfig.afterChange(newVal, oldVal);
                            $scope.refreshWatch();
                        }
                    });
                };

                $scope.refreshWatch();

                $scope.$watch("result", function(newVal, oldVal){
                    if (newVal!=oldVal && newVal != $scope.radioConfig.radResult) {
                        $scope.radioConfig.radResult = newVal;
                        $scope.refreshWatch();
                    }
                })
            }
        }
    })
});

