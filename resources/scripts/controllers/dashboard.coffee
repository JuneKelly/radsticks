angular.module('radsticksApp')
  .controller 'DashboardCtrl',
    ($scope, Auth, Notifications, Snippet, $modal) ->
      $scope.Auth = Auth
      $scope.Notifications = Notifications

      Auth.mustBeLoggedIn()

      $scope.snippets = []

      $scope.loadSnippets = () ->
        Snippet.list()
          .then (snippetData) ->
            console.log snippetData
            $scope.snippets = snippetData

      $scope.showModal = () ->
        modal = $modal.open(
          templateUrl: 'static/views/new_snippet.html'
          controller: NewSnippetCtrl
        )

        modal.result.then (newSnippet) ->
          console.log "LOL"

      # load
      $scope.loadSnippets()


NewSnippetCtrl = ($scope, $modalInstance) ->
  $scope.snippet =
    content: ""
    tags: []

  $scope.ok = () ->
    $modalInstance.close($scope.snippet)

  $scope.cancel = () ->
    $modalInstance.dismiss('cancel')
