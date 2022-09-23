package com.frizo.lab.service;

import com.frizo.lab.entity.DeliverItemOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeliverOrderServiceImpl implements DeliverOrderService {

    private static Logger logger = LoggerFactory.getLogger(AsyncService.class);

    public String deliverItemOrder(DeliverItemOrder deliverItemOrder){
        try {
            logger.info(String.format("Thread:[%s] delivering itemOrder[%s]...", Thread.currentThread().getName(), deliverItemOrder.getId()));
            Thread.sleep(5000L); // 模擬耗時操作
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info(String.format("Thread:[%s] deliver itemOrder[%s] done", Thread.currentThread().getName(), deliverItemOrder.getId()));
        return "SUCCESS";
    }

    /**
     * 出貨
     * @param orderNo
     * @param deliverOrderNo
     * @return OrderStatus
     */
    @Override
    @Async("async_executor")//指定配置的 executor Bean
    public void deliverOrder(String orderNo, String deliverOrderNo){
        try {
            // 建立品項出貨單
            List<DeliverItemOrder> deliverItemOrders = createDeliverItemOrders(deliverOrderNo);

            boolean allPass = true;

            // 品項一個一個出貨
            for (DeliverItemOrder deliverItemOrder : deliverItemOrders) {
                String status = deliverItemOrder(deliverItemOrder);
                if (!status.equals("SUCCESS")){
                    allPass = false;
                }
            }

            if (allPass){
                //TODO 回押修改 deliverOrder 狀態為 Success
                callbackOrder(orderNo, deliverOrderNo, "ORDER_DELIVER_SUCCESS");
            }else {
                //TODO 修改 deliverOrder 狀態為 Retry
            }
        }catch (Exception e){
            //TODO 修改 deliverOrder 狀態為 Retry
        }
    }

    private void callbackOrder(String orderNo, String deliverOrderNo, String deliverStatus) {
        System.out.println("deliverOrder result: " + deliverStatus);
    }

    private List<DeliverItemOrder> createDeliverItemOrders(String deliverOrderNo) {
        List<DeliverItemOrder> deliverItemOrders = new ArrayList<>();
        deliverItemOrders.add(new DeliverItemOrder(1));
        deliverItemOrders.add(new DeliverItemOrder(2));
        deliverItemOrders.add(new DeliverItemOrder(3));
        return deliverItemOrders;
    }
}
