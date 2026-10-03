package com.store;

import com.store.config.JpaConfig;
import com.store.config.QueryStatistics;
import com.store.model.Address;
import com.store.model.Customer;
import com.store.model.PaymentMethod;
import com.store.model.Product;
import com.store.repository.AuditLogRepository;
import com.store.repository.ProductRepository;
import com.store.service.CustomerService;
import com.store.service.OrderService;
import com.store.service.ProductService;
import com.store.service.ReportService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JpaConfig.class)) {
            CustomerService customerService = context.getBean(CustomerService.class);
            ProductService productService = context.getBean(ProductService.class);
            OrderService orderService = context.getBean(OrderService.class);
            ReportService reportService = context.getBean(ReportService.class);
            ProductRepository productRepository = context.getBean(ProductRepository.class);
            AuditLogRepository auditLogs = context.getBean(AuditLogRepository.class);
            QueryStatistics statistics = context.getBean(QueryStatistics.class);

            Customer ahmed = customerService.register("Ahmed", "ahmed@mail.com", new Address("1 Nile St", "Cairo", "Egypt"));
            Customer sara = customerService.register("Sara", "sara@mail.com", new Address("5 Corniche", "Alexandria", "Egypt"));
            Customer omar = customerService.register("Omar", "omar@mail.com", new Address("9 Palm Rd", "Giza", "Egypt"));

            Product laptop = productService.addProduct("LAP-1", "Laptop", new BigDecimal("1200.00"), 5, "Electronics", "Computers");
            Product mouse = productService.addProduct("MOU-1", "Wireless Mouse", new BigDecimal("25.00"), 50, "Electronics", "Accessories");
            Product desk = productService.addProduct("DSK-1", "Standing Desk", new BigDecimal("300.00"), 10, "Furniture");
            Product lamp = productService.addProduct("LMP-1", "Desk Lamp", new BigDecimal("40.00"), 2, "Furniture", "Accessories");
            Product pen = productService.addProduct("PEN-1", "Gel Pen", new BigDecimal("2.00"), 500, "Stationery");

            System.out.println("\n=== Duplicate email ===");
            try {
                customerService.register("Ahmed Again", "ahmed@mail.com", null);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }

            System.out.println("\n=== Orders ===");
            Long first = orderService.placeOrder(ahmed.getId(), items(laptop, 1, mouse, 2));
            Long second = orderService.placeOrder(sara.getId(), items(desk, 2, lamp, 1));
            Long third = orderService.placeOrder(omar.getId(), items(mouse, 5));
            orderService.pay(first, PaymentMethod.CARD);
            orderService.pay(second, PaymentMethod.WALLET);
            orderService.ship(first);
            System.out.println(orderService.getOrderSummary(first));
            System.out.println(orderService.getOrderSummary(second));

            System.out.println("\n=== Rules ===");
            try {
                orderService.ship(third);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
            try {
                orderService.pay(first, PaymentMethod.CASH);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }

            System.out.println("\n=== All or nothing and the audit log ===");
            int stockBefore = productRepository.findById(mouse.getId()).orElseThrow().getStock();
            try {
                orderService.placeOrder(omar.getId(), items(mouse, 3, lamp, 10));
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
            int stockAfter = productRepository.findById(mouse.getId()).orElseThrow().getStock();
            System.out.println("Mouse stock before " + stockBefore + " and after " + stockAfter);
            auditLogs.findAll().forEach(a -> System.out.println("Audit: " + a.getMessage()));

            System.out.println("\n=== Cancel restores stock ===");
            System.out.println("Lamp stock before cancel: " + productRepository.findById(lamp.getId()).orElseThrow().getStock());
            orderService.cancel(second);
            System.out.println("Lamp stock after cancel: " + productRepository.findById(lamp.getId()).orElseThrow().getStock());

            System.out.println("\n=== Reports ===");
            System.out.println(reportService.revenueByCategory());
            System.out.println(reportService.topCustomers(2));
            System.out.println(reportService.ordersPerStatus());
            reportService.productsNeverOrdered().forEach(p -> System.out.println("Never ordered: " + p.getName()));
            System.out.println(reportService.monthlySales(LocalDate.now().getYear()));
            System.out.println("Discounted " + reportService.applyDiscount("Electronics", 10) + " products");
            System.out.println("Laptop price now: " + productRepository.findById(laptop.getId()).orElseThrow().getPrice());

            System.out.println("\n=== Queries ===");
            System.out.println("Search desk: " + productRepository.search("desk", null, null, null).size());
            System.out.println("Search 20 to 100 in Accessories: " + productRepository.search(null, new BigDecimal("20"), new BigDecimal("100"), "Accessories").size());
            System.out.println("Low stock: " + productRepository.findLowStock(5).size());
            System.out.println("Page 1 of size 2 has " + productRepository.findPage(1, 2).size() + " of " + productRepository.countAll());

            System.out.println("\n=== Statements with and without join fetch ===");
            statistics.reset();
            orderService.getOrderSummaryWithoutFetch(first);
            System.out.println("Without fetch: " + statistics.statements());
            statistics.reset();
            orderService.getOrderSummary(first);
            System.out.println("With join fetch: " + statistics.statements());
        }
    }

    private static Map<Long, Integer> items(Object... pairs) {
        Map<Long, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put(((Product) pairs[i]).getId(), (Integer) pairs[i + 1]);
        }
        return map;
    }
}
