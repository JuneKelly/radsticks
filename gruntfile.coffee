# Grunt tasks

BOWER = 'resources/bower_components'

module.exports = (grunt) ->
  grunt.initConfig

    watch:
      coffee:
        files: [
          "resources/scripts/**/*.coffee"
        ]
        tasks: "newer:coffee"
        options:
          liveReload: true
      styles:
        files: ["resources/styles/*.sass"]
        tasks: "compass"
      views:
        files: ["resources/views/*.html"]
        tasks: "newer:copy:views"
      fonts:
        files: ["resources/fonts/*"]
        tasks: "newer:copy:fonts"
      images:
        files: ["resources/images/*"]
        tasks: "newer:copy:images"

    exec:
      server:
        cmd: "lein with-profile dev ring server-headless"
      leinspec:
        cmd: "lein with-profile testing spec"
      build:
        cmd: "lein with-profile production ring uberjar"

    coffee:
      compile:
        files:
          "resources/public/static/js/app.js": [
            "resources/scripts/*.coffee"
            "resources/scripts/services/*.coffee"
            "resources/scripts/controllers/*.coffee"
          ]

    copy:
      views:
        expand: true
        cwd: "resources/views"
        dest: "resources/public/static/views/"
        src: "*.html"
      fonts:
        expand: true
        cwd: "resources/fonts"
        dest: "resources/public/static/fonts"
        src: "**/*"
      images:
        expand: true
        cwd: "resources/images"
        dest: "resources/public/static/img"
        src: "**/*"

    compass:
      dist:
        options:
          sassDir: "resources/styles"
          cssDir:  "resources/public/static/css"

    parallel:
      server:
        options:
          stream: true
        tasks: [{
          grunt: true
          args: 'exec:server'
        }, {
          grunt: true
          args: 'watch'
        }]

    protractor:
      manual:
        options:
          configFile: "./spec/config/protractor.conf.js"
          keepAlive: false

    concat:
      vendor_js:
        src: [
          BOWER+"/angular/angular.min.js",
          BOWER+"/angular-resource/angular-resource.min.js",
          BOWER+"/angular-cookies/angular-cookies.min.js",
          BOWER+"/angular-sanitize/angular-sanitize.min.js",
          BOWER+"/angular-ui-router/release/angular-ui-router.min.js",
          BOWER+"/angular-animate/angular-animate.min.js",
          BOWER+"/jquery/jquery.min.js",
          BOWER+"/lodash/dist/lodash.min.js",
          BOWER+"/sass-bootstrap/dist/js/bootstrap.min.js",
          BOWER+"/angular-bootstrap/ui-bootstrap.min.js",
          BOWER+"/angular-bootstrap/ui-bootstrap-tpls.min.js",
          BOWER+"/ng-tags-input/ng-tags-input.min.js"
        ]
        dest: "resources/public/static/js/vendor.js"
      vendor_css:
        src: [
          BOWER+"/sass-bootstrap/dist/css/bootstrap.min.css",
          BOWER+"/ng-tags-input/ng-tags-input.min.css"
        ]
        dest: "resources/public/static/css/vendor.css"


  grunt.loadNpmTasks "grunt-exec"
  grunt.loadNpmTasks "grunt-contrib-coffee"
  grunt.loadNpmTasks "grunt-contrib-watch"
  grunt.loadNpmTasks "grunt-contrib-copy"
  grunt.loadNpmTasks "grunt-newer"
  grunt.loadNpmTasks "grunt-parallel"
  grunt.loadNpmTasks "grunt-contrib-compass"
  grunt.loadNpmTasks "grunt-protractor-runner"
  grunt.loadNpmTasks "grunt-contrib-concat"

  grunt.registerTask "server", [
    "compass"
    "copy:views"
    "concat:vendor_js"
    "concat:vendor_css"
    "copy:fonts"
    "copy:images"
    "coffee"
    "parallel:server"
  ]

  grunt.registerTask "build", [
    "compass"
    "copy:views"
    "concat:vendor_js"
    "concat:vendor_css"
    "copy:fonts"
    "copy:images"
    "coffee"
    "exec:build"
  ]

  grunt.registerTask "test:all", [
    "test:frontend",
    "test:backend"
  ]

  grunt.registerTask "test:backend", [
    "exec:leinspec"
  ]

  grunt.registerTask "test:frontend", [
    "protractor:manual"
  ]
