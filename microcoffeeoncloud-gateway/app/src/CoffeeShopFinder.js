import React, { useState } from 'react';
import { Button, Container } from 'reactstrap';
import { config } from './EnvConfig';

const CoffeeShopLocation = props => {
    const debug = false;

    const mapUrl = `https://maps.google.com?q=${props.coffeeShop.name}&ll=${props.coffeeShop.location.coordinates[1]},${props.coffeeShop.location.coordinates[0]}`;

    return (
        <span>
            {' '}<strong>{props.coffeeShop.name}</strong>
            {debug && <strong>{' '}latitude: {props.coffeeShop.location.coordinates[1]}</strong>}
            {debug && <strong>{' '}longitude: {props.coffeeShop.location.coordinates[0]}</strong>}
            {' '}<a target="_blank" rel="noreferrer" href={mapUrl}>Map</a>
        </span>
    );
};

const CoffeeShopFinder = () => {

    const maxDistance = 2500; // Max distance to coffee shop in meters

    const [nearestCoffeeShop, setNearestCoffeeShop] = useState(null);

    const findCoffeeShopNearestToMe = () => {
        window.navigator.geolocation.getCurrentPosition(position => {
            getCoffeeShopAt(position.coords.latitude, position.coords.longitude);
        }, null);
    };

    const getCoffeeShopAt = (latitude, longitude) => {
        console.log('latitude=' + latitude + ', longitude=' + longitude);
        console.log('NODE_ENV=' + process.env.NODE_ENV);
        console.log('LOCATION_BASE_URL=' + config.url.LOCATION_BASE_URL);

        fetch(`${config.url.LOCATION_BASE_URL}/api/coffeeshop/nearest/${latitude}/${longitude}/${maxDistance}`, {
            method: 'GET'
        })
            .then(handleResponse)
            .then(data => {
                console.log('nearestCoffeeShop=' + JSON.stringify(data));
                setNearestCoffeeShop(data);
            })
            .catch(value => {
                console.log('Failed to find coffee shop;', value);
                alert('Error when trying to find coffee shop.\n\n' + value);
            });
    };

    const handleResponse = (response) => {
        if (!response.ok) {
            throw Error(response.statusText);
        } else if (response.status === 204) {
            alert('No coffee shop found within ' + maxDistance + ' meters distance');
        } else {
            return response.json();
        }
    };

    return (
        <div>
            <Container fluid>
                <Button color="primary" onClick={findCoffeeShopNearestToMe}>Find my coffee shop</Button>
                {nearestCoffeeShop && <CoffeeShopLocation coffeeShop={nearestCoffeeShop} />}
            </Container>
        </div>
    );
};

export default CoffeeShopFinder;
