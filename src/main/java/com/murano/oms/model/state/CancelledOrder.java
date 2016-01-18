package com.murano.oms.model.state;

import com.murano.oms.model.Order;
import com.murano.oms.model.OrderState;

/**
 * Created by chenguo on 22/12/15.
 */
public class CancelledOrder implements OrderState {
    private Order order;

    @Override
    public void stateCheck() {

    }
}
