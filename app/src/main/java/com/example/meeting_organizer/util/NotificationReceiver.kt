//package com.example.meeting_organizer.util
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import androidx.core.app.NotificationManagerCompat
//
//class NotificationReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
//        val notification = intent.getParcelableExtra<android.app.Notification>(NOTIFICATION)
//        notification?.let {
//            NotificationManagerCompat.from(context).notify(notificationId, it)
//        }
//    }
//
//    companion object {
//        const val NOTIFICATION_ID = "notification_id"
//        const val NOTIFICATION = "notification"
//    }
//}