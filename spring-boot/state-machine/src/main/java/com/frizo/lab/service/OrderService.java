package com.frizo.lab.service;

import com.frizo.lab.enums.OrderStatus;
import com.frizo.lab.enums.OrderStatusChangeEvent;
import com.frizo.lab.mapper.OrderMapper;
import com.frizo.lab.model.Order;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private StateMachine<OrderStatus, OrderStatusChangeEvent> orderStateMachine;
    private StateMachinePersister<OrderStatus, OrderStatusChangeEvent, Order> persister;

    public Order getById(Long id) {
        return orderMapper.selectById(id);
    }

    public Order create(Order order) {
        order.setStatus(OrderStatus.WAIT_PAYMENT.getKey());
        orderMapper.insert(order);
        return order;
    }

    public Order pay(Long id) {
        Order order = orderMapper.selectById(id);
        log.info("Thread name：{},try paying，orderNo：{}", Thread.currentThread().getName(), id);
        if (!sendEvent(OrderStatusChangeEvent.PAYED, order)) {
            log.error("Thread name：{},pay failed, status abnormal，order details：{}", Thread.currentThread().getName(), order);
            throw new RuntimeException("pay failed, order status abnormal");
        }
        return order;
    }

    public Order deliver(Long id) {
        Order order = orderMapper.selectById(id);
        log.info("Thread name：{},try deliver，orderNo：{}", Thread.currentThread().getName(), id);
        if (!sendEvent(OrderStatusChangeEvent.DELIVERY, order)) {
            log.error("Thread name：{},deliver failed, status abnormal，order details：{}", Thread.currentThread().getName(), order);
            throw new RuntimeException("deliver failed, order status abnormal");
        }
        return order;
    }

    public Order receive(Long id) {
        Order order = orderMapper.selectById(id);
        log.info("Thread name：{},try receive，orderNo：{}", Thread.currentThread().getName(), id);
        if (!sendEvent(OrderStatusChangeEvent.RECEIVED, order)) {
            log.error("Thread name：{},receive failed, status abnormal，order details：{}", Thread.currentThread().getName(), order);
            throw new RuntimeException("receive failed, order status abnormal");
        }
        return order;
    }

    @SneakyThrows
    private synchronized boolean sendEvent(OrderStatusChangeEvent changeEvent, Order order) {
        boolean result = false;
        try {
            // start state machine
            orderStateMachine.start();
            // restore state machine
            persister.restore(orderStateMachine, order);
            // send event
            Message message = MessageBuilder.withPayload(changeEvent).setHeader("order", order).build();
            result = orderStateMachine.sendEvent(message);
            log.info("order send event success, order details：{}", order);
            order.setStatus(orderStateMachine.getState().getId().getKey());
            persister.persist(orderStateMachine, order);

        } catch (Exception e) {
            log.error("order send event failed.", e);
        } finally {
            orderStateMachine.stop();
        }
        return result;
    }
}
