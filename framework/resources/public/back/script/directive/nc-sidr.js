/**
 * 树形菜单
 * attr:
 * menus:[{
 *      routerPath:路径
 *      menus:子菜单
 *      collapsed:是否折叠
 * }]
 */
define([
    'lib/sidr/dist/jquery.sidr'
], function (jquery) {
    module.addCSS('lib/sidr/dist/stylesheets/jquery.sidr.light.css');

    function buildMenu(menus, html, collapsed, levle, rootFlag, collapsable) {
        html.push('<ul class="menu');
        if (typeof collapsed === 'undefined' || collapsed) {
            html.push(' collapsed');
        }
        html.push('"> ');
        angular.forEach(menus, function (menu, index) {
            html.push('<li class="menuListItem"><a href="');
            var routerPath = 'javascript:void(0)';
            if (menu['routerPath']) {
                routerPath = "#" + menu.routerPath;
            }
            html.push(routerPath + '" ');
            if (menu['children'] && menu.children.length > 0) {
                if (rootFlag) {
                    html.push('style="padding-left:' + (10 + 20 * levle) + 'px" class="menuGroup rootMenu rootMenuGroup');
                } else {
                    html.push('style="padding-left:' + (10 + 20 * levle) + 'px" class="menuGroup');
                }
                if (typeof menu.collapsed === 'undefined' || menu.collapsed) {
                    html.push(' collapsed"');
                } else {
                    html.push('"');
                }
                if (typeof menu.collapsable === 'undefined' || collapsable === true) {
                    html.push(' collapsable=true');
                }
            } else {
                if (rootFlag) {
                    html.push('style="padding-left:' + (32 + 20 * levle) + 'px" class="menuItem rootMenu"');
                } else {
                    html.push('style="padding-left:' + (32 + 20 * levle) + 'px" class="menuItem"');
                }

            }
            html.push('>' + menu.name + '</a>');
            if (menu['children'] && menu.children.length > 0) {
                buildMenu(menu.children, html, menu.collapsed, levle + 1);
            }
            html.push('</li>');
        });
        html.push('</ul>');
    }

    module.directive('ncSidr', function ($compile) {
        function getSidrId() {
            return "sidr-" + Math.floor((1 + Math.random()) * 0x10000)
                    .toString(16)
                    .substring(1);
        }

        return {
            restrict: 'E',
            replace: true,
            scope: {
                menus: "=menus"
            },
            template: '<div><a class="sidr-menu-button" ><div class="sidr-menu-button-show"></div><div class="sidr-menu-button-hidden"></div></a><div></div></div>',
            link: function (scope, element, attr) {
                var menuButton = element.find(".sidr-menu-button")
                $(window).resize(function () {
                    //menuButton.css("top", document.body.clientHeight / 2 - 34 + "px");
                    //部分页面refresh后body高度未撑开，导致sidr偏高
                    menuButton.css("top", $(".sidr").height() / 2 - 34 + "px");
                });

                // buildMenu();
                scope.$watch('menus', function (newVal, oldVal) {
                    if (newVal && newVal.length > 0)
                        buildMenus();
                });

                function buildMenus() {
                    var sidrMenu = element.children().eq(1);
                    var sidrId = getSidrId();
                    sidrMenu.attr("id", sidrId);
                    var menus = scope.menus;
                    var html = [];
                    buildMenu(menus, html, false, 0, true);
                    sidrMenu.append(html.join(''));
                    sidrMenu.find('.menuGroup,.menuItem').click(function () {
                        var target = $(this);
                        if (!target.attr("collapsable")) {
                            return;
                        }
                        if (target.hasClass('menuGroup')) {
                            var ul = target.parent().children("ul");
                            if (target.hasClass("collapsed")) {
                                target.removeClass("collapsed");
                                ul.removeClass("collapsed");
                                target.addClass("secMenuBorder");
                                setTimeout(function () {
                                    ul[0].style.height = null;
                                }, 400);
                            } else {
                                ul[0].style.height = ul.height() + 'px';
                                setTimeout(function () {
                                    target.addClass("collapsed");
                                    ul.addClass("collapsed");
                                }, 10);
                                setTimeout(function () {
                                    target.removeClass("secMenuBorder");
                                }, 300);
                            }
                        } else {
                            sidrMenu.find('.menuItem').removeClass('selected');
                            target.addClass('selected');
                        }
                    });
                    element.children().eq(0).sidr({
                        speed: 10,
                        name: sidrId
                    });
                    $.sidr('open', sidrId);
                    $compile(element)(scope);
                    setTimeout(function () {
                        var menus = sidrMenu.find('.menu.collapsed');
                        for (var index = 0, length = menus.length; index < length; index++) {
                            var children = menus.eq(index).children();
                            menus[index].style.height = children.length * children.eq(0).height() + 'px';
                        }
                    }, 0);
                }
            },
            controller: function ($scope) {

            }
        }
    })
});