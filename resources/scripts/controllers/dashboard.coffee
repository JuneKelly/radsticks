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
            snippetData: () ->
              $scope.snippets[index]
        )

        modal.result.then (updatedSnippet) ->
          console.log updatedSnippet  # TODO
          Snippet.update(updatedSnippet)
            .then (result) ->
              Notifications.success('Snippet updated')

      $scope.deleteSnippet = (index) ->
        if confirm('Delete this snippet?')
          snippetData = $scope.snippets[index]
          Snippet.destroy(snippetData.id)
            .then (result) ->
              if result
                Notifications.success('Snippet deleted')
                $scope.snippets.splice(index, 1)
              else
                Notifications.error('Something went wrong')

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


EditSnippetCtrl = ($scope, $modalInstance, snippetData) ->

  $scope.snippet =
    content: snippetData.content
    tags: snippetData.tags

  $scope.ok = () ->
    result = snippetData
    if typeof $scope.snippet.tags == "string"
      result.tags = $scope.snippet
        .tags
        .replace(/^\s*|\s*$/g,'')
        .split(/\s*,\s*/)
    else
      result.tags = $scope.snippet.tags
    result.content = $scope.snippet.content

    $modalInstance.close(result)

  $scope.cancel = () ->
    $modalInstance.dismiss('cancel')


