package com.example.todocompose

import ServerInterface
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class AnswerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val service = NotificationService(context)
        val candidate = intent.getStringExtra("candidate")
        val isTrue = intent.getBooleanExtra("isTrue", false)
        val quizString = intent.getStringExtra("quiz")

        Log.i(TAG, "Got answer $candidate which is $isTrue")
        if (quizString != null && candidate != null) {
            var quiz = JSONObject(quizString)
            if (service.isQuizSolved(quiz.getJSONArray("candidates"))) {
                return
            }
            quiz = checkGuessed(quiz, candidate)
            service.showNotification(quiz)
            if (service.isQuizSolved(quiz.getJSONArray("candidates"))) {
                val notificationId = quiz.getInt("notificationId")
                scheduleAnnotationRemoval(service, notificationId)
                sendQuizResultToServer(context, quiz)
            }
        }
    }

    fun getCurrentToken(context: Context): String {
        val sharedPreference = context.getSharedPreferences(
            FCMTokenService.SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        val token: String? = sharedPreference.getString("token", null)
        return token ?: "TOKEN_NOT_FOUND"
    }

    private fun sendQuizResultToServer(
        context: Context, quiz: JSONObject
    ) {
        val token = getCurrentToken(context)
        quiz.put("token", token)
        ServerInterface.sendQuizResult(quiz, object : ServerInterface.ServerCallback {
            override fun onSuccess(response: Response) {
                Log.i(TAG, "Quiz result sent to server successfully")
            }
            override fun onFailure(exception: IOException) {
                Log.e(TAG, "Quiz result sending to server failed")
                exception.printStackTrace()
            }
        })
    }

    private fun scheduleAnnotationRemoval(
        notificationService: NotificationService,
        notificationID: Int
    ) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            notificationService.dismissNotification(notificationID)
        }
        handler.postDelayed(runnable, 10000L)
    }

    private fun checkGuessed(quiz: JSONObject, choice: String): JSONObject {
        val candidates = quiz.getJSONArray("candidates")
        for (i in 0 until candidates.length()) {
            val candidate = candidates.getJSONObject(i)
            val word = candidate.getString("word")
            if (choice.lowercase() == word.lowercase()) {
                Log.i(TAG, "Marking word $word guessed")
                candidate.put("isGuessed", true)
            }
        }
        return quiz
    }

    companion object {
        private const val TAG = "NotifReceiver"
    }
}