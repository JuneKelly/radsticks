'use strict'

angular.module('radsticksApp')
  .controller 'MainCtrl', ($scope, $http, AuthService) ->
    $scope.AuthService = AuthService

    $scope.sendLogin = (user) ->
      AuthService.login(user.username, user.password)

