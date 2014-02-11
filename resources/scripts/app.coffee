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
        abstract: true
        url: ''
        views:
          navbar:
            templateUrl: 'static/views/navbar.html'
            controller: 'NavbarCtrl'
          notifications:
            templateUrl: 'static/views/notifications.html'
            controller: 'NotificationsCtrl'
      .state 'root.main',
        url: '/'
        views:
          'container@': {
            templateUrl: 'static/views/main.html'
            controller: 'MainCtrl'
          }
      .state 'root.about',
        url: '/about'
        views:
          'container@': {
            templateUrl: 'static/views/about.html'
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
