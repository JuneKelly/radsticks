angular.module('jetcanApp')
  .service 'Storage', () ->

    store = localStorage

    getUserEmail = () ->
      store['jetcan_user']

    setUserEmail = (email) ->
      store['jetcan_user'] = email

    getToken = () ->
      store['jetcan_token']

    setToken = (token) ->
      store['jetcan_token'] = token

    return {
      getUserEmail: getUserEmail
      setUserEmail: setUserEmail
      getToken: getToken
      setToken: setToken
    }
