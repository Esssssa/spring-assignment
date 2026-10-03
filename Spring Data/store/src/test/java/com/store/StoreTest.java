package com.store;

import com.store.config.JpaConfig;
import com.store.config.QueryStatistics;
import com.store.exception.DuplicateCustomerException;
import com.store.exception.InsufficientStockException;
import com.store.exception.InvalidOrderStateException;
import com.store.exception.InvalidQuantityException;
import com.store.model.Address;
import com.store.model.Customer;
import com.store.model.OrderStatus;
import com.store.model.PaymentMethod;
import com.store.model.Product;
import com.store.repository.AuditLogRepository;
import com.store.repository.OrderRepository;
import com.store.repository.ProductRepository;
import com.store.service.CustomerService;
import com.store.service.OrderService;
import com.store.service.ProductService;
import com.store.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(JpaConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StoreTest {

    @Autowired
    CustomerService customerService;
    @Autowired
    ProductService productService;
    @Autowired
    OrderService orderService;
    @Autowired
    ReportService reportService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    AuditLogRepository auditLogs;
    @Autowired
    QueryStatistics statistics;

    Customer customer;
    Product laptop;
    Product mouse;
    Product pen;

    @BeforeEach
    void setUp() {
        customer = customerService.register("Ahmed", "ahmed@mail.com", new Address("1 Nile St", "Cairo", "Egypt"));
        laptop = productService.addProduct("LAP-1", "Laptop", new BigDecimal("1200.00"), 5, "Electronics");
        mouse = productService.addProduct("MOU-1", "Wireless Mouse", new BigDecimal("25.00"), 10, "Electronics", "Accessories");
        pen = productService.addProduct("PEN-1", "Gel Pen", new BigDecimal("2.00"), 100, "Stationery");
    }

    private int stock(Product product) {
        return productRepository.findById(product.getId()).orElseThrow().getStock();
    }

    @Test
    void placeOrderDecreasesStockAndCopiesPrice() {
        Long id = orderService.placeOrder(customer.getId(), Map.of(laptop.getId(), 2));
        assertEquals(3, stock(laptop));
        productService.changePrice(laptop.getId(), new BigDecimal("999.00"));
        assertEquals(0, new BigDecimal("2400.00").compareTo(orderService.getOrderSummary(id).total()));
    }

    @Test
    void badLineRollsBackTheWholeOrderButTheAuditSurvives() {
        assertThrows(InsufficientStockException.class,
                () -> orderService.placeOrder(customer.getId(), Map.of(mouse.getId(), 2, laptop.getId(), 50)));
        assertEquals(10, stock(mouse));
        assertEquals(5, stock(laptop));
        assertTrue(orderRepository.findByCustomer(customer.getId()).isEmpty());
        assertEquals(1, auditLogs.findAll().size());
    }

    @Test
    void quantityMustBePositive() {
        assertThrows(InvalidQuantityException.class, () -> orderService.placeOrder(customer.getId(), Map.of(pen.getId(), 0)));
    }

    @Test
    void payShipAndCancelFollowTheRules() {
        Long id = orderService.placeOrder(customer.getId(), Map.of(mouse.getId(), 3));
        assertThrows(InvalidOrderStateException.class, () -> orderService.ship(id));
        orderService.pay(id, PaymentMethod.CARD);
        assertEquals(OrderStatus.PAID, orderRepository.findById(id).orElseThrow().getStatus());
        assertThrows(InvalidOrderStateException.class, () -> orderService.pay(id, PaymentMethod.CASH));
        orderService.ship(id);
        assertThrows(InvalidOrderStateException.class, () -> orderService.cancel(id));
    }

    @Test
    void cancelRestoresStock() {
        Long id = orderService.placeOrder(customer.getId(), Map.of(mouse.getId(), 4));
        assertEquals(6, stock(mouse));
        orderService.cancel(id);
        assertEquals(10, stock(mouse));
        assertEquals(OrderStatus.CANCELLED, orderRepository.findById(id).orElseThrow().getStatus());
    }

    @Test
    void duplicateEmailIsRejected() {
        assertThrows(DuplicateCustomerException.class, () -> customerService.register("Other", "ahmed@mail.com", null));
    }

    @Test
    void searchFiltersAreOptional() {
        assertEquals(3, productRepository.search(null, null, null, null).size());
        assertEquals(1, productRepository.search("lap", null, null, null).size());
        assertEquals(2, productRepository.search(null, new BigDecimal("20"), null, "Electronics").size());
        assertEquals(1, productRepository.search(null, null, new BigDecimal("30"), "Accessories").size());
        assertEquals(2, productRepository.findByCategory("Electronics").size());
        assertEquals(1, productRepository.findLowStock(6).size());
        assertEquals(2, productRepository.findPage(0, 2).size());
        assertEquals(1, productRepository.findPage(1, 2).size());
        assertEquals(3, productRepository.countAll());
    }

    @Test
    void reportsReturnTheExpectedNumbers() {
        Long paid = orderService.placeOrder(customer.getId(), Map.of(laptop.getId(), 1, mouse.getId(), 2));
        orderService.pay(paid, PaymentMethod.CARD);
        orderService.placeOrder(customer.getId(), Map.of(pen.getId(), 10));

        assertEquals(0, new BigDecimal("1250.00").compareTo(reportService.revenueByCategory().get(0).revenue()));
        assertEquals("Electronics", reportService.revenueByCategory().get(0).category());
        assertEquals(1, reportService.topCustomers(5).size());
        assertEquals(0, new BigDecimal("1250.00").compareTo(reportService.topCustomers(5).get(0).total()));
        assertEquals(1L, reportService.ordersPerStatus().get(OrderStatus.PAID));
        assertEquals(1L, reportService.ordersPerStatus().get(OrderStatus.NEW));
        assertEquals(0, reportService.productsNeverOrdered().size());
        assertEquals(1, reportService.monthlySales(java.time.LocalDate.now().getYear()).size());
    }

    @Test
    void bulkDiscountChangesPricesAndSeesFreshData() {
        productRepository.findById(laptop.getId());
        assertEquals(2, reportService.applyDiscount("Electronics", 10));
        assertEquals(0, new BigDecimal("1080.00").compareTo(productRepository.findById(laptop.getId()).orElseThrow().getPrice()));
    }

    @Test
    void joinFetchUsesFewerStatements() {
        Long id = orderService.placeOrder(customer.getId(), Map.of(laptop.getId(), 1, mouse.getId(), 1, pen.getId(), 1));
        statistics.reset();
        orderService.getOrderSummaryWithoutFetch(id);
        long without = statistics.statements();
        statistics.reset();
        orderService.getOrderSummary(id);
        long with = statistics.statements();
        assertEquals(1, with);
        assertTrue(without > with);
    }
}
