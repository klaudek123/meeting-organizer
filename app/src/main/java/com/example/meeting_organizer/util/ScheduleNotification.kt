//package com.example.meeting_organizer.util
//
//import android.app.AlarmManager
//import android.app.PendingIntent
//import android.app.TaskStackBuilder
//import android.content.Context
//import android.content.Intent
//import android.os.SystemClock
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import com.example.meeting_organizer.R
//import com.example.meeting_organizer.ui.MainActivity
//
//fun ScheduleNotification(context: Context, title: String, timeInMillis: Long) {
//    val intent = Intent(context, MainActivity::class.java).apply {
//        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//    }
//    val pendingIntent = TaskStackBuilder.create(context).run {
//        addNextIntentWithParentStack(intent)
//        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
//    }
//
//    val notification = NotificationCompat.Builder(context, "meeting_channel")
//        .setSmallIcon(R.drawable.ic_launcher_foreground)
//        .setContentTitle("Upcoming Meeting")
//        .setContentText(title)
//        .setPriority(NotificationCompat.PRIORITY_HIGH)
//        .setContentIntent(pendingIntent)
//        .setAutoCancel(true)
//        .build()
//
//    val notificationManager = NotificationManagerCompat.from(context)
//
//    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//    val notificationIntent = Intent(context, NotificationReceiver::class.java).apply {
//        putExtra(NotificationReceiver.NOTIFICATION_ID, 1)
//        putExtra(NotificationReceiver.NOTIFICATION, notification)
//    }
//    val alarmPendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//    alarmManager.setExact(
//        AlarmManager.RTC_WAKEUP,
//        SystemClock.elapsedRealtime() + timeInMillis - System.currentTimeMillis(),
//        alarmPendingIntent
//    )
//}
