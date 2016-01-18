package com.murano.oms.model;

import java.util.List;


public abstract class AbstractOrder implements Order {
    private OrderState orderState;
    private List<Execution> executions;

    @Override

    public OrderId getId() {
        return null;
    }

    @Override
    public Symbol getSymbol() {
        return null;
    }

    @Override
    public Initiator getInitiator() {
        return null;
    }

    @Override
    public Acceptor getAcceptor() {
        return null;
    }

    @Override
    public OrderState getOrderState() {
        return orderState;
    }

    @Override
    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    @Override
    public List<Execution> getExecutions() {
        return executions;
    }

    @Override
    public void handleEvent(OrderEvent orderEvent) {
    }
}
