# State Machine

<br>

----

<br>


## 定義基本狀態與 Event:

State:
```java
public enum OrderStatus {
    // 待支付，待发货，待收货，已完成
    WAIT_PAYMENT(1, "wait payment"),
    WAIT_DELIVER(2, "wait deliver"),
    WAIT_RECEIVE(3, "wait receive"),
    FINISH(4, "finish");

    private Integer key;
    private String desc;

    OrderStatus(Integer key, String desc) {
        this.key = key;
        this.desc = desc;
    }
    public Integer getKey() {
        return key;
    }
    public String getDesc() {
        return desc;
    }
    public static OrderStatus getByKey(Integer key) {
        for (OrderStatus e : values()) {
            if (e.getKey().equals(key)) {
                return e;
            }
        }
        throw new RuntimeException("enum not exists.");
    }
}
```

<br>

Event:

```java
public enum OrderStatusChangeEvent {
    PAYED, DELIVERY, RECEIVED;
}
```

<br>

定義狀態機持久化：

```java
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
```

<br>

定義狀態機的狀態切換 flow （Event 驅動狀態切換）：

```java
package com.frizo.lab.config;

import com.frizo.lab.enums.OrderStatus;
import com.frizo.lab.enums.OrderStatusChangeEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachine(name = "orderStateMachine")
@AllArgsConstructor
public class OrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderStatus, OrderStatusChangeEvent> {

    private final MyOrderStateMachinePersist orderStateMachinePersist;

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderStatusChangeEvent> states) throws Exception {
        states.withStates()
                .initial(OrderStatus.WAIT_PAYMENT)
                .states(EnumSet.allOf(OrderStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderStatusChangeEvent> transitions) throws Exception {
        transitions
                // EVENT - PAYED: WAIT_PAYMENT -> WAIT_DELIVER
                .withExternal().source(OrderStatus.WAIT_PAYMENT).target(OrderStatus.WAIT_DELIVER).event(OrderStatusChangeEvent.PAYED)
                .and()
                // EVENT - DELIVERY: WAIT_DELIVER -> WAIT_RECEIVE
                .withExternal().source(OrderStatus.WAIT_DELIVER).target(OrderStatus.WAIT_RECEIVE).event(OrderStatusChangeEvent.DELIVERY)
                .and()
                // EVENT - RECEIVED: WAIT_RECEIVE -> FINISH
                .withExternal().source(OrderStatus.WAIT_RECEIVE).target(OrderStatus.FINISH).event(OrderStatusChangeEvent.RECEIVED);
    }

    @Bean(name = "stateMachineMemPersister")
    public StateMachinePersister getPersister() {
        return new DefaultStateMachinePersister(orderStateMachinePersist);
    }

}
```

<br>

定義 Event 發生時的監聽器：

__要特別注意這邊，如果監聽事件的邏輯中出現 Error，無論拋不拋出，外部的 Event Publish 邏輯都會往下走，監聽器內部發生的錯誤不會影響到外部邏輯，所以不要把一些狀態切換時需要處理的重要業務邏輯放到監聽器中，例如當訂單確認收款後，需要做匯款分帳之類的行為，千萬不要放在監聽器中完成．這類的重要邏輯請提到主流程中．__

```java
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

```

<br>

主流程邏輯：

OrderService:

```java
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

```

