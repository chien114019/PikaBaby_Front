package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Receivable;
import com.example.demo.model.SalesOrder;
import com.example.demo.model.SalesOrderDetail;
import com.example.demo.model.ShippingOrder;
import com.example.demo.model.ShippingOrderDetail;

import com.example.demo.repository.ShippingOrderRepository;
import com.example.demo.repository.ReceivableRepository;
import com.example.demo.repository.SalesOrderRepository;

@Service
public class ShippingOrderService {

    @Autowired
    private ShippingOrderRepository shippingOrderRepository;

    @Autowired
    private ReceivableRepository receivableRepository;

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    public List<ShippingOrder> findAll() {
        return shippingOrderRepository.findAll();
    }

    public ShippingOrder findById(Integer id) {
        return shippingOrderRepository.findById(id).orElse(null);
    }

    /**
     * 建立出貨單
     */
    public ShippingOrder createFromSalesOrder(SalesOrder salesOrder) {
        ShippingOrder shippingOrder = new ShippingOrder();
        shippingOrder.setSalesOrder(salesOrder);
        shippingOrder.setShippingDate(LocalDate.now());
        shippingOrder.setStatus("待出貨");

        // 複製明細
        for (SalesOrderDetail detail : salesOrder.getDetails()) {
            ShippingOrderDetail shipDetail = new ShippingOrderDetail();
            shipDetail.setShippingOrder(shippingOrder);
            shipDetail.setProduct(detail.getProduct());
            shipDetail.setQuantity(detail.getQuantity());
            shippingOrder.getDetails().add(shipDetail);
        }

        return shippingOrderRepository.save(shippingOrder);
    }

    /**
     * 標記為已出貨，並建立應收帳款
     */
    public void markAsShipped(Integer shippingOrderId) {
        ShippingOrder order = shippingOrderRepository.findById(shippingOrderId).orElseThrow();
        if (!"已出貨".equals(order.getStatus())) {
            order.setStatus("已出貨");
            shippingOrderRepository.save(order);

            // 建立應收帳款
            Receivable rc = new Receivable();
            rc.setCustomer(order.getSalesOrder().getCustomer());
            rc.setAmount(order.getSalesOrder().getTotalAmount());
            rc.setDueDate(LocalDate.now().plusDays(30));
            rc.setOrder(order.getSalesOrder());
            receivableRepository.save(rc);
        }
    }
    
    public void markAsUnshipped(Integer id) {
        ShippingOrder order = shippingOrderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("找不到出貨單 ID: " + id));
        order.setStatus("待出貨");
        shippingOrderRepository.save(order);
    }

    
    public List<SalesOrder> findSalesOrdersWithoutShipping() {
        List<SalesOrder> allSalesOrders = salesOrderRepository.findAll();
        List<ShippingOrder> allShippingOrders = shippingOrderRepository.findAll();

        // 收集已經有出貨單的 salesOrder ID
        Set<Integer> shippedSalesOrderIds = allShippingOrders.stream()
            .map(order -> order.getSalesOrder().getId())
            .collect(Collectors.toSet());

        return allSalesOrders.stream()
            .filter(order -> !shippedSalesOrderIds.contains(order.getId()))
            .collect(Collectors.toList());
    }

}
