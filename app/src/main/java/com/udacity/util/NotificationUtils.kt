package com.udacity.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R


// Notification ID.
private val NOTIFICATION_ID = 0


fun createDownloadNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            context.getString(R.string.channel_id),
            context.getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.channel_description)
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun NotificationManager.sendDownloadComplete(fileName: String, fileDescription: String, status: String, context: Context) {
    val extras = Bundle()
    extras.putString("status", status)
    extras.putString("fileName", fileName)
    extras.putString("fileDescription", fileDescription)

    val contentIntent = Intent(context, DetailActivity::class.java)
    contentIntent.putExtras(extras)

    val contentPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(context, context.getString(R.string.channel_id))
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(fileName)
        .setContentText(fileDescription)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .addAction(R.drawable.ic_assistant_black_24dp, context.getString(R.string.notification_button), contentPendingIntent)

    notify(NOTIFICATION_ID, builder.build())
}


