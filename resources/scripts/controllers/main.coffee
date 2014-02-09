'use strict'

angular.module('radsticksApp')
  .controller 'MainCtrl', ($scope, $http, Auth) ->
    $scope.Auth = Auth

    $scope.sendLogin = (user) ->
      Auth.login(user.username, user.password)
      user.username = ''
      user.password = ''
      $scope.loginForm.setPristine

