'use strict'

angular.module('radsticksApp')
  .controller 'MainCtrl', ($scope, Auth) ->
    $scope.Auth = Auth
