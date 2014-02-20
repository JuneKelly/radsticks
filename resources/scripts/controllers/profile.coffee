angular.module('radsticksApp')
  .controller 'ProfileCtrl',
    ($scope, Auth, Notifications, User, $stateParams) ->

      $scope.Auth = Auth
      $scope.Notifications = Notifications

      $scope.userEmail = $stateParams.id
      $scope.profile = null

      $scope.loadProfile = () ->
        User.getUserProfile($scope.userEmail)
          .then (profileData) ->
            $scope.profile = profileData

      $scope.loadProfile()

