angular.module('radsticksApp')
  .service 'User', ($http, Notifications, Auth, Storage, $q) ->


    get = (email) ->
      deferred = $q.defer()

      $http(
        method: 'GET'
        url: 'api/user/' + email
        headers: {'auth_token': Storage.getToken() }
      )
        .success (payload, status, headers, config) ->
          deferred.resolve(payload)

        .error (payload, status, headers, config) ->
          if status == 401
            Notifications.error('You are not authorized to do that')

      return deferred.promise


    update = (email, newData) ->
      data =
        email: email
        name: newData.name

      $http(
        method: 'POST'
        url: 'api/user/' + email
        headers: {'auth_token': Storage.getToken() }
        data: data
      )
        .success (payload, status, headers, config) ->
          console.log payload
          console.log status
          Notifications.success('Updated Profile of ' + email)

        .error (payload, status, headers, config) ->
          Notifications.error(status + ', something went wrong')


    return {
      get: get
      update: update
    }
