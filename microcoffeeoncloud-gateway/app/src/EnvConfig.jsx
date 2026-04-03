/**
 * NODE_ENV values set by npm:
 *
 * npm start     => development
 * npm test      => test
 * npm run build => production
 *
 * Source: https://create-react-app.dev/docs/adding-custom-environment-variables/
 */
const npmdev = {
    url: {
        LOCATION_BASE_URL: 'http://localhost:8081',
        MENU_BASE_URL: 'http://localhost:8082',
        ORDER_BASE_URL: 'http://localhost:8082'
    }
};

const otherenv = {
    url: {
        LOCATION_BASE_URL: '',
        MENU_BASE_URL: '',
        ORDER_BASE_URL: ''
    }
};

export const config = process.env.NODE_ENV === 'development' ? npmdev : otherenv;
