package com.example.taskmvc;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TaskRepository {

    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public TaskRepository() {
        save(new Task(null, "Write the report", Priority.HIGH, false));
        save(new Task(null, "Book a meeting room", Priority.LOW, true));
        save(new Task(null, "Review pull requests", Priority.MEDIUM, false));
        save(new Task(null, "Fix the login bug", Priority.HIGH, false));
    }

    public Task save(Task task) {
        task.setId(nextId.getAndIncrement());
        tasks.put(task.getId(), task);
        return task;
    }

    public List<Task> findAll() {
        List<Task> all = new ArrayList<>(tasks.values());
        all.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        return all;
    }

    public Task findById(Long id) {
        return tasks.get(id);
    }

    public List<Task> findByPriority(Priority priority) {
        return findAll().stream().filter(t -> t.getPriority() == priority).toList();
    }
}
