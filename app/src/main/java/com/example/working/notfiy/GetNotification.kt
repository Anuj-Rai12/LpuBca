package com.example.working.notfiy

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.working.MainActivity3
import com.example.working.R
import com.example.working.loginorsignup.TAG
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class GetNotification : FirebaseMessagingService() {
    private var notificationManager: NotificationManager? = null
    private val channelId = "com.example.working"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)
        notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        msg.notification?.let { notification ->
            val title =notification.title?:"No title"
            val text=notification.body?:"No MESSAGE"
            Log.i(TAG, "onMessageReceived: ${notification.imageUrl}")
            Log.i(TAG, "onMessageReceived: ${notification.icon}")
            Log.i(TAG, "onMessageReceived: ${notification.color}")
            createNotification(title,text,msg)
            Log.i(TAG, "onMessageReceived: Data= ${msg.data["Key1"]}")
            return
        }

        Log.i(TAG,"onMessageReceived: TITTLE IS Null")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    private fun createNotification(
        name: String,
        desc: String = "No data Found",
        msg: RemoteMessage,
    ) {
        val notificationId = 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH).apply {
                    description = desc
                    enableLights(true)
                    lightColor = Color.GREEN
                }
            notificationManager?.createNotificationChannel(channel)
        }
        val intent = Intent(applicationContext, MainActivity3::class.java)
        intent.putExtra("Key1",msg.data["Key1"])
        val pendingIntent = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(101, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(name)
            .setContentText(desc)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(this, R.color.my_color))
            .setSmallIcon(R.drawable.book_icon)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
        notificationManager?.notify(notificationId, notification)
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.i(TAG, "onNewToken: $p0")
    }
}