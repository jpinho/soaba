"use strict";

App.rootElement = '.main.page';
App.setupForTesting();
App.injectTestHelpers();

module("Integration tests", {
  setup: function() {
    Ember.run(App, App.advanceReadiness);
  },

  teardown: function() {
    App.reset();
  }
});