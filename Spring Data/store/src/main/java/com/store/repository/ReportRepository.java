package com.store.repository;

import com.store.dto.CategoryRevenue;
import com.store.dto.CustomerSpend;
import com.store.dto.MonthlySales;
import com.store.model.OrderStatus;
import com.store.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReportRepository {

    private static final List<OrderStatus> EARNING = List.of(OrderStatus.PAID, OrderStatus.SHIPPED);

    @PersistenceContext
    private EntityManager em;

    public List<CategoryRevenue> revenueByCategory() {
        return em.createQuery("SELECT NEW com.store.dto.CategoryRevenue(c.name, SUM(i.unitPrice * i.quantity)) "
                        + "FROM Order o JOIN o.items i JOIN i.product p JOIN p.categories c "
                        + "WHERE o.status IN :statuses GROUP BY c.name ORDER BY SUM(i.unitPrice * i.quantity) DESC", CategoryRevenue.class)
                .setParameter("statuses", EARNING)
                .getResultList();
    }

    public List<CustomerSpend> topCustomers(int limit) {
        return em.createQuery("SELECT NEW com.store.dto.CustomerSpend(c.name, SUM(i.unitPrice * i.quantity)) "
                        + "FROM Order o JOIN o.customer c JOIN o.items i "
                        + "WHERE o.status IN :statuses GROUP BY c.id, c.name ORDER BY SUM(i.unitPrice * i.quantity) DESC", CustomerSpend.class)
                .setParameter("statuses", EARNING)
                .setMaxResults(limit)
                .getResultList();
    }

    public Map<OrderStatus, Long> ordersPerStatus() {
        Map<OrderStatus, Long> result = new EnumMap<>(OrderStatus.class);
        List<Object[]> rows = em.createQuery("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status", Object[].class).getResultList();
        for (Object[] row : rows) {
            result.put((OrderStatus) row[0], (Long) row[1]);
        }
        return result;
    }

    public List<Product> productsNeverOrdered() {
        return em.createQuery("SELECT p FROM Product p WHERE NOT EXISTS (SELECT i.id FROM OrderItem i WHERE i.product = p) ORDER BY p.id", Product.class)
                .getResultList();
    }

    public List<MonthlySales> monthlySales(int year) {
        return em.createQuery("SELECT NEW com.store.dto.MonthlySales(MONTH(o.orderedAt), SUM(i.unitPrice * i.quantity)) "
                        + "FROM Order o JOIN o.items i WHERE YEAR(o.orderedAt) = :year AND o.status IN :statuses "
                        + "GROUP BY MONTH(o.orderedAt) ORDER BY MONTH(o.orderedAt)", MonthlySales.class)
                .setParameter("year", year)
                .setParameter("statuses", EARNING)
                .getResultList();
    }

    public int applyDiscount(String category, double percent) {
        BigDecimal factor = BigDecimal.valueOf(100 - percent).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        int updated = em.createQuery("UPDATE Product p SET p.price = p.price * :factor "
                        + "WHERE p.id IN (SELECT p2.id FROM Product p2 JOIN p2.categories c WHERE c.name = :category)")
                .setParameter("factor", factor)
                .setParameter("category", category)
                .executeUpdate();
        em.clear();
        return updated;
    }
}
