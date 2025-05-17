package com.wlopezob.todo;

import org.springframework.stereotype.Service;
// Import for @Function쫀bbing if that's how Spring AI MCP server exposes tools
// For example: import org.springframework.ai.model.function.Function쫀bbing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service // This annotation stays on the implementation class
public class TodoServiceImpl implements TodoService { // Implement the interface

    private final Map<String, Todo> todos = new ConcurrentHashMap<>();

    public TodoServiceImpl() { // Constructor name changed
        // Initialize with a sample todo
        String sampleId = UUID.randomUUID().toString();
        todos.put(sampleId, new Todo(sampleId, "Initial MCP Todo Item", false));
        System.out.println("TodoServiceImpl Initialized for MCP Server functionality.");
    }

    @Override
    public List<Todo> listTodos() {
        System.out.println("MCP Tool Invoked: listTodos");
        return new ArrayList<>(todos.values());
    }

    @Override
    public Todo addTodo(String task) { 
        String id = UUID.randomUUID().toString();
        Todo newTodo = new Todo(id, task, false);
        todos.put(id, newTodo);
        System.out.println("MCP Tool Invoked: addTodo - Task: " + task);
        return newTodo;
    }

    @Override
    public Optional<Todo> getTodoById(String id) {
        System.out.println("MCP Tool Invoked: getTodoById - ID: " + id);
        return Optional.ofNullable(todos.get(id));
    }

    @Override
    public Optional<Todo> updateTodoStatus(String id, boolean completed) { 
        Todo todo = todos.get(id);
        if (todo != null) {
            todo.setCompleted(completed);
            todos.put(id, todo);
            System.out.println("MCP Tool Invoked: updateTodoStatus - ID: " + id + ", Completed: " + completed);
            return Optional.of(todo);
        }
        System.out.println("MCP Tool Invoked: updateTodoStatus - ID: " + id + " - Not Found");
        return Optional.empty();
    }

    @Override
    public boolean deleteTodo(String id) {
        System.out.println("MCP Tool Invoked: deleteTodo - ID: " + id);
        return todos.remove(id) != null;
    }

} 