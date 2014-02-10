angular.module('radsticksApp')
  .service 'Notifications', () ->
    data =
      successMessage: ''
      errorMessage: ''

    success = (message) ->
      data.successMessage = message

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
