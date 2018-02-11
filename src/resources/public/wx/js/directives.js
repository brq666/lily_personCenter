angular.module('starter.directives', [])
  .directive('headPic', ["$state", function($state){
    return {
      restrict: 'E',
      replace: true,
      scope: {userIcon: '='},
      template: "<img alt=\"头像\" class=\"head-pic-normal\" ng-src=\"{{path}}{{userIcon==null ? 'resource/image/P0_icon_1.png' : userIcon}}\">",
      controller: function($scope) {
        $scope.path = path;
      }
    }
  }])
  .directive('autoFocusWhen', ["$timeout", function ($timeout) {
    return {
      restrict: 'A',
      scope: {
        autoFocusWhen: '='
      },
      link: function (scope, element) {
        scope.$watch('autoFocusWhen', function (newValue) {
          if (newValue) {
            $timeout(function () {
              element[0].focus();
            })
          }
        });
      }
    }
  }])
  //为了控制带输入框的footer，解决ios浏览器上滑出键盘时input被遮住的问题
  .directive('footerWithInput', ["platformUtil",function(platformUtil){
    return {
      restrict: 'A',
      link: function(scope, element, tAttr) {
        if(platformUtil.isIos()) {
          var ele = $(element[0]);
          window.addEventListener('native.keyboardshow', function keyboardShowHandler(e) {
            ele.css({bottom: e.keyboardHeight, position: "absolute"});
          });
          window.addEventListener('native.keyboardhide', function keyboardShowHandler(e) {
            ele.css({bottom: 0, position: "absolute"});
          });
        }
      }
    }
  }])
  .directive('gradientLine', function(){
    return {
      restrict: 'E',
      //replace: true,
      scope: {type: '@'},
      template: "<div class=\"{{type}}\">" +
                " <div class=\"transit-line\">" +
                "   <div class=\"transit-line-two\">" +
                "     <div class=\"transit-line-three\"></div>" +
                "   </div>" +
                " </div>" +
                "</div>",
      link: function(scope, element, tAttr) {
        if(scope.type.indexOf('header-bar') != -1) {
          var pageHeader = $(element).prev();
          var top = pageHeader.offset().top + pageHeader[0].offsetHeight - 1;
          element.children().css('top', top + 'px');
        }
      }
    }
  });
