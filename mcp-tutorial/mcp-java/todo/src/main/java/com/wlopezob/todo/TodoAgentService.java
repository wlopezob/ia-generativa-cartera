package com.wlopezob.todo;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoAgentService {
    private final TodoService todoService;
    private final Gson gson = new Gson();

    // public TodoAgentService(TodoService todoService) {
    // this.todoService = todoService;
    // }

    @Tool(name = "addTodo", description = "Add a new todo item")
    public Todo addTodo(String task) {
        return todoService.addTodo(task);
    }

    @Tool(name = "getTodoById", description = "Get a todo item by its ID")
    public String getTodoById(@ToolParam(description = "The ID of the todo item") String id) {
        Todo todo = todoService
                .getTodoById(id).orElseThrow(() -> new RuntimeException("Not found " + id));
        return gson.toJson(todo);
    }

    @Tool(name = "listTodos", description = "List all todo items")
    public String listTodos() {
        return gson.toJson(todoService.listTodos());
    }

    @Tool(name = "updateTodoStatus", description = "Update the status of a todo item")
    public String updateTodoStatus(@ToolParam(description = "The ID of the todo item") String id,
            @ToolParam(description = "The new status of the todo item") boolean completed) {
        var todo = todoService.updateTodoStatus(id, completed)
                .orElseThrow(() -> new RuntimeException("Not found " + id));
        return gson.toJson(todo);
    }

    @Tool(name = "deleteTodo", description = "Delete a todo item")
    public String deleteTodo(@ToolParam(description = "The ID of the todo item") String id) {
        todoService.deleteTodo(id);
        return "Deleted " + id;
    }
    
}
