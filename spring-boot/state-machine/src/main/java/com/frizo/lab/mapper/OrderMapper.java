package com.frizo.lab.mapper;

import com.frizo.lab.model.Order;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OrderMapper {

    private static final Map<Long, Order> orderMap = new ConcurrentHashMap<>();

    public void insert(Order order) {
        orderMap.put(order.getId(), order);
    }

    public Order selectById(Long id) {
        return orderMap.get(id);
    }

    public void updateById(Order order) {
        orderMap.put(order.getId(), order);
    }
}
