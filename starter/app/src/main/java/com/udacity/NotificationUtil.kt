package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(filename: String, status: String, description: String, title: String, id :String, applicationContext: Context) {

    val completedIntent = Intent(applicationContext, DetailActivity::class.java)
    completedIntent.apply {
        putExtra("filename", filename)
        putExtra("status", status)
    }

    val completedPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        completedIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val action = NotificationCompat.Action.Builder(0,"Show Details",completedPendingIntent).build()

    val builder = NotificationCompat.Builder(
        applicationContext,
        id
    )
        .setSmallIcon(R.drawable.ic_download)
        .setContentTitle(title)
        .setContentText(description)
        .setContentIntent(completedPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .addAction(action)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}