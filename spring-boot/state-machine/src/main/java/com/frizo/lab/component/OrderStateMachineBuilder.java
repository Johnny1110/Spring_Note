package com.frizo.lab.component;

import com.frizo.lab.enums.OrderStatus;
import com.frizo.lab.enums.OrderStatusChangeEvent;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
@AllArgsConstructor
public class OrderStateMachineBuilder {
    
    private final static String MACHINEID = "orderMachine";

    private final BeanFactory beanFactory;

    public StateMachine<OrderStatus, OrderStatusChangeEvent> build(OrderStatus orderStatus) throws Exception {
        StateMachineBuilder.Builder<OrderStatus, OrderStatusChangeEvent> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
                .withConfiguration()
                .machineId(MACHINEID)
                .beanFactory(beanFactory);

        builder.configureStates()
                .withStates()
                .initial(orderStatus)
                .states(EnumSet.allOf(OrderStatus.class));

        builder.configureTransitions()
                // EVENT - PAYED: WAIT_PAYMENT -> WAIT_DELIVER
                .withExternal().source(OrderStatus.WAIT_PAYMENT).target(OrderStatus.WAIT_DELIVER).event(OrderStatusChangeEvent.PAYED)
                .and()
                // EVENT - DELIVERY: WAIT_DELIVER -> WAIT_RECEIVE
                .withExternal().source(OrderStatus.WAIT_DELIVER).target(OrderStatus.WAIT_RECEIVE).event(OrderStatusChangeEvent.DELIVERY)
                .and()
                // EVENT - RECEIVED: WAIT_RECEIVE -> FINISH
                .withExternal().source(OrderStatus.WAIT_RECEIVE).target(OrderStatus.FINISH).event(OrderStatusChangeEvent.RECEIVED);

        return builder.build();
    }
}
