angular.module('radsticksApp')
  .controller 'NavbarCtrl', ($scope, Auth) ->
    $scope.Auth = Auth
    $scope.aboutText = 'About'

    $scope.login = (user) ->
      Auth.login(user.username, user.password)
      user.username = ''
      user.password = ''

    $scope.logout = () ->
      console.log 'LOGOUT'
      Auth.reset()
