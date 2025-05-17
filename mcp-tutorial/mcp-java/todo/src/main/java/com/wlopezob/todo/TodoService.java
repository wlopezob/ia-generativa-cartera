package com.wlopezob.todo;

import java.util.List;
import java.util.Optional;

// This interface defines the contract for Todo operations.
// Implementations of this interface will provide the actual logic,
// and its methods can be exposed as MCP tools.
public interface TodoService {
    List<Todo> listTodos();
    Todo addTodo(String task);
    Optional<Todo> getTodoById(String id);
    Optional<Todo> updateTodoStatus(String id, boolean completed);
    boolean deleteTodo(String id);
} 