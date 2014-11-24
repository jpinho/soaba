if (window.location.search.indexOf("?test") !== -1) {
  document.write(
    '<div id="qunit" style="margin-left: 16.66666667%"></div>' +
    '<div id="qunit-fixture"></div>' +
    //'<div id="ember-testing-container">' +
    //'  <div id="ember-testing"></div>' +
    //'</div>' +
    '<link rel="stylesheet" href="tests/runner.css">' +
    '<link rel="stylesheet" href="tests/vendor/qunit-1.12.0.css">' +
    '<script src="tests/vendor/qunit-1.12.0.js"></script>' +
    '<script src="tests/tests.js"></script>'
  )
}
