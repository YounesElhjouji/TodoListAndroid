package com.example.todocompose

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.util.Log
import androidx.core.app.NotificationCompat
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream


class NotificationService(
    private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(quiz: JSONObject) {
        Log.i(TAG, "Creating notification to show")
        val word = quiz.getString("word")
        val example = quiz.getString("example")
        val translated = quiz.getString("translated")
        val candidates = quiz.getJSONArray("candidates")
        val isSolved = isQuizSolved(candidates)
        val text = if (isSolved)  example+"\n"+translated else example
        val builder = NotificationCompat.Builder(context, MAIN_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_quiz_24)
            .setChannelId(MAIN_CHANNEL_ID)
            .setContentTitle(word)
            .setContentText(text)

        if (!isSolved){
            getActions(quiz).forEach {
                builder.addAction(it)
            }
        }

        notificationManager.notify(
            NOTIFICATION_ID,
            builder.build()
        )
        Log.i(TAG, "Showed notification for quiz $quiz")
    }


    fun isQuizSolved(candidates: JSONArray): Boolean {
        for (i in 0 until candidates.length()) {
            val candidate = candidates.getJSONObject(i)
            val isTrue = candidate.getBoolean("isTrue")
            val isGuessed = candidate.getBoolean("isGuessed")
            if (isTrue && isGuessed) {
                return true
            }
        }
        return false
    }


    private fun getActions(quiz: JSONObject): MutableList<NotificationCompat.Action> {
        val candidates = quiz.getJSONArray("candidates")
        val actions = mutableListOf<NotificationCompat.Action>()
        for (i in 0 until candidates.length()) {
            val candidate = candidates.getJSONObject(i)
            val word = candidate.getString("word")
            val isTrue = candidate.getBoolean("isTrue")
            val isGuessed = candidate.getBoolean("isGuessed")
            val answerIntent = Intent(context, AnswerReceiver::class.java)
            answerIntent.putExtra("candidate", word)
            answerIntent.putExtra("isTrue", isTrue)
            answerIntent.putExtra("quiz", quiz.toString())
            val pendingAnswerIntent = PendingIntent.getBroadcast(
                context,
                i + 40,
                answerIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val spannable = SpannableString(word).apply {
                if (isGuessed && !isTrue){
                    setSpan(
                        StrikethroughSpan(),
                        0,
                        word.length,
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            val action = NotificationCompat.Action(
                R.drawable.baseline_question_answer_24,
                spannable,
                pendingAnswerIntent
            )
            actions.add(action)
        }
        return actions
    }

    fun dismissNotification(){
        notificationManager.cancel(NOTIFICATION_ID)
    }

    fun getInitialQuiz(): JSONObject {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.dummy_quiz)
        val jsonText = inputStream.bufferedReader().use { it.readText() }
        return JSONObject(jsonText)
    }

    companion object {
        const val MAIN_CHANNEL_ID = "main_channel"
        const val NOTIFICATION_ID = 14
        private const val TAG = "NotifSvc"
    }
}