package com.example.forecastify.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.forecastify.MainActivity
import com.example.forecastify.R
import com.example.forecastify.alerts.MyReceiver

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
                val soundUri = Uri.parse("android.resource://${context.packageName}/${R.raw.weather_news_sound}")
                val channel = NotificationChannel(
                    NotificationConstants.CHANNEL_ID,
                    "Weather Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    setDescription("Weather Alert Notifications")
                    setSound(
                        soundUri,
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    enableLights(true)
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                NotificationConstants.REQUEST_CODE_MAIN,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Cancel action
            val cancelIntent = Intent(context, MyReceiver::class.java).apply {
                action = NotificationConstants.ACTION_CANCEL
            }
            val cancelPendingIntent = PendingIntent.getBroadcast(
                context,
                NotificationConstants.REQUEST_CODE_CANCEL,
                cancelIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            // Dismiss action
            val dismissIntent = Intent(context, MyReceiver::class.java).apply {
                action = NotificationConstants.ACTION_DISMISS
                putExtra("description", description)
            }
            val dismissPendingIntent = PendingIntent.getBroadcast(
                context,
                NotificationConstants.REQUEST_CODE_DISMISS,
                dismissIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            // Snooze action
            val snoozeIntent = Intent(context, MyReceiver::class.java).apply {
                action = NotificationConstants.ACTION_SNOOZE
                putExtra("description", description)
            }
            val snoozePendingIntent = PendingIntent.getBroadcast(
                context,
                NotificationConstants.REQUEST_CODE_SNOOZE,
                snoozeIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
                .setSmallIcon(R.drawable.cloudy_icon)
                .setContentTitle(city)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.cloudy_icon, "Cancel", cancelPendingIntent)
                .addAction(R.drawable.cloudy_icon, "Dismiss", dismissPendingIntent)
                .addAction(R.drawable.cloudy_icon, "Snooze", snoozePendingIntent)

            notificationManager.notify(NotificationConstants.NOTIFICATION_ID, builder.build())
        }
    }
}
