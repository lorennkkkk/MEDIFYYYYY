package com.example.medifyyyyy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medifyyyyy.data.remote.SupabaseHolder
import com.example.medifyyyyy.data.repository.TodoRepository
import com.example.medifyyyyy.domain.model.Todo
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TodoViewModel : ViewModel() {

    private val repo = TodoRepository()

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> get() = _todos

    fun loadTodos() {
        val session = SupabaseHolder.session() ?: return
        val userId = session.user!!.id

        viewModelScope.launch {
            _todos.value = repo.getTodos(userId)
        }
    }

    fun addTodo(title: String, time: String) {
        val session = SupabaseHolder.session() ?: return
        val userId = session.user!!.id

        viewModelScope.launch {
            repo.addTodo(userId, title, time)
            loadTodos() // refresh list
        }
    }

    fun deleteTodo(todo: Todo) {
        val id = todo.id ?: return

        viewModelScope.launch {
            repo.deleteTodo(id)
            loadTodos()
        }
    }
}