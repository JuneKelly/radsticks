'use strict'

angular.module('radsticksApp', [
  'ngCookies',
  'ngResource',
  'ngSanitize',
  'ui.router'
])
  .config ($stateProvider, $urlRouterProvider) ->
    $stateProvider
      .state 'root',
        url: ''
        views:
          navbar:
            templateUrl: 'static/views/navbar.html'
            controller: 'NavbarCtrl'
      .state 'root.main',
        url: '/'
        views:
          'container@': {
            templateUrl: 'static/views/main.html'
            controller: 'MainCtrl'
          }

    $urlRouterProvider
      .otherwise('/')

    #$routeProvider
    #  .when '/',
    #    templateUrl: 'static/views/main.html'
    #    controller: 'MainCtrl'
    #  .otherwise
    #    redirectTo: '/'


# collapse the menu on click
$(->
  navMain = $("#main-menu")

  navMain.on(
    "click",
    "a",
    null,
    () -> navMain.collapse('hide')
  )
)
