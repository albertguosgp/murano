package com.murano.oms.model.state;

import com.murano.oms.model.Order;
import com.murano.oms.model.OrderState;

public class BustedOrder implements OrderState {
    private Order order;

    @Override
    public void stateCheck() {
        order.setOrderState(new NewOrder());
    }
}
