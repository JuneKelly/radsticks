angular.module('radsticksApp')
  .service 'Auth', ($http) ->
    data =
      username: ''
      token: ''
      errorMessage: ''

    reset = () ->
      data.username = ''
      data.token = ''
      data.errorMessage = ''

    login = (username, password) ->
      reset()

      $http(
        method: 'POST'
        url: '/api/auth'
        data: {username: username, password: password}
        headers: { 'Accept': 'application/json' }
      )
        .success (payload, status, headers, config) ->
          console.log payload
          console.log status

          if payload.token == null
            data.errorMessage = 'Error, authentication failed'
          else
            if status == 201
              data.username = payload.username
              data.token = payload.token
            else
              data.errorMessage = 'Error, authentication failed'

        .error (payload, status, headers, config) ->
          console.log 'ERROR'
          console.log status
          data.errorMessage = 'Error, authentication failed'

    return {
      data: data
      login: login
    }

