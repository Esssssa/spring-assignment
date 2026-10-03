# jpa library management

run com.library.Main. h2 in memory, unit name libraryPU. it prints the sample data, cascade + orphan removal, the LazyInitializationException and all the query results.

## relationships

- Author 1-N Book: Book owns it (book.author_id). Author.books is mappedBy so it has no column
- Publisher 1-N Book: Book owns it (book.publisher_id), same idea
- Book N-N Category: Book owns it, join table book_category(book_id, category_id). join table is only needed for many to many because neither side can hold a single fk

everything is LAZY. @ManyToOne is eager by default so i set it to LAZY myself.
Author.books has cascade ALL + orphanRemoval, so saving an author saves its new books and removing a book from the list deletes the row. addBook/removeBook keep both sides in sync. no cascade on Publisher.books because deleting a publisher shouldnt delete the books.

## inheritance

Person (abstract) -> Employee and Customer, SINGLE_TABLE with a person_type column. went with it because the subclasses only add a field or two, one table means no joins and its the fastest. downside is the subclass columns can be null, fine for something this small.

## queries

jpql: books by author name, books by publisher, book by id with ?1.
join fetch gets an author with all books in one select.
count + group by for books per author (left join so an author with 0 books still shows 0).
criteria: by title, then a dynamic version where title and author name are optional and predicates only get added if there is a value.

## lazy vs join fetch

the normal query only loads the author. after the EntityManager is closed, author.getBooks() throws LazyInitializationException because the collection is just an uninitialized proxy with no session behind it. with join fetch the books are loaded in the same query so you can read them after closing.

## jpql vs hql

jpqlFirstTwoByTitle is standard jpql (needs select b from Book b and setMaxResults). hqlFirstTwoByTitle is `from Book order by title limit 2`, no select and a limit clause, jpql doesnt allow either. only works on hibernate.

didnt do the optional orm.xml part.
