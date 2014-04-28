'use strict'

angular.module('radsticksApp', [
  'ngCookies',
  'ngResource',
  'ngSanitize',
  'ngAnimate',
  'ui.router',
  'ui.bootstrap',
  'ngTagsInput'
])
  .config ($stateProvider, $urlRouterProvider) ->
    $stateProvider
      .state 'app',
        abstract: true
        url: ''
        views:
          navbar:
            templateUrl: 'static/views/navbar.html'
            controller: 'NavbarCtrl'
          notifications:
            templateUrl: 'static/views/notifications.html'
            controller: 'NotificationsCtrl'
      .state 'app.main',
        url: '/'
        views:
          'container@': {
            templateUrl: 'static/views/main.html'
            controller: 'MainCtrl'
          }
      .state 'app.about',
        url: '/about'
        views:
          'container@': {
            templateUrl: 'static/views/about.html'
          }
      .state 'app.register',
        url: '/register'
        views:
          'container@':
            templateUrl: 'static/views/register.html'
            controller: 'RegistrationCtrl'
      .state 'app.profile',
        url: '/profile/:id'
        views:
          'container@':
            templateUrl: 'static/views/profile.html'
            controller: 'ProfileCtrl'
      .state 'app.dashboard',
        url: '/dashboard'
        views:
          'container@':
            templateUrl: 'static/views/dashboard.html'
            controller: 'DashboardCtrl'


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
