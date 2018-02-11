define([], function () {
    var module = window.module = angular.module('app.component', []);
    module.addRequires = function (requires) {
        module.requires = module.requires.concat(requires);
    };
    module.addCSS = function (url) {
        var link = document.createElement("link");
        link.type = "text/css";
        link.rel = "stylesheet";
        link.href = url;
        document.getElementsByTagName("head")[0].appendChild(link);
    }
});