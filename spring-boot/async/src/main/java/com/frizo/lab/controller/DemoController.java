package com.frizo.lab.controller;

import com.frizo.lab.service.AsyncService;
import com.frizo.lab.service.DeliverOrderService;
import com.frizo.lab.service.OrderService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping(value = "/rest")
public class DemoController {

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/test")
    public String parseData() {
        asyncService.configName();
        Future<String> future2 = asyncService.noConfigName();
        return new Gson().toJson("result");
    }

    @RequestMapping(value = "/deliverOrder")
    public Map<String, Object> deliverOrder() {
        String status = orderService.callDeliver("GM001", "MA0001");
        Map<String, Object> result = new HashMap<>();
        result.put("code", "00000");
        result.put("msg", "success");
        result.put("data", status);
        return result;
    }

}
