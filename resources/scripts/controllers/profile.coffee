angular.module('radsticksApp')
  .controller 'ProfileCtrl',
    ($scope, Auth, Notifications, User, $stateParams) ->

      Auth.mustBeLoggedIn()

      $scope.editMode = false
      $scope.editToggle = () ->
        $scope.editMode = !$scope.editMode

      $scope.Auth = Auth
      $scope.Notifications = Notifications
      $scope.userEmail = $stateParams.id
      $scope.profile = null

      $scope.isCurrentUser = () ->
        $scope.userEmail == Auth.currentUser()

      $scope.loadProfile = () ->
        User.getUserProfile($scope.userEmail)
          .then (profileData) ->
            $scope.profile = profileData

      $scope.updateProfile = () ->
        console.log 'Updating'
        console.log $scope.profile

      if Auth.loggedIn()
        $scope.loadProfile()

