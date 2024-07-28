package com.normanfr.todolist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class AddEditTaskActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var etDueDate: EditText
    private lateinit var etDueTime: EditText
    private lateinit var btnSelectDate: Button
    private lateinit var btnSelectTime: Button
    private lateinit var btnSave: Button

    private var taskId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_task)

        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        etDueDate = findViewById(R.id.etDueDate)
        etDueTime = findViewById(R.id.etDueTime)
        btnSelectDate = findViewById(R.id.btnSelectDate)
        btnSelectTime = findViewById(R.id.btnSelectTime)
        btnSave = findViewById(R.id.btnSave)

        val task = intent.getParcelableExtra<Task>("task")
        task?.let {
            taskId = it.id
            etTitle.setText(it.title)
            etDescription.setText(it.description)
            etDueDate.setText(it.dueDate)
            etDueTime.setText(it.dueTime)
        }

        btnSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        btnSelectTime.setOnClickListener {
            showTimePickerDialog()
        }

        btnSave.setOnClickListener {
            saveTask()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedMonth = String.format("%02d", selectedMonth + 1)
            val formattedDay = String.format("%02d", selectedDay)
            etDueDate.setText("$selectedYear-$formattedMonth-$formattedDay")
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            etDueTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun saveTask() {
        val title = etTitle.text.toString()
        val description = etDescription.text.toString()
        val dueDate = etDueDate.text.toString()
        val dueTime = etDueTime.text.toString()

        val isCompleted = false

        val newTask = Task(
            id = taskId ?: 0,
            title = title,
            description = description,
            dueDate = dueDate,
            dueTime = dueTime,
            isCompleted = isCompleted
        )

        val combinedDateTimeString = "$dueDate $dueTime"
        val dueDateTimeMillis = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(combinedDateTimeString)?.time

        dueDateTimeMillis?.let {
            scheduleNotification(newTask, it)
        }

        val resultIntent = Intent().apply {
            putExtra("task", newTask)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun scheduleNotification(task: Task, triggerAtMillis: Long) {
        val currentMillis = System.currentTimeMillis()
        val delay = triggerAtMillis - currentMillis

        if (delay > 0) {
            val data = Data.Builder()
                .putString("taskTitle", task.title)
                .putString("taskDescription", task.description)
                .build()

            val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            WorkManager.getInstance(this).enqueue(notificationWork)
            Toast.makeText(this, "Notification scheduled!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "The scheduled time is in the past.", Toast.LENGTH_SHORT).show()
        }
    }
}
