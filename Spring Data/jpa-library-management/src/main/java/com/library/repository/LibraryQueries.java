package com.library.repository;

import com.library.model.Author;
import com.library.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public class LibraryQueries {

    private final EntityManager em;

    public LibraryQueries(EntityManager em) {
        this.em = em;
    }

    public List<Book> booksByAuthorName(String name) {
        return em.createQuery("SELECT b FROM Book b WHERE b.author.name = :name", Book.class)
                .setParameter("name", name)
                .getResultList();
    }

    public List<Book> booksByPublisher(String publisherName) {
        return em.createQuery("SELECT b FROM Book b WHERE b.publisher.name = :name", Book.class)
                .setParameter("name", publisherName)
                .getResultList();
    }

    public Book bookById(Long id) {
        return em.createQuery("SELECT b FROM Book b WHERE b.id = ?1", Book.class)
                .setParameter(1, id)
                .getSingleResult();
    }

    public Author authorWithBooks(String name) {
        return em.createQuery("SELECT a FROM Author a JOIN FETCH a.books WHERE a.name = :name", Author.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    public Author authorWithoutFetch(String name) {
        return em.createQuery("SELECT a FROM Author a WHERE a.name = :name", Author.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    public List<Object[]> bookCountPerAuthor() {
        return em.createQuery("SELECT a.name, COUNT(b) FROM Author a LEFT JOIN a.books b GROUP BY a.name ORDER BY a.name", Object[].class)
                .getResultList();
    }

    public List<Book> criteriaByTitle(String title) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> book = query.from(Book.class);
        query.select(book).where(cb.equal(book.get("title"), title));
        return em.createQuery(query).getResultList();
    }

    public List<Book> criteriaSearch(String title, String authorName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> book = query.from(Book.class);
        List<Predicate> predicates = new ArrayList<>();
        if (title != null && !title.isBlank()) {
            predicates.add(cb.like(cb.lower(book.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if (authorName != null && !authorName.isBlank()) {
            Join<Book, Author> author = book.join("author");
            predicates.add(cb.equal(author.get("name"), authorName));
        }
        query.select(book).where(predicates.toArray(new Predicate[0]));
        return em.createQuery(query).getResultList();
    }

    public List<Book> hqlFirstTwoByTitle() {
        return em.createQuery("from Book order by title limit 2", Book.class).getResultList();
    }

    public List<Book> jpqlFirstTwoByTitle() {
        return em.createQuery("SELECT b FROM Book b ORDER BY b.title", Book.class)
                .setMaxResults(2)
                .getResultList();
    }
}
