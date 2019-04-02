(function(){

    /***************************************************************************
     * Set environment values.
     */

    var env = {};

    if (window) {
        Object.assign(env, window.__env);
    }

    /***************************************************************************
     * Define application.
     */
    var coffeeApp = angular.module('coffeeApp', ['ngResource', 'ui.bootstrap']);

    /***************************************************************************
     * Make environment variables available in Angular.
     */

    coffeeApp.constant('__env', env);

    function logEnvironment($log, env) {
        $log.debug('Environment variables: ', env);
    }

    logEnvironment.$inject = ['$log', '__env'];

    coffeeApp.run(logEnvironment);

    /***************************************************************************
     * Configure logging.
     */

    function configureLogging($logProvider) {
        $logProvider.debugEnabled(env.enableDebug);
    }

    configureLogging.$inject = ['$logProvider'];

    coffeeApp.config(configureLogging);

    /***************************************************************************
     * Log bootstrap message.
     */

    function confirmBootstrap($log) {
        $log.debug('Angular bootstrapped!')
    }

    confirmBootstrap.$inject = ['$log'];

    coffeeApp.run(confirmBootstrap);

    /***************************************************************************
     * Define factories and controllers.
     */

    coffeeApp.factory('CoffeeMenu', function ($resource, __env) {
        return $resource('/api/coffeeshop/menu');
    });

    coffeeApp.factory('CoffeeOrder', function ($resource, __env) {
        return $resource('/api/coffeeshop/:id/order', {id: '@CoffeeShopId'}, {});
    });

    coffeeApp.controller('DrinksController', function ($scope, CoffeeMenu, CoffeeOrder, $log) {
        $scope.types = [];
        $scope.sizes = [];
        $scope.availableOptions = [];

        $scope.messages = [];

        $scope.initCoffeeMenu = function () {
            CoffeeMenu.get().$promise
                .then(function (value) {
                    $scope.types = value.types;
                    $scope.sizes = value.sizes;
                    $scope.availableOptions = value.availableOptions;
                })
                .catch(function (value) {
                    $log.debug('Failed to load coffee menu', value);
                    alert('Failed to load coffee menu. Is backend service running?');
                });
        };

        $scope.giveMeCoffee = function () {
            CoffeeOrder.save({id:1}, $scope.drink, function (order, responseHeaders) {
                $log.debug('Headers:', responseHeaders());

                var orderUrl = responseHeaders('Location');
                $scope.messages.push({type: 'success', msg: 'Order sent!', orderId: order.id, coffeeShopId: order.coffeeShopId,
                        orderUrl: orderUrl});
            }, function (response) {
                $log.debug('Failed to order coffee: ', response);

                if (response.status === 402) {
                    alert('Sorry, too low credit rating to order!');
                } else {
                    alert(response.status + ' ' + response.statusText);
                }
            });
        };

        $scope.closeAlert = function (index) {
            $scope.messages.splice(index, 1);
        };

        $scope.addOption = function () {
            if (!$scope.drink.selectedOptions) {
                $scope.drink.selectedOptions = [];
            }
            $scope.drink.selectedOptions.push($scope.newOption);
            $scope.newOption = '';
        }

        init();

        function init() {
            $scope.initCoffeeMenu();
        }
    });

    coffeeApp.factory('CoffeeShopLocator', function ($resource, __env) {
        return $resource('/api/coffeeshop/nearest/:latitude/:longitude/:maxdistance',
                {latitude: '@latitude', longitude: '@longitude', maxdistance: '@maxdistance'}, {});
    });

    coffeeApp.controller('CoffeeShopController', function ($scope, $window, CoffeeShopLocator, $log) {
        const maxDistance = 2500; // Max distance to coffee shop in meters

        $scope.findCoffeeShopNearestToMe = function () {
            window.navigator.geolocation.getCurrentPosition(function (position) {
                $scope.getCoffeeShopAt(position.coords.latitude, position.coords.longitude)
            }, null);
        };

        $scope.getCoffeeShopAt = function (latitude, longitude) {
            CoffeeShopLocator.get({latitude: latitude, longitude: longitude, maxdistance: maxDistance}).$promise
                .then(function (value) {
                    $log.debug('Found coffee shop within ' + maxDistance + ' meters distance, name=', value.name);
                    $scope.nearestCoffeeShop = value;
                })
                .catch(function (value) {
                    $log.debug('Failed to find coffee shop', value);
                    alert('No coffee shop found within ' + maxDistance + ' meters distance!');
                });
        };
    });
})()
