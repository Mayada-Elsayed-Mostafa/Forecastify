package com.example.forecastify.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.forecastify.MainActivity
import com.example.forecastify.R

const val CHANNEL_ID = "WEATHER_ALERTS_CHANNEL"
const val NOTIFICATION_ID = 2002

class MyNotificationChannel {

    companion object {
        @SuppressLint("ObsoleteSdkInt")
        fun createNotificationChannel(
            context: Context,
            city: String,
            description: String,
        ) {

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Weather Alerts"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    setDescription("Weather Alert Notifications")
                }
                notificationManager.createNotificationChannel(channel)
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder =
                NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.cloudy_icon)
                    .setContentTitle(city).setContentText(description)
                    .setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(pendingIntent)
                    .setAutoCancel(true)

            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
    }
}
