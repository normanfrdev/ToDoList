package com.normanfr.todolist

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), TaskAdapter.TaskClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var fab: FloatingActionButton
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        fab = findViewById(R.id.fab)

        taskAdapter = TaskAdapter(this)
        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        taskViewModel = ViewModelProvider(this, TaskViewModelFactory(application)).get(TaskViewModel::class.java)
        taskViewModel.allTasks.observe(this, Observer { tasks ->
            tasks?.let { taskAdapter.setTasks(it) }
        })

        notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()

        fab.setOnClickListener {
            val intent = Intent(this, AddEditTaskActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onTaskClick(task: Task) {
        val intent = Intent(this, AddEditTaskActivity::class.java)
        intent.putExtra("task", task)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<Task>("task")?.let { task ->
                if (task.id == 0) {
                    taskViewModel.insert(task)
                } else {
                    taskViewModel.update(task)
                }
                // Send a notification when a task is added or updated
                notificationHelper.sendNotification(task)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NotificationHelper.REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, you can send notifications now
            } else {
                // Permission denied
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 1
    }
}

