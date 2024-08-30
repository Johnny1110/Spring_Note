package com.frizo.lab.model;

import com.frizo.lab.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class Order {

    private Long id; // 主键ID
    private String orderCode; // 订单编码
    private Integer status; // 订单状态
    private String name; // 订单名称
    private BigDecimal price; // 价格
    private Integer deleteFlag; // 删除标记，0未删除 1已删除
    private Instant createTime; // 创建时间
    private Instant updateTime; // 更新时间
    private String createUserCode; // 创建人
    private String updateUserCode; // 更新人
    private Integer version; // 版本号
    private String remark; // 备注
}
