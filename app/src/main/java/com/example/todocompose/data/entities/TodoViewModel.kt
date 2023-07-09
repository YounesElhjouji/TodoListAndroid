package com.example.todo.view.screens.Todo

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.todocompose.data.entities.Todo

class TodoViewModel : ViewModel() {
    private var _todoList = mutableStateListOf<Todo>()
    private var insertCounter = 0


    fun getAllTodos(): List<Todo> {
        return _todoList;
    }

    fun addTodo(todoItem: Todo) {
        insertCounter++
        todoItem.id = insertCounter
        _todoList.add(todoItem)
    }

    fun removeTodo(todoItem: Todo) {
        _todoList.remove(todoItem)
    }

    fun removeDoneTodos() {
        _todoList.removeAll {
            it.isChecked
        }
    }

    fun toggleChecked(index: Int, value: Boolean) {
        val item = _todoList[index].apply {
            isChecked = value
        }
        _todoList.removeAt(index)
        if (value) _todoList.add(item) else _todoList.add(0, item)

    }
}

