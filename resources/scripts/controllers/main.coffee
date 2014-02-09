'use strict'

angular.module('radsticksApp')
  .controller 'MainCtrl', ($scope, $http, AuthService) ->
    $scope.token = ''

    $scope.sendLogin = (user) ->
      AuthService.login(user.username, user.password)

    $scope.getUserData = () ->
      console.log AuthService.user
      $scope.token = AuthService.user.token
