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
        method: 'PUT'
        url: 'api/snippet/' + snippetData['id']
        headers: { 'auth_token': Storage.getToken() }
        data: snippetData
      )
        .success (payload, status, headers, config) ->
          deferred.resolve(payload)
        .error (payload, status, headers, config) ->
          Notifications.error('Snippet update failed: ' + status)

      return deferred.promise

    destroy = (snippetId) ->
      deferred = $q.defer()

      $http(
        method: 'DELETE'
        url: 'api/snippet/' + snippetId
        headers: { 'auth_token': Storage.getToken() }
      )
        .success (payload, status, headers, config) ->
          deferred.resolve(true)
        .error (payload, status, headers, config) ->
          Notifications.error('Snippet delete failed: ' + status)
          deferred.resolve(false)

      return deferred.promise

    return {
      list: list
      create: create
      update: update
      destroy: destroy
    }
