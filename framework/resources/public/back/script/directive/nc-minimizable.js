define([], function () {
    module.directive('ncMinimizable', [function () {
        return {
            'restrict': 'A',
            'link': function (scope, element, attr) {
                var button = $('<a class="sidr-menu-button"><div class="sidr-menu-button-show"></div><div class="sidr-menu-button-hidden"></div></a>')
                button.css('position', ' absolute');
                var position = attr.ncMinimizable ? attr.ncMinimizable : 'left';
                element.prop('position', position);
                if (position == 'left') {
                    button.css('top', element.height() / 2);
                    button.css('left', 0);
                } else if (position == 'right') {
                    button.css('top', element.height() / 2);
                    button.css('right', 0);
                } else if (position == 'top') {
                    button.css('top', 0);
                    button.css('left', element.width() / 2);
                } else if (position == 'bottom') {
                    button.css('bottom', 0);
                    button.css('left', element.width() / 2);
                }
                button.click(function () {
                    var position = element.prop('position');
                    if (element.hasClass("nc-minimizable-hm")) {
                        element.removeClass("nc-minimizable-hm");
                        if (scope.whenMinimize) {
                            scope.whenMinimize(element, false)
                        }
                    } else if (element.hasClass("nc-minimizable-vm")) {
                        element.removeClass("nc-minimizable-vm");
                        if (scope.whenMinimize) {
                            scope.whenMinimize(element, false)
                        }
                    } else if (position == 'left' || position == 'right') {
                        element.addClass("nc-minimizable-hm");
                        if (scope.whenMinimize) {
                            scope.whenMinimize(element, true);
                        }
                    } else if (position == 'top' || position == 'bottom') {
                        element.addClass("nc-minimizable-vm");
                        if (scope.whenMinimize) {
                            scope.whenMinimize(element, true);
                        }
                    }
                });
                element.append(button);
            }
        };
    }]);
});

