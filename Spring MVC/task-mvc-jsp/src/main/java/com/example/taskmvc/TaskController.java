package com.example.taskmvc;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository repository;

    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("heading", "All tasks");
        model.addAttribute("tasks", repository.findAll());
        return "tasks";
    }

    @GetMapping("/search")
    public String search(@RequestParam(defaultValue = "HIGH") Priority priority, Model model) {
        model.addAttribute("heading", priority + " priority tasks");
        model.addAttribute("tasks", repository.findByPriority(priority));
        return "tasks";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("priorities", Priority.values());
        return "task-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("task") Task task, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            return "task-form";
        }
        repository.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        Task task = repository.findById(id);
        if (task == null) {
            throw new TaskNotFoundException(id);
        }
        model.addAttribute("task", task);
        return "task";
    }
}
