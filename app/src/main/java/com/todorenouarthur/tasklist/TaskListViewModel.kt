package com.todorenouarthur.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskListViewModel: ViewModel() {

    private val repository = TasksRepository()
    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    public val taskList: StateFlow<List<Task>> = _taskList

    fun refresh() {
        viewModelScope.launch {
            val fetchedTasks = repository.refresh()
            // on modifie la valeur encapsulée, ce qui va notifier ses Observers et donc déclencher leur callback
            if (fetchedTasks != null) _taskList.value = fetchedTasks
        }
    }
    fun delete(task: Task) {
        viewModelScope.launch {
            if (repository.deleteTask(task)) {
                _taskList.value = taskList.value - task;
            }
        }
    }
    fun addOrEdit(task: Task) {
        viewModelScope.launch {
            val oldTask = taskList.value.firstOrNull { it.id == task.id }
            val task = repository.createOrUpdate(task)

            if(task != null) {
                if(oldTask != null) {
                    _taskList.value = taskList.value - oldTask;
                }
                _taskList.value = taskList.value + task
            }
        }
    }

}