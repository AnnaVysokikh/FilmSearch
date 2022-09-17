package ru.otus.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ru.otus.presentation.MainActivity.Companion.NOTIFICATION_RECEIVER_ACTION
import ru.otus.R
import ru.otus.presentation.view.DescriptionFragment.Companion.EXTRA_FILM_ID
import ru.otus.presentation.view.DescriptionFragment.Companion.EXTRA_FILM_NAME

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("__OTUS__", "Watch later NotificationReceiver")
        context?.let { _context ->
            val film = intent?.getStringExtra(EXTRA_FILM_NAME)?:""
            val intentNew = Intent(NOTIFICATION_RECEIVER_ACTION,null,context, MainActivity::class.java)
            intentNew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intentNew.putExtra(EXTRA_FILM_ID, intent?.action)
            intentNew.putExtra(EXTRA_FILM_NAME, film)
            val pendingIntent = PendingIntent.getActivity(_context,intent?.action!!.toInt(), intentNew,PendingIntent.FLAG_UPDATE_CURRENT)
            val builder = NotificationCompat.Builder(_context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_local_movies_24)
                .setContentTitle(_context.getString(R.string.notification_title))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(NotificationCompat.BigTextStyle().bigText(_context.getString(R.string.watch_later_notification, film)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_baseline_local_movies_24, _context.getString(R.string.open_film), pendingIntent)
            val manager = NotificationManagerCompat.from(_context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val notificationChannel = NotificationChannel(
                    CHANNEL_ID,"Watch later", NotificationManager.IMPORTANCE_HIGH)

                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.GREEN
                notificationChannel.enableVibration(false)
                manager.createNotificationChannel(notificationChannel)
            }
            manager.notify(intent?.action!!.toInt(), builder.build())

            Log.d("__OTUS__", "notification sent ${intent.action}")
        }
    }

    companion object {
        const val CHANNEL_ID = "ReminderChannel"
    }
}