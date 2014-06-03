angular.module('jetcanApp')
  .service 'Notifications', ($timeout) ->
    data =
      successMessage: ''
      errorMessage: ''

    # default to fading out success messages
    # after three seconds
    success = (message, timeout=3) ->
      data.successMessage = message
      $timeout () ->
        data.successMessage = ''
      , timeout * 1000

    error = (message) ->
      data.errorMessage = message

    resetSuccessMessage = () ->
      data.successMessage = ''

    resetErrorMessage = () ->
      data.errorMessage = ''

    resetAll = () ->
      resetErrorMessage()
      resetSuccessMessage()

    return {
      data: data
      success: success
      error: error
      resetAll: resetAll
      resetSuccessMessage: resetSuccessMessage
      resetErrorMessage: resetErrorMessage
    }
