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

      $scope.showNewSnippetModal = () ->
        modal = $modal.open(
          templateUrl: 'static/views/new_snippet.html'
          controller: NewSnippetCtrl
        )

        modal.result.then (newSnippet) ->
          Snippet.create(newSnippet).then (result) ->
            $scope.snippets.unshift(result)

      $scope.showEditSnippetModal = (index) ->
        modal = $modal.open(
          templateUrl: 'static/views/edit_snippet.html'
          controller: EditSnippetCtrl
          resolve:
            snippet: () ->
              $scope.snippets[index]
        )

        modal.result.then (updatedSnippet) ->
          console.log updatedSnippet

      $scope.deleteSnippet = (index) ->
        if confirm('Delete this snippet?')
          console.log 'DELETE'  # TODO

      # load
      $scope.loadSnippets()


# modal controllers
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


EditSnippetCtrl = ($scope, $modalInstance, snippet) ->

  $scope.snippet = snippet

  $scope.ok = () ->
    result = $scope.snippet
    if typeof result.tags == "string"
      result.tags = result.tags.
        replace(/^\s*|\s*$/g,'').split(/\s*,\s*/)

    $modalInstance.close(result)

  $scope.cancel = () ->
    $modalInstance.dismiss('cancel')


