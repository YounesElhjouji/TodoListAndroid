package com.example.todocompose

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import org.json.JSONObject

class AnswerReceiver: BroadcastReceiver() {

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
                scheduleAnnotationRemoval(service)
            }
        }

    }

    private fun scheduleAnnotationRemoval(notificationService: NotificationService){
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            notificationService.dismissNotification()
        }
        handler.postDelayed(runnable, 3000L)
    }

    private fun checkGuessed(quiz: JSONObject, choice: String): JSONObject{
        val candidates = quiz.getJSONArray("candidates")
        for (i in 0 until candidates.length()) {
            val candidate = candidates.getJSONObject(i)
            val word = candidate.getString("word")
            if (choice.lowercase() == word.lowercase()){
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