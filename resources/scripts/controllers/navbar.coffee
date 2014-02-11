angular.module('radsticksApp')
  .controller 'NavbarCtrl', ($scope, Auth) ->
    $scope.Auth = Auth
    $scope.aboutText = 'About'

    $scope.sendLogin = (user) ->
      Auth.login(user.username, user.password)
      user.username = ''
      user.password = ''
      $scope.loginForm.setPristine
