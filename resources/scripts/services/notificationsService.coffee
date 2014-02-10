angular.module('radsticksApp')
  .service 'Notifications', () ->
    data =
      successMessage: ''
      errorMessage: ''

    resetSuccessMessage = () ->
      data.successMessage = ''

    resetErrorMessage = () ->
      data.errorMessage = ''

    resetAll = () ->
      resetErrorMessage()
      resetSuccessMessage()

    return {
      data: data
      resetAll: resetAll
      resetSuccessMessage: resetSuccessMessage
      resetErrorMessage: resetErrorMessage
    }
