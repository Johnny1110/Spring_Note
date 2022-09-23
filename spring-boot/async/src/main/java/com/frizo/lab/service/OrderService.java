package com.frizo.lab.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private DeliverOrderService deliverOrderService;

    public String callDeliver(String orderNo, String deliverOrderNo){
        deliverOrderService.deliverOrder(orderNo, deliverOrderNo);
        return "正在配送商品..";
    }

}
