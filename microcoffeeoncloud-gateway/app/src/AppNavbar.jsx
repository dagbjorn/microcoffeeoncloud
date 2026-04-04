import React from 'react';
import { Navbar, Container, Nav } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const AppNavbar = () => {
    return (
        <Navbar bg="dark" variant="dark" expand="md">
            <Container fluid className="px-4">
                <Navbar.Brand as={Link} to="/">Microcoffee</Navbar.Brand>
                <Navbar.Toggle />
                <Navbar.Collapse className="justify-content-end">
                    <Nav>
                        <Nav.Link href="https://github.com/dagbjorn/microcoffeeoncloud" target="_blank">GitHub</Nav.Link>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default AppNavbar;
