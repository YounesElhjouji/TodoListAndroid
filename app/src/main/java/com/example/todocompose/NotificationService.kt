package com.example.todocompose

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat


class NotificationService(
    private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification() {
        Log.i(TAG, "Creating notification to show")
        val text = "Je suis Perdu"
        val builder = NotificationCompat.Builder(context, MAIN_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_quiz_24)
            .setChannelId(MAIN_CHANNEL_ID)
            .setContentTitle("Perdu")
            .setContentText(text)

        getActions(listOf("High", "Fried", "Lost")).forEach {
            builder.addAction(it)
        }

        notificationManager.notify(
            1,
            builder.build()
        )
        Log.i(TAG, "Showed notification first time")
    }

    fun onCorrectAnswer() {
        val builder = NotificationCompat.Builder(context, MAIN_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_quiz_24)
            .setChannelId(MAIN_CHANNEL_ID)
            .setContentTitle("Perdu")
            .setContentText("Je suis perdu\nI am lost")
        notificationManager.notify(
            1,
            builder.build()
        )
        Log.i(TAG, "Updated notification with correct answer")

    }

    private fun getActions(candidates: List<String>): MutableList<NotificationCompat.Action> {
        val actions = mutableListOf<NotificationCompat.Action>()
        candidates.forEachIndexed { i, candidate ->
            val answerIntent = Intent(context, AnswerReceiver::class.java)
            answerIntent.putExtra("candidate", candidate)
            answerIntent.putExtra("isTrue", (candidate == "Lost"))
            val pendingAnswerIntent = PendingIntent.getBroadcast(
                context,
                i + 3,
                answerIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val action = NotificationCompat.Action(
                R.drawable.baseline_question_answer_24,
                candidate,
                pendingAnswerIntent
            )
            actions.add(action)
        }
        return actions
    }

    companion object {
        const val MAIN_CHANNEL_ID = "main_channel"
        private const val TAG = "NotifSvc"
    }
}