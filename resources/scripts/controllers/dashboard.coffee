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
            $scope.snippets = snippetData

      $scope.showModal = () ->
        modal = $modal.open(
          templateUrl: 'static/views/new_snippet.html'
          controller: NewSnippetCtrl
        )

        modal.result.then (newSnippet) ->
          Snippet.create(newSnippet).then (result) ->
            $scope.snippets.unshift(result)

      # load
      $scope.loadSnippets()


NewSnippetCtrl = ($scope, $modalInstance) ->
  $scope.snippet =
    content: ""
    tags: ""

  $scope.ok = () ->
    result =
      content: $scope.snippet.content
      tags: $scope.snippet.tags.replace(/^\s*|\s*$/g,'').split(/\s*,\s*/)
    $modalInstance.close(result)

  $scope.cancel = () ->
    $modalInstance.dismiss('cancel')
