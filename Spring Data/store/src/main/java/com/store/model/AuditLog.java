package com.store.model;

import jakarta.persistence.Entity;

@Entity
public class AuditLog extends BaseEntity {

    private String message;

    protected AuditLog() {
    }

    public AuditLog(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
