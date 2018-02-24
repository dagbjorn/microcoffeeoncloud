/**
 * Contains environment specific configuration of the application.
 *
 * Assigns __env to the root window object.
 */
(function(window) {
    window.__env = window.__env || {};

    // REST API gateway
    window.__env.apiGatewayUrl = window.location.href.split('/').slice(0, 3).join('/');

    // Logging
    window.__env.enableDebug = true;

}(this));
