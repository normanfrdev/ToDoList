package com.normanfr.todolist

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository(private val taskDao: TaskDao) {

    // Using LiveData to observe data changes
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task) {
        withContext(Dispatchers.IO) {
            taskDao.insert(task)
        }
    }

    suspend fun update(task: Task) {
        withContext(Dispatchers.IO) {
            taskDao.update(task)
        }
    }

    suspend fun delete(task: Task) {
        withContext(Dispatchers.IO) {
            taskDao.delete(task)
        }
    }
}
