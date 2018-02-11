angular.module('starter.webPageContainer', [])
    .controller('webPageContainerCtrl', function ($scope, $stateParams) {

        $(".web-page-container").attr('src', $stateParams.url);

    });
