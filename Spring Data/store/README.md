# online store order management

final project from Task.pdf. plain spring (no boot), spring orm + jpa only. no spring data jpa, no jdbctemplate, no Session/SessionFactory.

one small exception to that last one: QueryStatistics in config unwraps the SessionFactory just to read the statement counter for the part 6 demo (generate_statistics). nothing else touches hibernate classes.

run `com.store.Main` for the demo, `mvn test` for the 10 tests (every test gets a fresh context and h2 db).

## config

JpaConfig has the h2 datasource (random db name every time), LocalContainerEntityManagerFactoryBean with HibernateJpaVendorAdapter, create-drop, show_sql and generate_statistics, JpaTransactionManager, and the static PersistenceExceptionTranslationPostProcessor bean.

## model

BaseEntity (@MappedSuperclass) has id, createdAt and @Version. Customer has an @Embedded Address, Product has the unique sku and equals/hashCode on sku, Order is on table `orders`, Payment is @OneToOne with Order. i also added an AuditLog entity for the REQUIRES_NEW part.

owning sides: OrderItem owns order + product, Order owns customer, Payment owns the order link (order_id), Product owns the product_category join table. the other sides are mappedBy and the helper methods (addOrder, addItem, removeItem, setPayment, addCategory) keep both sides in sync. everything is LAZY. Order -> OrderItem is cascade ALL + orphanRemoval. Order.getTotal() is calculated, not stored.

## business rules

- placeOrder checks qty > 0 and stock, takes the stock, copies the current price into the item. one bad line throws and the whole order rolls back (test checks stock is untouched)
- pay only from NEW, ship only from PAID, cancel from NEW or PAID and puts the stock back
- pay never calls save for the status, its just dirty checking
- getOrderSummary is readOnly and returns a record dto so nothing lazy leaks out
- register rejects duplicate emails

**REQUIRES_NEW**: when placeOrder fails it writes an audit log through AuditService.record (REQUIRES_NEW) and rethrows. the main transaction rolls back but the audit row stays because it was committed in its own transaction. needs to be a different bean or the proxy wouldnt kick in.

**two people buying the last item**: thats what the @Version column is for (optimistic locking). if two transactions change the same product the second one fails at commit with an optimistic lock exception instead of silently overselling. i didnt write a concurrent test for it.

## reports

all in ReportRepository, dtos are records built with jpql constructor expressions. revenue and top customers only count PAID and SHIPPED orders. ordersPerStatus returns an EnumMap. productsNeverOrdered uses not exists. monthlySales uses month()/year() on orderedAt.

**why clear() after the bulk discount**: the bulk update goes straight to the database and skips the persistence context, so any Product already loaded there would still hold the old price. clear() throws those stale copies away so the next read hits the db again. also bulk updates dont bump @Version.

## join fetch and n+1

OrderService has getOrderSummaryWithoutFetch (walks the lazy stuff, 6 statements for an order with 2 items) and getOrderSummary (join fetch, 1 statement). Main prints both counts.

funny thing i hit: Order.payment is the inverse (mappedBy) side of a one to one, hibernate cant make those lazy without bytecode enhancement so it loaded the payment with an extra select every time. fixed by adding `left join fetch o.payment` to the query.

## tests

StoreTest covers: stock + price copy, rollback with audit surviving, quantity check, pay/ship/cancel rules, cancel restoring stock, duplicate email, search with optional filters + paging, the reports, bulk discount, and the statement counts.
