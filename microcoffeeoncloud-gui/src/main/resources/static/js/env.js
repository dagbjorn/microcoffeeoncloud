/**
 * Contains environment specific configuration of the application.
 *
 * Assigns __env to the root window object.
 */
(function(window) {
    window.__env = window.__env || {};

    // REST services (http)
//    window.__env.locationServiceUrl = 'http://192.168.99.100:8081';
//    window.__env.menuServiceUrl = 'http://192.168.99.100:8082';
//    window.__env.orderServiceUrl = 'http://192.168.99.100:8082';

    // REST services (https)
//    window.__env.locationServiceUrl = 'https://192.168.99.100:8444';
//    window.__env.menuServiceUrl = 'https://192.168.99.100:8445';
//    window.__env.orderServiceUrl = 'https://192.168.99.100:8445';

    // REST services (http)
    window.__env.locationServiceUrl = '${location.endpointurl.http}';
    window.__env.menuServiceUrl = '${menu.endpointurl.http}';
    window.__env.orderServiceUrl = '${order.endpointurl.http}';

    // REST services (https)
    window.__env.locationServiceUrl = '${location.endpointurl.https}';
    window.__env.menuServiceUrl = '${menu.endpointurl.https}';
    window.__env.orderServiceUrl = '${order.endpointurl.https}';

    // Logging
    window.__env.enableDebug = true;

}(this));
