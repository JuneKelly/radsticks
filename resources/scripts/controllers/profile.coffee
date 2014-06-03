angular.module('jetcanApp')
  .controller 'ProfileCtrl',
    ($scope, Auth, Notifications, User, $stateParams) ->
      $scope.Auth = Auth
      $scope.Notifications = Notifications

      Auth.mustBeLoggedIn()

      $scope.editMode = false
      $scope.editToggle = () ->
        $scope.editMode = !$scope.editMode

      $scope.userEmail = $stateParams.id
      $scope.profile = null

      $scope.isCurrentUser = () ->
        $scope.userEmail == Auth.currentUser()

      $scope.loadProfile = () ->
        User.get($scope.userEmail)
          .then (profileData) ->
            $scope.profile = profileData

      $scope.updateProfile = () ->
        User.update($scope.userEmail, $scope.profile)
        $scope.editMode = false

      if Auth.loggedIn()
        $scope.loadProfile()

