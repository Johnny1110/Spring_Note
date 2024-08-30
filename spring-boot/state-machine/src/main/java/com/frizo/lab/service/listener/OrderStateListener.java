package com.frizo.lab.service.listener;

import com.frizo.lab.enums.OrderStatusChangeEvent;
import com.frizo.lab.model.Order;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
@WithStateMachine(name = "orderStateMachine")
public class OrderStateListener {

    @OnTransition(source = "WAIT_PAYMENT", target = "WAIT_DELIVER")
    public void payTransition(Message<OrderStatusChangeEvent> message) {
        try {
            Order order = (Order) message.getHeaders().get("order");
            log.info("[OrderStateListener] payTransition -> msg header：{}", message.getHeaders());
            log.info("[OrderStateListener] payTransition -> Thread name：{}", Thread.currentThread().getName());
            try {
                Thread.sleep(3000L); // simulate business processing
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            log.error("[OrderStateListener] payTransition -> error：{}", e.getMessage());
        }
    }

    @OnTransition(source = "WAIT_DELIVER", target = "WAIT_RECEIVE")
    public void deliverTransition(Message<OrderStatusChangeEvent> message) {
        try {
            Order order = (Order) message.getHeaders().get("order");
            log.info("[OrderStateListener] deliverTransition -> msg header：{}", message.getHeaders());
        } catch (Exception e) {
            log.error("[OrderStateListener] deliverTransition -> error：{}", e.getMessage());
        }

        // TODO other business like update order details

    }

    @OnTransition(source = "WAIT_RECEIVE", target = "FINISH")
    public void receiveTransition(Message<OrderStatusChangeEvent> message) {
        try {
            Order order = (Order) message.getHeaders().get("order");
            log.info("[OrderStateListener] receiveTransition -> msg header：{}", message.getHeaders());
            throw new RuntimeException("simulate exception");
        } catch (Exception e) {
            log.error("[OrderStateListener] receiveTransition -> error：{}", e.getMessage());
        }

        // TODO other business like update order details
    }

}
