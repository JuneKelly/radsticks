angular.module('radsticksApp')
  .service 'Util', ($state) ->

    kickToRoot = () ->
      $state.go('app.main')

    return {
      kickToRoot: kickToRoot
    }
