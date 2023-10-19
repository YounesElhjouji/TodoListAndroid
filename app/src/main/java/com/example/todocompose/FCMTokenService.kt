package com.example.todocompose

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class FCMTokenService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "Refreshed token :: $token")
        saveTokenLocally(token)
        sendRegistrationToServer(token)
    }

    private fun saveTokenLocally(token: String) {
        val sharedPreference =  getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("token", token)
        editor.apply()
    }

    private fun sendRegistrationToServer(token: String) {
        ServerInterface.sendRegistrationToken(token, object : ServerInterface.ServerCallback {
            override fun onSuccess(response: Response) {
                Log.i(TAG, "Token registration done successfully")
            }
            override fun onFailure(exception: IOException) {
                Log.e(TAG, "Token registration failed")
                exception.printStackTrace()
            }
        })
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationService = NotificationService(this)

        Log.d(TAG, "From: " + remoteMessage.from)
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }
        val data = remoteMessage.data
        if (data.containsKey("quiz")) {
            val quizJsonString = data["quiz"]
                ?: throw Exception("Expected quiz to be string attribute of FCM message")
            val quiz = convertStringToJson(quizJsonString)
            if (quiz != null) {
                Log.i(TAG, "Received Quiz JSON: $quiz")
                if (!notificationService.isNotificationActive()) {
                    notificationService.showNotification(quiz)
                } else {
                    Log.i(TAG, "Ignoring notification because another one is already shown")
                }

            } else {
                Log.e(TAG, "Failed to parse Quiz JSON")
            }
        }
        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body)
        }
    }

    private fun convertStringToJson(jsonString: String): JSONObject? {
        return try {
            JSONObject(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error converting JSON String to JSONObject: ${e.message}")
            null
        }
    }

    companion object {
        private const val TAG = "FCMTokenService"
        const val SHARED_PREFERENCE_NAME = "FCM_VALUES"
    }
}