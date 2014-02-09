'use strict'

angular.module('radsticksApp')
  .controller 'MainCtrl', ($scope, $http, Auth) ->
    $scope.Auth = Auth

    $scope.sendLogin = (user) ->
      Auth.login(user.username, user.password)

