angular.module('radsticksApp')
  .service 'Storage', () ->

    store = localStorage

    getUserEmail = () ->
      store['radsticks_user']

    setUserEmail = (email) ->
      store['radsticks_user'] = email

    getToken = () ->
      store['radsticks_token']

    setToken = (token) ->
      store['radsticks_token'] = token

    return {
      getUserEmail: getUserEmail
      setUserEmail: setUserEmail
      getToken: getToken
      setToken: setToken
    }
