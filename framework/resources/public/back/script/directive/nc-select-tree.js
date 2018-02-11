/**
 * 下拉选择树
 *
 * Created by Virushi on 2016/4/5.
 *
 * selectTreeD中的字段
 * data: 具体数据（icon图标、name大标题、maker具体内容、ticked是否选中）
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
    'lib/zTree/jquery.ztree.all.min'
], function () {
    module.addCSS('lib/zTree/zTreeStyle.css');
    module.addRequires([]);

    var defaultSelectTreeOptions = {
        view: {
            dblClickExpand: false
        },
        data: {
            simpleData: {
                enable: true
            }
        }
    };

    function getSelectTreeId() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }

    module.directive('ncSelectTree', function($timeout) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                label: '@', selectTreeConfig: '=', result: '=', labelWidth: '@', seat: '@'
            },
            template: "<div class=\"nc-select-tree nc-collapse-item\">" +
            "              <label class=\"control-label nc-label\" ng-show='label != null'>{{label}}</label>" +
            "              <div class=\"nc-select-tree-main nc-collapse-item-content input-group\">" +
            "                  <input type=\"button\" value=\"\" class=\"nc-select-tree-input\" ng-click=\"showMenu()\"/>" +
            "                  <span class=\"input-group-btn\">" +
            "                      <button type=\"button\" class=\"btn nc-select-tree-drop-btn\" ng-click=\"showMenu()\">" +
            "                          <img src=\"./img/6.png\">" +
            "                      </button>" +
            "                  </span>" +
            "                  <div class=\"nc-select-tree-content\">" +
            "                      <div class='nc-select-tree-content-menu'><ul class=\"ztree\"></ul></div>" +
            "                      <div class='nc-select-tree-content-btn'><button ng-click='close()'>关闭</button></div>" +
            "                  </div>" +
            "              </div>" +
            "          </div>",
            link: function(scope, element, attr) {
                if (scope.labelWidth != null) {
                    element.find(".nc-label").css("width", scope.labelWidth);
                    element.find(".nc-label").attr("labelWidth", scope.labelWidth);
                }
                if (scope.seat != null) {
                    element.attr("seat", scope.seat);
                }
                if (attr.hasOwnProperty("readonly") && "readonly" in attr) {
                    element.find("input").attr("disabled", "disabled");
                    element.find("button").attr("disabled", "disabled");
                    element.find(".nc-label").css("color", "#8B8686");
                    setTimeout(function(){
                        element.find("input").css("color", "#8B8686").css("cursor", "no-drop");
                    },0)
                }
                //2016-11-02 计算inputWidth宽度
                function initInputWidth (){
                    var inputWidth = element.find(".nc-select-tree-main").width();
                    element.find(".nc-select-tree-input").css("width", inputWidth - 33 + "px");
                }
                $(window).resize(function(){
                    initInputWidth();
                });
                initInputWidth();


                var isMultiSelect = scope.selectTreeConfig.multiSelect;
                var curMenu = null, zTree_Menu = null, zTree_nodes = null;
                //给当前tree赋予唯一id
                element.find(".ztree").attr("id", scope.selectTreeConfig.name);

                function beforeClick(treeId, treeNode) {
                    var zTree = $.fn.zTree.getZTreeObj(treeId);
                    if(isMultiSelect){
                        zTree.checkNode(treeNode, !treeNode.checked, null, true);
                        return false;
                    } else {
                        zTree.expandNode(treeNode);
                        return true;
                    }
                }

                function onClick(e,treeId, treeNode, callback) {
                    var zTree = $.fn.zTree.getZTreeObj(treeId);
                    var nodes = zTree.getSelectedNodes();

                    var enableFlag = true;
                    scope.oldVal = [];
                    scope.newVal = [];
                    scope.oldVal = angular.copy(scope.result);
                    scope.newVal = nodes;
                    //beforeChange
                    if (scope.selectTreeConfig.beforeChange != null) {
                        enableFlag = scope.selectTreeConfig.beforeChange(
                            scope.newVal,
                            scope.oldVal
                        );
                    }
                    if (enableFlag) {
                        var v = "";
                        nodes.sort(function compare(a,b){return a.id-b.id;});
                        for (var i=0, l=nodes.length; i<l; i++) {
                            v += nodes[i].name + ",";
                        }
                        if (v.length > 0 ) v = v.substring(0, v.length-1);
                        var cityObj = $("#" + treeId).parent().parent().parent().children(0);
                        cityObj.attr("value", v);
                        //将选到的值返回前台
                        $timeout(function(){
                            scope.result = nodes;
                        })
                    }
                    //afterChange
                    if (scope.selectTreeConfig.afterChange != null && enableFlag) {
                        scope.selectTreeConfig.afterChange(
                            scope.newVal,
                            scope.oldVal
                        );
                    }
                    //2016-11-02 添加单选点击后隐藏菜单的判断条件
                    if (!treeNode.hasOwnProperty("children") && !scope.selectTreeConfig.multiSelect) {
                        hideMenu();
                    }
                }

                function onCheck(e,treeId, treeNode, callback) {
                    var zTree = $.fn.zTree.getZTreeObj(treeId);
                    var nodes = zTree.getCheckedNodes(true);

                    var enableFlag = true;
                    scope.oldVal = [];
                    scope.newVal = [];
                    scope.oldVal = angular.copy(scope.result);
                    scope.newVal = nodes;
                    //beforeChange
                    if (scope.selectTreeConfig.beforeChange != null) {
                        enableFlag = scope.selectTreeConfig.beforeChange(
                            scope.newVal,
                            scope.oldVal
                        );
                    }
                    if (enableFlag) {
                        var v = "";
                        nodes.sort(function compare(a,b){return a.id-b.id;});
                        for (var i=0, l=nodes.length; i<l; i++) {
                            if(!nodes[i].isParent){
                                v += nodes[i].name + ",";
                            }
                        }
                        if (v.length > 0 ) v = v.substring(0, v.length-1);
                        var cityObj = $("#" + treeId).parent().parent().parent().children(0);
                        cityObj.attr("value", v);
                        //将选到的值返回前台
                        $timeout(function(){
                            scope.result = nodes;
                        });
                    }
                    //afterChange
                    if (scope.selectTreeConfig.afterChange != null && enableFlag) {
                        scope.selectTreeConfig.afterChange(
                            scope.newVal,
                            scope.oldVal
                        );
                    }
                    //2016-11-02 添加单选点击后隐藏菜单的判断条件
                    if (!treeNode.hasOwnProperty("children") && !scope.selectTreeConfig.multiSelect) {
                        hideMenu();
                    }
                }

                //回调函数
                if (isMultiSelect) {
                    scope.selectTreeConfig.options.callback = {
                        beforeClick: beforeClick,
                        onCheck: onCheck
                    };
                } else {
                    scope.selectTreeConfig.options.callback = {
                        beforeClick: beforeClick,
                        onClick: onClick
                    };
                }

                //显示下拉框
                scope.showMenu  = function() {
                    if (element.find(".nc-select-tree-content").css("display") == 'none'){
                        var cityObj = element.find('input');
                        var cityOffset = element.find('input').offset();
                        element.find('.nc-select-tree-content').css({left:cityOffset.left + "px", top:cityOffset.top + cityObj.outerHeight() + "px"}).slideDown("fast");
                        $("body").bind("mousedown", onBodyDown);
                    }
                };
                //关闭下拉框
                scope.close = function (e) {
                    hideMenu();
                };
                //隐藏下拉框
                function hideMenu() {
                    element.find('.nc-select-tree-content').fadeOut("fast");
                    $("body").unbind("mousedown", onBodyDown);
                }
                //点击其他区域
                function onBodyDown(event) {
                    if (!(event.target.className == "nc-select-tree-content" || $(event.target).parents(".nc-select-tree-content").length>0)) {
                        hideMenu();
                    }
                }

                //初始化下拉框
                function treeInit (options, data, id) {
                    curMenu = null;
                    subIndex = [];
                    selectedArr = [];
                    deepCount = 1;
                    //2016-10-27 去除空值
                    var temp = [];
                    angular.forEach(data, function(value){
                        if (value){
                            temp.push(value);
                        }
                    });

                    var treeObj = $("#" + id);
                    $.fn.zTree.init(element.find('.ztree'), options, temp);
                    zTree_Menu = $.fn.zTree.getZTreeObj(id);

                    //默认选中
                    zTree_nodes = zTree_Menu.getNodes();
                    getIndex(zTree_nodes);
                    //获取选中节点
                    angular.forEach(selectedArr, function(value, index){
                        if (typeof(value) == 'number') {
                            curMenu = zTree_nodes[value];
                        } else {
                            angular.forEach(value.split(","), function(val, ind) {
                                if (ind == 0) {
                                    curMenu = zTree_nodes[val];
                                } else {
                                    curMenu = curMenu.children[val];
                                }
                            });
                        }
                        //放入选中框
                        //2016-11-02 第二个参数为是否多选显示
                        if(isMultiSelect){
                            zTree_Menu.checkNode(curMenu, true, false, true);
                        } else {
                            zTree_Menu.selectNode(curMenu, true);
                        }
                    });
                    //将选择数据放到input框和result中
                    var selectNodes = [];
                    if (isMultiSelect) {
                        selectNodes = zTree_Menu.getCheckedNodes(true);
                    } else {
                        selectNodes = zTree_Menu.getSelectedNodes();
                    }

                    if (selectNodes.length > 0) {
                        var v = "";
                        selectNodes.sort(function compare(a,b){return a.id-b.id;});
                        for (var i=0, l=selectNodes.length; i<l; i++) {
                            if (isMultiSelect){
                                if(!selectNodes[i].isParent){
                                    v += selectNodes[i].name + ",";
                                }
                            } else {
                                v += selectNodes[i].name + ",";
                            }
                        }
                        if (v.length > 0 ) v = v.substring(0, v.length-1);
                        var cityObj = treeObj.parent().parent().parent().children(0);
                        cityObj.attr("value", v);

                        //将选到的值返回前台
                        $timeout(function(){
                            scope.result = selectNodes;
                        });
                    } else {
                        treeObj.parent().parent().parent().children(0).attr("value", "");
                    }

                    //显示打开/关闭图标
                    treeObj.addClass("showIcon");
                }

                var subIndex = [];
                var selectedArr = [];
                var deepCount = 1;
                var getIndex = function (data) {
                    angular.forEach(data, function (value, index) {
                        subIndex.push(index);

                        if (value.ticked || value.checked) {
                            var temp = "";
                            angular.forEach(subIndex, function(value, index){
                                if (temp === "") {
                                    temp = value;
                                } else {
                                    temp = temp + "," + value;
                                }
                            });
                            selectedArr.push(temp);
                            value.ticked = false;
                            value.checked = false;
                        }

                        if (value.children != null && value.children.length > 0){
                            deepCount++;
                            getIndex(value.children);
                        } else {
                            subIndex.splice(-1);
                        }

                        if (index == (data.length - 1) && deepCount > 1) {
                            subIndex.splice(-1);
                            deepCount--;
                        }
                    });
                };

                //数据发生变化,刷新下拉框
                scope.$watch('selectTreeConfig.data',function (n,o) {
                    treeInit(scope.selectTreeConfig.options, scope.selectTreeConfig.data, scope.selectTreeConfig.name);
                });

                scope.refreshResult = function () {
                    setTimeout(function(){
                        scope.resultWatch = scope.$watch("result", function(n,o){
                            if (n != o) {
                                scope.resultWatch();
                                angular.forEach(scope.selectTreeConfig.data, function(value, index){
                                    value.ticked = false;
                                    angular.forEach(scope.result, function(val, ind){
                                        if (value.id == val.id){
                                            value.ticked = true;
                                        }
                                    })
                                });
                                if(!isMultiSelect){
                                    treeInit(scope.selectTreeConfig.options, scope.selectTreeConfig.data, scope.selectTreeConfig.name);
                                }
                                scope.refreshResult();
                            }
                        })
                    }, 100);
                };

                scope.refreshResult();
            },
            controller: function ($scope, $state, $http) {
                $scope.http = $http;
                var selectTreeConfig = $scope.selectTreeConfig;

                selectTreeConfig.name = getSelectTreeId();

                //2016-11-02 是否使用selectedMulti，是，则自动转换为 check模式
                if (selectTreeConfig.multiSelect) {
                    defaultSelectTreeOptions.check = {
                        enable: true
                    };
                    defaultSelectTreeOptions.view.selectedMulti = true;
                } else {
                    defaultSelectTreeOptions.view.selectedMulti = false;
                }

                selectTreeConfig.options = angular.extend({}, defaultSelectTreeOptions, selectTreeConfig.options);
            }
        }
    })
});