# JPA Library Management System

Run `com.library.Main` (H2 in memory, `persistence.xml` unit `libraryPU`). It prints the sample data, the cascade and orphan removal results, the `LazyInitializationException`, and every query result.

## Relationships

| Relationship | Owning side | Foreign key | Fetch |
|---|---|---|---|
| `Author` 1 to N `Book` | `Book.author` | `book.author_id` | `Author.books` LAZY, `Book.author` LAZY |
| `Publisher` 1 to N `Book` | `Book.publisher` | `book.publisher_id` | `Book.publisher` LAZY, `Publisher.books` LAZY |
| `Book` N to N `Category` | `Book.categories` | join table `book_category(book_id, category_id)` | LAZY both ways |

- `Author.books` and `Publisher.books` use `mappedBy`, so they are the inverse sides and have no column.
- A join table is needed only for many to many, because neither table can hold a single foreign key.
- `@ManyToOne` defaults to EAGER, so it is set to LAZY explicitly.
- `Author.books` has `cascade = ALL` and `orphanRemoval = true`. Persisting an author persists new books, and removing a book from the list deletes its row. `addBook` and `removeBook` keep both sides in sync.
- `Publisher.books` has no cascade because deleting a publisher should not delete books.

## Inheritance
`Person` is an abstract base for `Employee` and `Customer`, mapped with `SINGLE_TABLE` and a `person_type` discriminator. The two subclasses add only one or two fields, so one table avoids joins and is the fastest to query. The cost is that subclass columns are nullable, which is acceptable at this size.

## Queries
- JPQL: books by author name, books by publisher, a book by id with the positional parameter `?1`.
- `JOIN FETCH` loads the author and the books in one `SELECT`.
- `COUNT` with `GROUP BY` returns each author and the number of books. It uses `LEFT JOIN`, so an author with no books shows 0.
- Criteria API: by title, then a dynamic version where the title and author filters are optional and predicates are added only when a value is given.

## LAZY versus JOIN FETCH
The plain query loads only the author. After the `EntityManager` is closed, `getBooks()` throws `LazyInitializationException` because the collection is an uninitialised proxy with no session. The `JOIN FETCH` query fills the collection while the session is open, so it is safe to read afterwards.

## JPQL versus HQL
`jpqlFirstTwoByTitle` is standard JPQL and needs `SELECT b FROM Book b` and `setMaxResults`. `hqlFirstTwoByTitle` is HQL: `from Book order by title limit 2` has no `SELECT` clause and has a `LIMIT` clause, both of which JPQL does not allow. It works only on Hibernate.

The optional `orm.xml` extension was not done.
