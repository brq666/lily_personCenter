/**
 * Created by sgq on 2016/10/27.
 */
define([
    'lib/zTree/jquery.ztree.all.min'
], function(){
    module.addCSS('lib/zTree/zTreeStyle.css');

    var defaultOptions = {
        view: {
            selectedMulti: false,
            dblClickExpand: false
        }
    };

    function getSelectTreeId() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }

    function NCMenuService(menu){
        this.menu = menu;
    }

    NCMenuService.prototype.refresh = function(node){
        var selectedNode = this.menu.getSelectedNodes()[0];
        var index = this.menu.getNodeIndex(selectedNode);
        this.menu.getNodes()[index] = node;
        this.menu.refresh();
    };

    NCMenuService.prototype.getSelectedNode = function(){
        return this.menu.getSelectedNodes()[0];
    };

    NCMenuService.prototype.addNode = function(selectedNode, newNode){
        this.menu.addNodes(selectedNode, newNode);
    };

    NCMenuService.prototype.removeNode = function(selectedNode){
        this.menu.removeNode(selectedNode);
    };

    module.directive("ncMenu", function(){
        return {
            restrict: "E",
            replace: true,
            scope: {
                menuConfig: "="
            },
            template:
            "       <div class=\"nc-menu\">" +
            "           <div class=\"content_wrap\">" +
            "               <div class=\"zTreeDemoBackground left\">" +
            "                   <ul class=\"ztree\"></ul>" +
            "               </div>" +
            "           </div>" +
            "           <div class=\"rMenu\">" +
            "               <ul ng-repeat='item in menuConfig.rightMenu'>" +
            "                   <li class=\"{{item.class}}\" ng-click=\"item.func(); hideMenu()\">{{item.title}}</li>" +
            "               </ul>" +
            "           </div>" +
            "       </div>",
            link: function(scope, element, attr){
                /*
                * 变量
                * */
                var zTree;
                var rMenu;
                var id = scope.menuConfig.id;
                var rMenuId = "rMenu" + id;
                var options = scope.menuConfig.options;
                var data = scope.menuConfig.data;
                /*
                * 属性设置
                * */
                //给当前menu赋予唯一id
                element.find(".ztree").attr("id", id);
                element.find(".rMenu").attr("id", rMenuId);

                /*
                * 回调函数
                * */
                function onClick(event, treeId, treeNode) {
                    scope.menuConfig.editNode(treeNode);
                    zTree.selectNode(treeNode);
                }
                function onRightClick(event, treeId, treeNode) {
                    if (!treeNode && event.target.tagName.toLowerCase() != "button" && $(event.target).parents("a").length == 0) {
                        zTree.cancelSelectedNode();
                        showRMenu("root", event, null);
                    } else if (treeNode && !treeNode.noR) {
                        var isMenuRec = treeNode.resourceType;
                        zTree.selectNode(treeNode);
                        showRMenu("node", event, isMenuRec);
                    }
                }
                // 添加回调函数到插件
                scope.menuConfig.options.callback = {
                    onClick: onClick,
                    onRightClick: onRightClick
                };
                /*
                * 普通使用
                * */
                function showRMenu(type, event, isMenuRec) {
                    $("#" + rMenuId +" ul").show();
                    if (type=="root") {
                        $("#" + rMenuId + " .m_add").show();
                        $("#" + rMenuId + " .m_del").hide();
                    } else {
                        $("#" + rMenuId + " .m_del").show();
                        if (isMenuRec == 1) {
                            $("#" + rMenuId + " .m_add").show();
                        } else {
                            $("#" + rMenuId + " .m_add").hide();
                        }
                    }
                    //计算右键菜单的位置
                    var container = document.getElementById(id);
                    var menu = document.getElementById(rMenuId);
                    /*获取当前鼠标右键按下后的位置，据此定义菜单显示的位置*/
                    var rightedge = container.clientWidth-event.clientX;
                    var bottomedge = container.clientHeight-event.clientY;
                    /*如果从鼠标位置到容器右边的空间小于菜单的宽度，就定位菜单的左坐标（Left）为当前鼠标位置向左一个菜单宽度*/
                    if (rightedge < menu.offsetWidth) {
                        menu.style.left = container.scrollLeft + event.clientX - menu.offsetWidth + "px";
                    } else {
                        /*否则，就定位菜单的左坐标为当前鼠标位置*/
                        menu.style.left = container.scrollLeft + event.clientX + "px";
                    }
                    /*如果从鼠标位置到容器下边的空间小于菜单的高度，就定位菜单的上坐标（Top）为当前鼠标位置向上一个菜单高度*/
                    if (bottomedge < menu.offsetHeight) {
                        menu.style.top = container.scrollTop + event.clientY - menu.offsetHeight + "px";
                    } else {
                        /*否则，就定位菜单的上坐标为当前鼠标位置*/
                        menu.style.top = container.scrollTop + event.clientY + "px";
                    }
                    /*设置菜单可见*/
                    menu.style.visibility = "visible";

                    $("body").bind("mousedown", onBodyMouseDown);
                }

                function hideRMenu() {
                    if (rMenu) rMenu.css({"visibility": "hidden"});
                    $("body").unbind("mousedown", onBodyMouseDown);
                }
                function onBodyMouseDown(event){
                    if (!(event.target.id == rMenuId || $(event.target).parents("#" + rMenuId).length>0)) {
                        rMenu.css({"visibility" : "hidden"});
                    }
                }

                /*
                * 指令綁定函數
                * */
                scope.hideMenu = function() {
                    hideRMenu();
                };

                /*
                * 初始化
                * */
                function init(id, options, data){
                    var menuObj = $("#" + id);
                    $.fn.zTree.init(menuObj, options, data);
                    zTree = $.fn.zTree.getZTreeObj(id);
                    rMenu = $("#" + rMenuId);

                    // 初始化 ncMenuService
                    if(scope.menuConfig.loadMenuService){
                        var ncMenuService = new NCMenuService(zTree);
                        scope.menuConfig.loadMenuService(ncMenuService);
                    }
                }

                init(id, options, data);

                //数据发生变化,刷新menu
                scope.$watch('menuConfig.data',function (n,o) {
                    data = n;
                    init(id, options, data);
                });
            },
            controller: function($scope){
                var menuConfig = $scope.menuConfig;
                menuConfig.id = getSelectTreeId();
                menuConfig.options = angular.extend({}, defaultOptions, menuConfig.options);
            }
        }
    })
});