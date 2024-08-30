package com.frizo.lab.controller;

import com.frizo.lab.model.Order;
import com.frizo.lab.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/getById")
    public Order getById(@RequestParam("id") Long id) {
        return orderService.getById(id);
    }

    @PostMapping("/create")
    public String create(@RequestBody Order order) {
        orderService.create(order);
        return "success";
    }

    @GetMapping("/pay")
    public String pay(@RequestParam("id") Long id) {
        orderService.pay(id);
        return "success";
    }

    @GetMapping("/deliver")
    public String deliver(@RequestParam("id") Long id) {
        orderService.deliver(id);
        return "success";
    }

    @GetMapping("/receive")
    public String receive(@RequestParam("id") Long id) {
        orderService.receive(id);
        return "success";
    }

}
