import React, { useEffect, useState } from 'react';
import { Button, Container, Form, FormGroup, Input, Label } from 'reactstrap';
import { OrderLine } from './OrderConfirmation';
import { config } from './EnvConfig';

const PrintJson = ({ data }) => (<div><pre>{JSON.stringify(data, null, 2)}</pre></div>);

const CoffeeOrder = () => {
    const debug = false;

    const emptyOrder = {
        type: {
            name: '',
            family: ''
        },
        size: '',
        drinker: '',
        selectedOptions: []
    };

    const [order, setOrder] = useState(emptyOrder);
    const [drinks, setDrinks] = useState([]);
    const [sizes, setSizes] = useState([]);
    const [availableOptions, setAvailableOptions] = useState([]);
    const [displayedOption, setDisplayedOption] = useState('');
    const [orderList, setOrderList] = useState([]);

    useEffect(() => {
        console.log('NODE_ENV=' + process.env.NODE_ENV);
        console.log('MENU_BASE_URL=' + config.url.MENU_BASE_URL);

        fetch(`${config.url.MENU_BASE_URL}/api/coffeeshop/menu`)
            .then(response => response.json())
            .then(data => {
                console.log('menu=' + JSON.stringify(data));
                setDrinks(data.types.sort((a, b) => a.name.localeCompare(b.name)));
                setSizes(data.sizes.map(size => size.name));
                setAvailableOptions(data.availableOptions.map(option => option.name).sort())
            })
            .catch(value => {
                console.log('Failed to load coffee menu', value);
                alert('Failed to load coffee menu. Is backend service running?\n\n' + value);
            });

    }, []);

    const handleSubmit = (event) => {
        event.preventDefault();

        fetch(`${config.url.ORDER_BASE_URL}/api/coffeeshop/1/order`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(order)
        })
            .then(response => response.json().then(json => ({
                ok: response.ok,
                headers: response.headers,
                status: response.status,
                statusText: response.statusText,
                json
            })
            ))
            .then(({ ok, headers, status, statusText, json }) => (
                handleResponse(ok, headers, status, statusText, json)
            ))
            .then(orderConfirmation => {
                console.log('orderConfirmation=' + JSON.stringify(orderConfirmation));
                setOrderList([...orderList, orderConfirmation]);
            })
            .catch(value => {
                console.log('Failed to order coffee;', value);
                alert('Failed to order coffee.\n\n' + value);
            });
    };

    const handleResponse = (ok, headers, status, statusText, json) => {
        if (!ok) {
            throw Error(statusText);
        } else if (status === 402) {
            alert('Sorry, too low credit rating to order!');
        } else {
            let orderConfirmation = json;
            let orderUrl = headers.get('Location');
            return {
                key: orderConfirmation.id,
                type: 'success',
                msg: 'Order sent!',
                orderId: orderConfirmation.id,
                coffeeShopId: orderConfirmation.coffeeShopId,
                orderUrl: orderUrl
            };
        }
    };

    const handleChange = (event) => {
        const { name, value } = event.target;
        console.log('handleChange: name=' + name + ', value=' + value);
        setOrder({ ...order, [name]: value });
    };

    const handleChangeInDrink = (event) => {
        const { name, value } = event.target;
        console.log('handleChangeInDrink: name=' + name + ', value=' + value);
        setOrder({ ...order, type: drinks.find(e => e.name === value) });
    };

    const handleChangeInOptions = (event) => {
        const { name, value } = event.target;
        console.log('handleChangeInOptions: name=' + name + ', value=' + value);
        setDisplayedOption(value);

        if (availableOptions.includes(value) && !order.selectedOptions.includes(value)) {
            order.selectedOptions.push(value);
            setOrder({ ...order, "selectedOptions": order.selectedOptions });
            setDisplayedOption('');
        }
    };

    const handleClickInOptions = () => {
        setDisplayedOption('');
    };

    return (
        <div>
            <Container fluid>
                <Form onSubmit={handleSubmit}>
                    <FormGroup>
                        <Label for="type">Drink</Label>
                        <Input type="select" name="type" id="type" value={order.type.name}
                            onChange={handleChangeInDrink} autoComplete="type">
                            <option key="none" value="none" hidden="hidden"></option>
                            {drinks.map(drink => <option key={drink.name} value={drink.name}>{drink.name}</option>)}
                        </Input>
                    </FormGroup>
                    <FormGroup>
                        <Label for="size">Size</Label>
                        <Input type="select" name="size" id="size" value={order.size}
                            onChange={handleChange} autoComplete="size">
                            <option key="none" value="none" hidden="hidden"></option>
                            {sizes.map(size => <option key={size} value={size}>{size}</option>)}
                        </Input>
                    </FormGroup>
                    <FormGroup>
                        <Label for="drinker">Your name</Label>
                        <Input name="drinker" id="drinker" value={order.drinker}
                            onChange={handleChange} autoComplete="drinker">
                        </Input>
                    </FormGroup>
                    <FormGroup>
                        <Label for="options">Options</Label>
                        <Input type="search" name="options" id="options" value={displayedOption} list="optionlist"
                            onChange={handleChangeInOptions} onClick={handleClickInOptions}
                            placeholder="Type milk or syrups or extras here">
                        </Input>
                        <datalist id="optionlist">
                            {availableOptions.map(option => <option key={option} value={option}>{option}</option>)}
                        </datalist>
                    </FormGroup>
                    <FormGroup>
                        {order.selectedOptions.map(option => <span key={option}>{option}; </span>)}
                    </FormGroup>
                    <FormGroup>
                        <Button type="submit" color="primary">Give Me Coffee</Button>
                    </FormGroup>
                </Form>

                {orderList && orderList.map(order => <OrderLine key={order.id} order={order} />)}
                {debug && <PrintJson data={order} />}
            </Container>
        </div>
    );
};

export default CoffeeOrder;
