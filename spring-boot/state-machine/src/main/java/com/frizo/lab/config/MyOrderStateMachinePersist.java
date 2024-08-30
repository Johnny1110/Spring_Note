package com.frizo.lab.config;

import com.frizo.lab.enums.OrderStatus;
import com.frizo.lab.mapper.OrderMapper;
import com.frizo.lab.model.Order;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class MyOrderStateMachinePersist implements StateMachinePersist<Object, Object, Order> {

    private final OrderMapper orderMapper;

    @Override
    public void write(StateMachineContext<Object, Object> stateMachineContext, Order order) {
        orderMapper.updateById(order);
    }

    @Override
    public StateMachineContext read(Order order) {
        OrderStatus status = OrderStatus.getByKey(order.getStatus());
        return new DefaultStateMachineContext(status, null, null, null);
    }
}
