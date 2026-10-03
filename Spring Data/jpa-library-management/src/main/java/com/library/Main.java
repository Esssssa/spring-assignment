package com.library;

import com.library.model.Author;
import com.library.model.Book;
import com.library.model.Category;
import com.library.model.Customer;
import com.library.model.Employee;
import com.library.model.Person;
import com.library.model.Publisher;
import com.library.repository.LibraryQueries;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.LazyInitializationException;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("libraryPU");

        loadSampleData(emf);
        cascadeAndOrphanRemoval(emf);
        lazyInitialization(emf);
        queries(emf);

        emf.close();
    }

    private static void loadSampleData(EntityManagerFactory emf) {
        System.out.println("=== Sample data and cascade persist ===");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Publisher penguin = new Publisher("Penguin");
        Publisher oreilly = new Publisher("OReilly");
        Category fiction = new Category("Fiction");
        Category technology = new Category("Technology");
        Category classics = new Category("Classics");
        em.persist(penguin);
        em.persist(oreilly);
        em.persist(fiction);
        em.persist(technology);
        em.persist(classics);

        Author orwell = new Author("George Orwell");
        Book nineteenEightyFour = book("1984", penguin, fiction, classics);
        Book animalFarm = book("Animal Farm", penguin, fiction, classics);
        Book homage = book("Homage to Catalonia", penguin, classics);
        orwell.addBook(nineteenEightyFour);
        orwell.addBook(animalFarm);
        orwell.addBook(homage);

        Author bloch = new Author("Joshua Bloch");
        Book effectiveJava = book("Effective Java", oreilly, technology);
        Book javaPuzzlers = book("Java Puzzlers", oreilly, technology);
        bloch.addBook(effectiveJava);
        bloch.addBook(javaPuzzlers);

        Author newcomer = new Author("New Author");

        em.persist(orwell);
        em.persist(bloch);
        em.persist(newcomer);

        em.persist(new Employee("Mona Hassan", "mona@library.com", "Librarian", 9000));
        em.persist(new Customer("Karim Adel", "karim@mail.com", "MEM-1001"));
        em.getTransaction().commit();

        System.out.println("Authors persisted: " + em.createQuery("SELECT COUNT(a) FROM Author a", Long.class).getSingleResult());
        System.out.println("Books persisted through cascade: " + em.createQuery("SELECT COUNT(b) FROM Book b", Long.class).getSingleResult());
        for (Person person : em.createQuery("SELECT p FROM Person p", Person.class).getResultList()) {
            System.out.println(person.getClass().getSimpleName() + ": " + person.getName());
        }
        em.close();
    }

    private static Book book(String title, Publisher publisher, Category... categories) {
        Book book = new Book(title);
        book.setPublisher(publisher);
        for (Category category : categories) {
            book.addCategory(category);
        }
        return book;
    }

    private static void cascadeAndOrphanRemoval(EntityManagerFactory emf) {
        System.out.println("\n=== Orphan removal ===");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Author orwell = new LibraryQueries(em).authorWithBooks("George Orwell");
        System.out.println("Books before: " + orwell.getBooks().size());
        Book removed = orwell.getBooks().stream().filter(b -> b.getTitle().equals("Homage to Catalonia")).findFirst().orElseThrow();
        orwell.removeBook(removed);
        em.getTransaction().commit();
        System.out.println("Books in the table after removal: " + em.createQuery("SELECT COUNT(b) FROM Book b WHERE b.author.name = 'George Orwell'", Long.class).getSingleResult());
        System.out.println("Removed book still exists: " + (em.find(Book.class, removed.getId()) != null));
        em.close();
    }

    private static void lazyInitialization(EntityManagerFactory emf) {
        System.out.println("\n=== LAZY versus JOIN FETCH ===");
        EntityManager em = emf.createEntityManager();
        Author lazy = new LibraryQueries(em).authorWithoutFetch("Joshua Bloch");
        em.close();
        try {
            System.out.println(lazy.getBooks().size());
        } catch (LazyInitializationException e) {
            System.out.println("LazyInitializationException: " + e.getMessage());
        }

        em = emf.createEntityManager();
        Author fetched = new LibraryQueries(em).authorWithBooks("Joshua Bloch");
        em.close();
        System.out.println("With JOIN FETCH after close: " + fetched.getBooks().size() + " books");
    }

    private static void queries(EntityManagerFactory emf) {
        System.out.println("\n=== Queries ===");
        EntityManager em = emf.createEntityManager();
        LibraryQueries queries = new LibraryQueries(em);

        System.out.println("Books by George Orwell: " + queries.booksByAuthorName("George Orwell"));
        System.out.println("Books by publisher OReilly: " + queries.booksByPublisher("OReilly"));
        System.out.println("Book with id 1: " + queries.bookById(1L));
        for (Object[] row : queries.bookCountPerAuthor()) {
            System.out.println(row[0] + " -> " + row[1]);
        }
        System.out.println("Criteria by title: " + queries.criteriaByTitle("Effective Java"));
        System.out.println("Criteria title only: " + queries.criteriaSearch("java", null));
        System.out.println("Criteria author only: " + queries.criteriaSearch(null, "George Orwell"));
        System.out.println("Criteria both: " + queries.criteriaSearch("farm", "George Orwell"));
        System.out.println("Criteria none: " + queries.criteriaSearch(null, null));
        System.out.println("JPQL first two: " + queries.jpqlFirstTwoByTitle());
        System.out.println("HQL first two: " + queries.hqlFirstTwoByTitle());
        em.close();
    }
}
