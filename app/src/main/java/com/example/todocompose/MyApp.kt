package com.example.todocompose

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i("channel creator", "Creating channel")
        createNotificationChannel()
        Log.i("channel creator", "Created channel")
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NotificationService.MAIN_CHANNEL_ID,
            "Main",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Main and probably only channel in this app"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}