package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Receivable;
import com.example.demo.model.SalesOrder;
import com.example.demo.model.ShippingOrder;
import com.example.demo.repository.ReceivableRepository;
import com.example.demo.repository.SalesOrderRepository;
import com.example.demo.repository.ShippingOrderRepository;

@Service
public class OrderStatusService {

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private ReceivableRepository receivableRepository;

    @Autowired
    private ShippingOrderRepository shippingOrderRepository;

    public void updatePayStatus(Integer orderId, Integer payStatus) {
        SalesOrder order = salesOrderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setPayStatus(payStatus);
            salesOrderRepository.save(order);
            System.out.println("✅ 訂單 " + orderId + " 付款狀態已更新為：" + payStatus);

            // 同步更新應收帳款
            Receivable receivable = receivableRepository.findByOrder(order);
            if (receivable != null) {
                receivable.setStatus("已收款");
                receivableRepository.save(receivable);
            }

            // 同步出貨單
            ShippingOrder shippingOrder = shippingOrderRepository.findBySalesOrder(order);
            if (shippingOrder != null) {
                shippingOrder.setStatus("待出貨");
                shippingOrderRepository.save(shippingOrder);
            }

        } else {
            throw new IllegalArgumentException("找不到訂單 ID：" + orderId);
        }
    }
}

