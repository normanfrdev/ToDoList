package com.normanfr.todolist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Update
import androidx.room.Query
import com.normanfr.todolist.Task

@Dao
interface TaskDao {

    @Insert
    fun insert(task: Task): Long // Returns the row ID of the newly inserted task

    @Delete
    fun delete(task: Task): Int // Returns the number of rows affected

    @Update
    fun update(task: Task): Int // Returns the number of rows affected

    @Query("SELECT * FROM task")
    fun getAllTasks(): LiveData<List<Task>> // Use LiveData to observe data changes

    @Query("SELECT * FROM task WHERE id = :taskId")
    fun getTaskById(taskId: Int): Task?
}
