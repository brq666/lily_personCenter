/**
 * Created by Virushi on 2016/4/7.
 *
 * 伸缩框
 * title：标题
 * column：列数
 * item-label-width：content 中 item 上的 label 宽度，不写默认为 90
 */
define([], function () {
    module.addRequires([]);

    module.directive('ncCollapse', function() {
        return {
            restrict: 'E',
            replace: false,
            transclude: true,
            scope: {
                title: '@', column: '@', itemLabelWidth: '@'
            },
            template: "<div class=\"nc-collapse\">" +
            "   <div class=\"nc-collapse-bar\">" +
            "       <span>{{title}}</span>" +
            "       <button type=\"button\" class=\"btn pull-right nc-collapse-title-btn\" ng-click=\"isOPen = !isOPen\" style='float: right'>" +
            "           <span ng-if='!title'><img src='./img/5.png'></span>" +
            "           <span ng-if='title'><img src='./img/5.png'></span>" +
            "       </button>" +
            "   </div>" +
            "   <div uib-collapse=\"isOPen\">" +
            "       <div class=\"nc-collapse-content\" ng-transclude>userDom</div>" +
            "   </div>" +
            "</div>",
            link: function (scope, element, attr) {
                //获取条目数,行间添加分割线
                var children = element.find(".nc-collapse-content").children();
                var itemNums = 0;
                angular.forEach(children, function(value, index){
                    var itemWidth = 1;
                    if (value.hasAttribute("seat")) {
                        itemWidth = $(value).attr("seat") > scope.column ? scope.column : $(value).attr("seat");
                    }
                    itemNums += parseInt(itemWidth);
                    if (itemNums == scope.column && index != children.length - 1) {
                        $(value).after('<hr class="nc-collapse-hr">');
                        itemNums = 0;
                    } else if (itemNums > scope.column) {
                        $(children[index - 1]).after('<hr class="nc-collapse-hr">');
                        if (children[index -1].hasAttribute("seat")) {
                            itemNums -= parseInt($(children[index - 1]).attr("seat") > scope.column ? scope.column : $(children[index - 1]).attr("seat"));
                        } else {
                            itemNums -= 1;
                        }
                    }
                });

                function init() {
                    //当前容器宽度
                    var width = element.find(".nc-collapse-content").width();
                    if (width == "100" && element.parents(".modal-dialog").length > 0) {
                        //嵌套在modal中,默认modal宽为 800
                        width = element.parent().css("width") == null ? 800 : parseInt(element.parent().css("width").substr(0, element.parent().css("width").length-2)
                            - getEnableWidth(element.parent())
                        );
                        element.parents().find(".modal-dialog").width(element.parent().css("width"));
                    }
                    //去掉scroll的宽度
                    width = width - 10;
                    //按 column 算出每个列宽，并赋值
                    var childWidth = width/scope.column - getEnableWidth(".nc-collapse-item") - getEnableWidth(".nc-label");
                    element.find(".nc-collapse-item").width(childWidth);
                    //nc-label 宽度
                    var ncLabelWidth = (typeof (scope.itemLabelWidth) == 'undefine' || scope.itemLabelWidth == null) ? 90 : scope.itemLabelWidth;
                    var tempLabelWidth = ncLabelWidth;
                    angular.forEach(element.find(".nc-label"), function(value, index){
                        if (value.hasAttribute("labelWidth")) {
                            tempLabelWidth = $(value).attr("labelWidth");
                        }
                        $(value).css("width", tempLabelWidth);
                        var childRightWidth = childWidth - tempLabelWidth;
                        if (value.parentElement.hasAttribute("seat")) {
                            var count = $(value).parent().attr("seat") > scope.column ? scope.column : $(value).parent().attr("seat");
                            childRightWidth = childWidth * count - tempLabelWidth;
                            $(value).parent().css("width", childWidth * count + 28);
                            $(value).next().css("width", childRightWidth + 16);
                        } else {
                            $(value).next().css("width", childRightWidth);
                        }
                        //给 nc-collapse-item-content 赋值
                        $(value).next().find(".select-input-btn").css("width", childRightWidth - 30);
                        $(value).next().find(".nc-select-tree-input").css("width", childRightWidth - 33);
                        //复原tempLabelWidth
                        tempLabelWidth = ncLabelWidth;
                    });
                }

                function getEnableWidth (dom) {
                    var width = 0;
                    var target;
                    if (typeof(dom) == "string") {
                        target = element.find(dom);
                    } else {
                        target = dom;
                    }
                    /*
                    * 2016-11-23 sgq 添加是否为 nc 组件的判断
                    * */
                    if (target.length > 0) {
                        width = parseInt(target.css("padding-left").substr(0,target.css("padding-left").length-2))
                            + parseInt(target.css("padding-right").substr(0,target.css("padding-right").length-2))
                            + parseInt(target.css("margin-left").substr(0,target.css("margin-left").length-2))
                            + parseInt(target.css("margin-right").substr(0,target.css("margin-right").length-2));
                        return width;
                    } else {
                        return 0;
                    }
                }
                init();
                $(window).resize(function(){
                    var width = element.find(".nc-collapse-content").width();
                    if (width > 100) {
                        init();
                    }
                });
            }
        }
    })
});