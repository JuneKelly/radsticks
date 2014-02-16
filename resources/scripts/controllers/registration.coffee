angular.module('radsticksApp')
  .controller 'RegistrationCtrl', ($scope, Auth, Notifications) ->
    $scope.Auth = Auth

    $scope.register = (user) ->
      if user.passwordOne != user.passwordTwo
        Notifications.error('Passwords must match')
      else
        credentials =
          email: user.email
          name: user.displayName
          password: user.passwordOne
        Auth.register(credentials)
