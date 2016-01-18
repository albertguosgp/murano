package com.murano.oms.model;

import java.util.List;

public interface Order {
    OrderId getId();

    Symbol getSymbol();

    Initiator getInitiator();

    Acceptor getAcceptor();

    OrderState getOrderState();

    void setOrderState(OrderState orderState);

    void handleEvent(OrderEvent orderEvent);

    List<Execution> getExecutions();
}
