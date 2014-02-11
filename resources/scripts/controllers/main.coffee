'use strict'

angular.module('radsticksApp')
  .controller 'MainCtrl', ($scope, $http, Auth) ->
    $scope.Auth = Auth
