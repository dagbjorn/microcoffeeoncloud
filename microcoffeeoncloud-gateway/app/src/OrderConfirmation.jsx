import React from 'react';

const OrderLine = (props) => {
    return (
        <div>
            <span>{props.order.msg} <a target="_blank" rel="noreferrer" href={props.order.orderUrl}>Order details</a></span>
        </div>
    );
};

export { OrderLine }
