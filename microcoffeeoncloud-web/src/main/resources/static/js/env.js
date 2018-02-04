/**
 * Contains environment specific configuration of the application.
 *
 * Assigns __env to the root window object.
 */
(function(window) {
    window.__env = window.__env || {};

    // REST API gateway
    window.__env.apiGatewayUrl = '${app.gateway.url}';

    // Logging
    window.__env.enableDebug = true;

}(this));
