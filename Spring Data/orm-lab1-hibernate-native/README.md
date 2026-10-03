# ORM Day 1, Lab 1: Hibernate native API

Run `com.training.Main`. The console shows the SQL for each CRUD step, the lifecycle walkthrough and the cache demonstration.

## Lifecycle states
- Transient: created with `new`, no id, Hibernate knows nothing about it.
- Persistent: attached to an open `Session`, tracked, and changes are written at flush or commit.
- Detached: was persistent but its `Session` closed, so changes are no longer tracked.
- Removed: scheduled for deletion by `session.remove(...)`, the row is gone after commit.

## Cache
Two `get()` calls for the same id in one `Session` return the same reference and issue one `SELECT`. A new `Session` issues a new `SELECT`, so the first level cache belongs to one `Session`.

## Part F
In raw JDBC the raise needs an `UPDATE` statement written by hand, a `PreparedStatement`, parameters bound in the right order, `executeUpdate`, and manual closing of resources. Here the salary field is changed on a managed object and dirty checking builds and runs the `UPDATE` at commit.
