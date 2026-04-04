import './App.css';
import AppNavbar from './AppNavbar';
import { Container } from 'react-bootstrap';
import CoffeeShopFinder from './CoffeeShopFinder';
import CoffeeOrder from './CoffeeOrder';

const Home = () => {

    return (
        <div>
            <AppNavbar />
            <Container fluid>
                <CoffeeShopFinder />
                <hr />
                <CoffeeOrder />
            </Container>
        </div>
    );
};

export default Home;
