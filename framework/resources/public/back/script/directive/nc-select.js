/**
 * 下拉选择框
 *
 * Created by Virushi on 2016/4/5.
 *
 * selectConfig中的字段
 * data: 具体内容（icon图标、name大标题、value具体内容、ticked是否选中）
 * multiSelect: 默认单选，可设置为"true"多选
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
    'lib/angular-select/isteven-multi-select'
], function () {
    module.addCSS('lib/angular-select/isteven-multi-select.css');
    module.addRequires([
        'isteven-multi-select'
    ]);

    module.directive('ncSelect', function() {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                label: '@', selectConfig: '=', result: '=', labelWidth: '@', seat: '@'
            },
            template: "<div class=\"form-group nc-collapse-item\">" +
            "  <label class=\"control-label nc-label\" ng-show='label != null'>{{label}}</label>" +
            "  <div class=\"nc-collapse-item-content\">" +
            "    <div isteven-multi-select" +
            "           input-model=\"selectConfig.data\"" +
            "           is-disabled=\"selectConfig.readonly\"" +
            "           output-model=\"result\"" +
            "           button-label=\"icon name\"" +
            "           item-label=\"icon name\"" +
            "           tick-property=\"ticked\"" +
            "           selection-mode=\"{{selectConfig.multiSelect == true ? '' : 'single'}}\"" +
            //"           output-properties=\"name maker ticked value\"" +
            "           helper-elements=\"\"'></div>" +
            "  </div>" +
            "</div>",
            link: function(scope, element, attr) {
                if (scope.labelWidth != null) {
                    element.find(".nc-label").css("width", scope.labelWidth);
                    element.find(".nc-label").attr("labelWidth", scope.labelWidth);
                }
                if (scope.seat != null) {
                    element.attr("seat", scope.seat);
                }
                if (attr.hasOwnProperty("readonly") && "readonly" in attr) {
                    scope.selectConfig.readonly = true;
                    element.find(".nc-label").css("color", "#8B8686");
                    setTimeout(function(){
                        element.find("button").css("color", "#8B8686").css("cursor", "no-drop");
                    },0)
                }
            },
            controller: function ($scope, $state, $http, $timeout) {
                var selectOptions = $scope.selectConfig;

                function setInputData (data) {
                    angular.forEach(data, function(value, index) {
                        //console.log("name:"+value.orgId+"-"+"marker:"+value.id)
                        selectOptions.data[index] = {
                            name: value.orgId,
                            marker: value.id,
                            track: false
                        };
                    })
                }

                $scope.refreshWatch = function () {
                    //关闭监测
                    if ($scope.resultWatch != null) {
                        $scope.resultWatch();
                    }
                    //获取旧的显示数据
                    $scope.inputModelOld = angular.copy($scope.selectConfig.data);
                    $timeout(function(){
                        $scope.resultWatch = $scope.$watch("result", function(newVal, oldVal){
                            if (newVal != oldVal && !($scope.selectConfig.beforeChange == null ? true : $scope.selectConfig.beforeChange(
                                    newVal, oldVal
                                ))) {
                                //获取旧值
                                $scope.result = oldVal;
                                newVal = oldVal;
                                $scope.selectConfig.data = $scope.inputModelOld;
                                //重启监测
                                $scope.refreshWatch();
                            }

                            angular.forEach($scope.selectConfig.data, function(value, index){
                                value.ticked = false;
                            });
                            if ($scope.result != null) {
                                angular.forEach($scope.result, function(value, index){
                                    if (value.ticked) {
                                        angular.forEach($scope.selectConfig.data, function(val, ind){
                                            if (val.value == value.value) {
                                                val.ticked = true;
                                            }
                                        });
                                    }
                                })
                            }

                            if ($scope.selectConfig.afterChange != null && (newVal != oldVal)) {
                                $scope.selectConfig.afterChange(newVal, oldVal);
                                $scope.refreshWatch();
                            }
                        });
                    });
                };

                $scope.refreshWatch();
            }
        }
    })
});