angular.module('radsticksApp')
  .service 'User', ($http, Notifications, Auth, Storage, $q) ->

    getUserProfile = (email) ->
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

    return {
      getUserProfile: getUserProfile
    }
