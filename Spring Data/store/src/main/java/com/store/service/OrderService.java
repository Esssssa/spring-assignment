package com.store.service;

import com.store.dto.ItemLine;
import com.store.dto.OrderSummary;
import com.store.exception.InsufficientStockException;
import com.store.exception.InvalidOrderStateException;
import com.store.exception.InvalidQuantityException;
import com.store.exception.NotFoundException;
import com.store.model.Customer;
import com.store.model.Order;
import com.store.model.OrderItem;
import com.store.model.OrderStatus;
import com.store.model.Payment;
import com.store.model.PaymentMethod;
import com.store.model.Product;
import com.store.repository.CustomerRepository;
import com.store.repository.OrderRepository;
import com.store.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final CustomerRepository customers;
    private final ProductRepository products;
    private final AuditService audit;

    public OrderService(OrderRepository orders, CustomerRepository customers, ProductRepository products, AuditService audit) {
        this.orders = orders;
        this.customers = customers;
        this.products = products;
        this.audit = audit;
    }

    @Transactional
    public Long placeOrder(Long customerId, Map<Long, Integer> productQuantities) {
        try {
            Customer customer = customers.findById(customerId).orElseThrow(() -> new NotFoundException("Customer", customerId));
            Order order = new Order(customer);
            for (Map.Entry<Long, Integer> line : productQuantities.entrySet()) {
                Product product = products.findById(line.getKey()).orElseThrow(() -> new NotFoundException("Product", line.getKey()));
                int quantity = line.getValue();
                if (quantity <= 0) {
                    throw new InvalidQuantityException(quantity);
                }
                if (product.getStock() < quantity) {
                    throw new InsufficientStockException(product.getSku(), quantity, product.getStock());
                }
                product.setStock(product.getStock() - quantity);
                order.addItem(new OrderItem(product, quantity, product.getPrice()));
            }
            orders.save(order);
            return order.getId();
        } catch (RuntimeException e) {
            audit.record("placeOrder failed for customer " + customerId + ": " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void pay(Long orderId, PaymentMethod method) {
        Order order = load(orderId);
        if (order.getStatus() != OrderStatus.NEW) {
            throw new InvalidOrderStateException("Only a NEW order can be paid but order " + orderId + " is " + order.getStatus());
        }
        order.setPayment(new Payment(order.getTotal(), method));
        order.setStatus(OrderStatus.PAID);
    }

    @Transactional
    public void ship(Long orderId) {
        Order order = load(orderId);
        if (order.getStatus() != OrderStatus.PAID) {
            throw new InvalidOrderStateException("Only a PAID order can be shipped but order " + orderId + " is " + order.getStatus());
        }
        order.setStatus(OrderStatus.SHIPPED);
    }

    @Transactional
    public void cancel(Long orderId) {
        Order order = load(orderId);
        if (order.getStatus() != OrderStatus.NEW && order.getStatus() != OrderStatus.PAID) {
            throw new InvalidOrderStateException("Order " + orderId + " is " + order.getStatus() + " and cannot be cancelled");
        }
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);
    }

    @Transactional(readOnly = true)
    public OrderSummary getOrderSummary(Long orderId) {
        Order order = orders.findByIdWithItems(orderId).orElseThrow(() -> new NotFoundException("Order", orderId));
        return toSummary(order);
    }

    @Transactional(readOnly = true)
    public OrderSummary getOrderSummaryWithoutFetch(Long orderId) {
        return toSummary(load(orderId));
    }

    private Order load(Long orderId) {
        return orders.findById(orderId).orElseThrow(() -> new NotFoundException("Order", orderId));
    }

    private OrderSummary toSummary(Order order) {
        List<ItemLine> lines = order.getItems().stream()
                .map(i -> new ItemLine(i.getProduct().getSku(), i.getQuantity(), i.getUnitPrice()))
                .toList();
        return new OrderSummary(order.getId(), order.getCustomer().getName(), order.getStatus(), order.getTotal(), lines);
    }
}
