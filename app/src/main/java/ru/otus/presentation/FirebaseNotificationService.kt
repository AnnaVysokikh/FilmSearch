package ru.otus.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.otus.R
import java.lang.NumberFormatException
import ru.otus.presentation.view.DescriptionFragment.Companion.EXTRA_FILM_ID
import ru.otus.presentation.view.DescriptionFragment.Companion.EXTRA_FILM_NAME

class FirebaseNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("__OTUS__Firebase", "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("__OTUS__Firebase", "Message data: ${remoteMessage.data}")
        Log.d("__OTUS__Firebase", "Message Notification Body: ${remoteMessage.notification?.body}")
        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            sendNotification(remoteMessage)
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {

        var filmID = -1
        var filmName = " "
        try {
            filmID = remoteMessage.data["film_id"]!!.toInt()
            filmName = remoteMessage.data["film_name"]!!
        } catch (e:NumberFormatException){
            Firebase.crashlytics.recordException(e)
        }
        val intent = Intent(MainActivity.NOTIFICATION_RECEIVER_ACTION,null,this, MainActivity::class.java)

        intent.putExtra(EXTRA_FILM_NAME, filmName)
        intent.putExtra(EXTRA_FILM_ID, filmID)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, filmID, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val channelId = "firebase channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_baseline_local_movies_24)
            .setContentTitle(remoteMessage.notification?.title?: getText(R.string.notification_title))
            .setContentText(remoteMessage.notification?.body?:getText(R.string.notification_title))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(filmID, notificationBuilder.build())
    }
}