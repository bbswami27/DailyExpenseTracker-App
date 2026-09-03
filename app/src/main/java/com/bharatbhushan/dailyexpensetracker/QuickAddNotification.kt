package com.bharatbhushan.dailyexpensetracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bharatbhushan.dailyexpensetracker.R

object QuickAddNotification {
    const val EXTRA_ACTION = "ghar_kharch_quick_action"
    const val ACTION_EXPENSE = "add_expense"
    const val ACTION_INCOME = "add_income"

    private const val CHANNEL_ID = "ghar_kharch_quick_entry"
    private const val NOTIFICATION_ID = 1702

    fun show(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Quick Cash In and Cash Out Entry",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Notification से expense या income जल्दी जोड़ें"
                    setShowBadge(false)
                }
            )
        }

        val homeIntent = actionIntent(context, null, 1700)
        val expenseIntent = actionIntent(context, ACTION_EXPENSE, 1701)
        val incomeIntent = actionIntent(context, ACTION_INCOME, 1702)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Daily Expense Tracker Quick Entry")
            .setContentText("Cash Out या Cash In तुरंत जोड़ें")
            .setContentIntent(homeIntent)
            .addAction(0, "Cash Out", expenseIntent)
            .addAction(0, "Cash In", incomeIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun actionIntent(
        context: Context,
        action: String?,
        requestCode: Int
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            action?.let { putExtra(EXTRA_ACTION, it) }
        }
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
