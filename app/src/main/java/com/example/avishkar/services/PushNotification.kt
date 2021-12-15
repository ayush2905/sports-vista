package com.example.avishkar.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.avishkar.App
import com.example.avishkar.R
import com.example.avishkar.activity.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class PushNotification : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, HomeActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification: Notification = NotificationCompat.Builder(this, App.CHANNEL_MESSAGE_ID)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle(remoteMessage.data["title"])
            .setContentText(remoteMessage.data["body"])
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        manager.notify(1, notification)
    }

    override fun onNewToken(s: String) {
        saveToken(s)
    }

    companion object {
        fun saveToken(token: String?) {
            if (FirebaseAuth.getInstance().currentUser == null) return
            FirebaseDatabase.getInstance()
                .reference
                .child("token")
                .child(FirebaseAuth.getInstance().uid!!)
                .setValue(token)
        }
    }
}