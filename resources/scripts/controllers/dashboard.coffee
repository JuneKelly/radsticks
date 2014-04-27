angular.module('radsticksApp')
  .controller 'DashboardCtrl',
    ($scope, Auth, Notifications, Snippet, $modal) ->
      $scope.Auth = Auth
      $scope.Notifications = Notifications

      Auth.mustBeLoggedIn()

      # State
      $scope.snippets = []

      # Loading snippets
      $scope.loadSnippets = () ->
        Snippet.list()
          .then (snippetData) ->
            $scope.snippets = snippetData

      # Creating new snippets
      $scope.showNewSnippetModal = () ->
        modal = $modal.open(
          templateUrl: 'static/views/new_snippet.html'
          controller: NewSnippetCtrl
        )

        modal.result.then (newSnippet) ->
          Snippet.create(newSnippet).then (result) ->
            $scope.snippets.unshift(result)

      # Editing Snippets
      $scope.showEditSnippetModal = (index) ->
        modal = $modal.open(
          templateUrl: 'static/views/edit_snippet.html'
          controller: EditSnippetCtrl
          resolve:
            snippet: () ->
              $scope.snippets[index]
        )

        modal.result.then (updatedSnippet) ->
          console.log updatedSnippet  # TODO

      $scope.deleteSnippet = (index) ->
        if confirm('Delete this snippet?')
          console.log 'DELETE'  # TODO

      # load page
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

  # TODO : transform to the view model and back again,
  # rather than direct binding
  $scope.snippet = snippet

  $scope.ok = () ->
    result = $scope.snippet
    if typeof result.tags == "string"
      result.tags = result.tags.
        replace(/^\s*|\s*$/g,'').split(/\s*,\s*/)

    $modalInstance.close(result)

  $scope.cancel = () ->
    $modalInstance.dismiss('cancel')


