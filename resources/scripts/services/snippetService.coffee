angular.module('radsticksApp')
  .service 'Snippet', ($http, Notifications, Auth, Storage, $q) ->

    list = () ->
      deferred = $q.defer()

      $http(
        method: 'GET'
        url: 'api/snippet'
        headers: { 'auth_token': Storage.getToken() }
      )
        .success (payload, status, headers, config) ->
          deferred.resolve(payload)
        .error (payload, status, headers, config) ->
          Notifications.error('Snippet List : ' + status)

      return deferred.promise

    create = (snippetData) ->
      snippetData['user'] = Storage.getUserEmail()

      deferred = $q.defer()

      $http(
        method: 'POST'
        url: 'api/snippet'
        headers: { 'auth_token': Storage.getToken() }
        data: snippetData
      )
        .success (payload, status, headers, config) ->
          deferred.resolve(payload)
        .error (payload, status, headers, config) ->
          Notifications.error('Snippet creation failed: ' + status)

      return deferred.promise

    update = (snippetData) ->

      deferred = $q.defer()

      $http(
        method: 'POST'
        url: 'api/snippet/' + snippetData['id']
        headers: { 'auth_token': Storage.getToken() }
        data: snippetData
      )
        .success (payload, status, headers, config) ->
          deferred.resolve(payload)
        .error (payload, status, headers, config) ->
          Notifications.error('Snippet creation failed: ' + status)

      return deferred.promise

    destroy = (snippetId) ->
      console.log 'Destroy ' + snippetId

    return {
      list: list
      create: create
      update: update
      destroy: destroy
    }
