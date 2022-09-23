package com.frizo.lab.controller;

import com.frizo.lab.service.AsyncService;
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

    @RequestMapping(value = "/test")
    public String parseData() {
        Future<String> future1 = asyncService.configName();
        Future<String> future2 = asyncService.noConfigName();
//        String str1 = future1.get();//阻塞
//        String str2 = future2.get();//阻塞
//        Map<String, String> result = new HashMap<>();
//        result.put("asyncService.configName", str1);
//        result.put("asyncService.noConfigName", str2);
        return new Gson().toJson("result");
    }

}
