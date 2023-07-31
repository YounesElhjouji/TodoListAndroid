package com.example.todocompose

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannedString
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.text.buildSpannedString
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
        val notificationId = quiz.getInt("notificationId")
        val word = quiz.getString("word")
        val example = quiz.getJSONObject("example")
        val translated = quiz.getJSONObject("translated")
        val candidates = quiz.getJSONArray("candidates")
        val isSolved = isQuizSolved(candidates)
        val text = getNotificationText(example, translated, isSolved)
        val builder = NotificationCompat.Builder(context, MAIN_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_quiz_24)
            .setChannelId(MAIN_CHANNEL_ID)
            .setContentTitle(word)
            .setContentText(text)

        if (!isSolved) {
            getActions(quiz).forEach {
                builder.addAction(it)
            }
        }

        notificationManager.notify(
            notificationId,
            builder.build()
        )
        Log.i(TAG, "Showed notification for quiz $quiz")
    }

    private fun getNotificationText(
        example: JSONObject,
        translated: JSONObject,
        isSolved: Boolean
    ): SpannedString {
        val styledExample = getStyledSentence(example)
        if (isSolved) {
            val styledTranslated = getStyledSentence(translated)
            return buildSpannedString {
                append(styledExample)
                append("\n")
                append(styledTranslated)
            }
        }
        return SpannedString.valueOf(styledExample)
    }

    private fun getStyledSentence(example: JSONObject): SpannableString {
        val sentence = example.getString("sentence")
        val start = example.getInt("start")
        val end = example.getInt("end")
        val style = StyleSpan(Typeface.BOLD)
        val spannable = SpannableString(sentence)
        spannable.setSpan(style, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
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
                if (isGuessed && !isTrue) {
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

    fun dismissNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    private fun generateNotificationId(): Int {
        val timestamp = System.currentTimeMillis()
        val randomNumber = (0..1000).random()
        return (timestamp + randomNumber).toInt()
    }

    fun getInitialQuiz(): JSONObject {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.dummy_quiz)
        val jsonText = inputStream.bufferedReader().use { it.readText() }
        val quiz = JSONObject(jsonText)
        quiz.put("notificationId", generateNotificationId())
        return quiz
    }

    companion object {
        const val MAIN_CHANNEL_ID = "main_channel"
        private const val TAG = "NotifSvc"
    }
}