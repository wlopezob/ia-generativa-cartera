// In-memory storage for todos
let todos = [];
// Helper function to generate a unique ID
const generateId = () => {
    return Math.random().toString(36).substring(2) + Date.now().toString(36);
};
// Add a new todo
export const createTodo = (task) => {
    const newTodo = {
        id: generateId(),
        task,
        completed: false,
        createdAt: new Date(),
    };
    todos.push(newTodo);
    return newTodo;
};
// Get all todos
export const getAllTodos = () => {
    return [...todos];
};
// Get a specific todo by ID
export const getTodoById = (id) => {
    return todos.find(todo => todo.id === id);
};
// Mark a todo as completed
export const completeTodo = (id) => {
    const todo = todos.find(todo => todo.id === id);
    if (todo) {
        todo.completed = true;
        todo.completedAt = new Date();
    }
    return todo;
};
// Delete a todo
export const deleteTodo = (id) => {
    const initialLength = todos.length;
    todos = todos.filter(todo => todo.id !== id);
    return todos.length !== initialLength;
};
// Update a todo's task
export const updateTodoTask = (id, newTask) => {
    const todo = todos.find(todo => todo.id === id);
    if (todo) {
        todo.task = newTask;
    }
    return todo;
};
// Get all completed todos
export const getCompletedTodos = () => {
    return todos.filter(todo => todo.completed);
};
// Get all pending todos
export const getPendingTodos = () => {
    return todos.filter(todo => !todo.completed);
};
// Clear all completed todos
export const clearCompletedTodos = () => {
    todos = todos.filter(todo => !todo.completed);
};
