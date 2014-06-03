'use strict'

angular.module('jetcanApp')
  .controller 'MainCtrl', ($scope, Auth) ->
    $scope.Auth = Auth
