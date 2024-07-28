package com.normanfr.todolist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val title = inputData.getString("taskTitle") ?: "Task Reminder"
        val description = inputData.getString("taskDescription") ?: "You have a task due!"

        sendNotification(title, description)
        return Result.success()
    }

    private fun sendNotification(title: String, description: String) {
        val channelId = "task_notifications"
        val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt() // Unique ID for each notification

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Task Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                //description = "Channel for task reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Replace with your notification icon
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
