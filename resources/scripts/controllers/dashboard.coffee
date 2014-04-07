angular.module('radsticksApp')
  .controller 'DashboardCtrl',
    ($scope, Auth, Notifications) ->
      $scope.Auth = Auth
      $scope.Notifications = Notifications

      Auth.mustBeLoggedIn()

      # todo
