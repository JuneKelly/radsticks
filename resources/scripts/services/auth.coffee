angular.module('radsticksApp')
  .service 'AuthService', ($http) ->
    user =
      username: ''
      token: ''

    return {
      user: user
      login: (username, password) ->
        $http(
          method: 'POST'
          url: '/api/auth'
          data: {username: username, password: password}
          headers: { 'Accept': 'application/json' }
        )
          .success (data) ->
            console.log data
            user.username = data.username
            user.token = data.token
            console.log user.username
            console.log user.token
          .error (data) ->
            console.log 'Error'
    }
