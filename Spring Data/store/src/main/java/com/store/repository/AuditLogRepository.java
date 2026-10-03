package com.store.repository;

import com.store.model.AuditLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuditLogRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(AuditLog log) {
        em.persist(log);
    }

    public List<AuditLog> findAll() {
        return em.createQuery("SELECT a FROM AuditLog a ORDER BY a.id", AuditLog.class).getResultList();
    }
}
