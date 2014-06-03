angular.module('jetcanApp')
  .service 'Util', ($state) ->

    kickToRoot = () ->
      $state.go('app.main')

    return {
      kickToRoot: kickToRoot
    }
