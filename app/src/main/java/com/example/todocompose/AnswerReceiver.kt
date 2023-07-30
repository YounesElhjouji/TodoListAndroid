package com.example.todocompose

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AnswerReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        intent!!
        val service = NotificationService(context)
        val candidate = intent.getStringExtra("candidate")
        val isTrue = intent.getBooleanExtra("isTrue", false)
        Log.i("NotRec", "Got answer $candidate which is $isTrue")
        if (isTrue) {
            service.onCorrectAnswer()
        }
    }
}