package com.normanfr.todolist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val task = intent.getParcelableExtra<Task>("task")
        task?.let {
            NotificationHelper(context).sendNotification(it)
        }
    }
}
