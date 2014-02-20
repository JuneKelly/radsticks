angular.module('radsticksApp')
  .service 'Auth', ($http, Notifications, $state) ->

    data =
      email: ''
      token: ''

    reset = () ->
      data.email = ''
      data.token = ''
      Notifications.resetAll()

    register = (credentials) ->
      reset()
      $http(
        method: 'POST'
        url: '/api/user'
        data: credentials
        headers: { 'Accept': 'application/json' }
      )
        .success (payload, status, headers, config) ->
          login(credentials.email, credentials.password)

        .error (payload, status, headers, config) ->
          console.log 'ERROR'
          console.log status
          Notifications.error(
            'Error, User registration failed'
          )

    login = (email, password) ->
      reset()
      $http(
        method: 'POST'
        url: '/api/auth'
        data: {email: email, password: password}
        headers: { 'Accept': 'application/json' }
      )
        .success (payload, status, headers, config) ->
          console.log payload
          console.log status

          if payload.token == null
            Notifications.error(
              'Error, authentication failed'
            )
          else
            if status == 201
              data.email = payload.email
              data.token = payload.token
              Notifications.success('Logged in as ' + data.email)
            else
              Notifications.error(
                'Error, authentication failed'
              )

        .error (payload, status, headers, config) ->
          console.log 'ERROR'
          console.log status
          console.log payload
          Notifications.error(
            'Error, authentication failed'
          )

    mustBeLoggedIn = () ->
      if !loggedIn()
        Notifications.error('You must be logged in to do that')
        $state.go('app.main')

    loggedIn = () ->
      if data.token == ''
        false
      else
        true

    return {
      data: data
      login: login
      reset: reset
      register: register
      loggedIn: loggedIn
      mustBeLoggedIn: mustBeLoggedIn
    }

