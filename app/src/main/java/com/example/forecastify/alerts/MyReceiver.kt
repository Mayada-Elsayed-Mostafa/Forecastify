package com.example.forecastify.alerts

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.forecastify.MainActivity
import com.example.forecastify.R
import com.example.forecastify.utils.NotificationConstants
import java.util.concurrent.TimeUnit

class MyReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = NotificationManagerCompat.from(context)
        when (intent.action) {
            NotificationConstants.ACTION_CANCEL -> {
                notificationManager.cancel(NotificationConstants.NOTIFICATION_ID)
            }

            NotificationConstants.ACTION_DISMISS -> {
                val description = intent.getStringExtra("description") ?: "Tap to open the app"
                val updatedBuilder = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
                    .setSmallIcon(R.drawable.cloudy_icon)
                    .setContentTitle("Check Weather Condition!")
                    .setContentText(description)
                    .setContentIntent(
                        PendingIntent.getActivity(
                            context,
                            NotificationConstants.REQUEST_CODE_MAIN,
                            Intent(context, MainActivity::class.java),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                    .setOnlyAlertOnce(true)
                    .setSilent(true)
                    .setAutoCancel(true)
                notificationManager.notify(NotificationConstants.NOTIFICATION_ID, updatedBuilder.build())
            }

            NotificationConstants.ACTION_SNOOZE -> {
                notificationManager.cancel(NotificationConstants.NOTIFICATION_ID)
                val description = intent.getStringExtra("description") ?: "Check the weather again!"

                val snoozeWorkRequest = OneTimeWorkRequestBuilder<AlertWorker>()
                    .setInitialDelay(1, TimeUnit.MINUTES)
                    .setInputData(workDataOf("description" to description))
                    .build()

                WorkManager.getInstance(context).enqueue(snoozeWorkRequest)
            }
        }
    }
}
