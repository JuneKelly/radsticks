angular.module('radsticksApp')
  .service 'Auth', ($http, Notifications, Util, $state, Storage) ->

    reset = () ->
      Storage.setUserEmail('')
      Storage.setToken('')
      Notifications.resetAll()
      Util.kickToRoot()

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
          if payload.token == null
            Notifications.error(
              'Error, authentication failed'
            )
          else
            if status == 201
              Storage.setToken(payload.token)
              Storage.setUserEmail(payload.email)
              Notifications.success(
                'Logged in as ' + Storage.getUserEmail())
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
        Util.kickToRoot()

    loggedIn = () ->
      token = Storage.getToken()
      if token == '' or token == null or token == "null"
        false
      else
        true

    currentUser = () ->
      Storage.getUserEmail()

    return {
      currentUser: currentUser
      login: login
      reset: reset
      register: register
      loggedIn: loggedIn
      mustBeLoggedIn: mustBeLoggedIn
    }

