package com.todorenouarthur.tasklist

import com.todorenouarthur.network.Api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TasksRepository {
    private val tasksWebService = Api.taskWebService

    // Ces deux variables encapsulent la même donnée:
    // [_taskList] est modifiable mais privée donc inaccessible à l'extérieur de cette classe
    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    // [taskList] est publique mais non-modifiable:
    // On pourra seulement l'observer (s'y abonner) depuis d'autres classes
    public val taskList: StateFlow<List<Task>> = _taskList.asStateFlow()

    suspend fun refresh() {
        // Call HTTP (opération longue):
        val tasksResponse = tasksWebService.getTasks()
        // À la ligne suivante, on a reçu la réponse de l'API:
        if (tasksResponse.isSuccessful) {
            val fetchedTasks = tasksResponse.body()
            // on modifie la valeur encapsulée, ce qui va notifier ses Observers et donc déclencher leur callback
            if (fetchedTasks != null) _taskList.value = fetchedTasks
        }
    }

    suspend fun createOrUpdate(task: Task) {
        val oldTask = taskList.value.firstOrNull { it.id == task.id }
        val response = when {
            oldTask != null -> tasksWebService.update(task, task.id) // Update
            else -> tasksWebService.create(task) // Create
        }
        if (response.isSuccessful) {
            val updatedTask = response.body()!!
            if (oldTask != null) _taskList.value = taskList.value - oldTask
            _taskList.value = taskList.value + updatedTask
        }
    }

    suspend fun delete(task: Task): Boolean {
        val response = tasksWebService.delete(task.id);
        if(response.isSuccessful) {
            return true;
        }
        return false;
    }
}