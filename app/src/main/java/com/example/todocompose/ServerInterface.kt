import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

object ServerInterface {

    private val client = OkHttpClient()
    private val baseUrl = "https://linguify-production.up.railway.app"

    fun sendRegistrationToken(token: String, callback: ServerCallback) {
        val url = "$baseUrl/token"

        val json = JSONObject()
        json.put("token", token)

        val requestBody = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback.onSuccess(response)
                } else {
                    callback.onFailure(IOException("Token registration failed with code ${response.code}"))
                }
                response.close()
            }
        })
    }

    fun sendQuizResult(quiz: JSONObject, callback: ServerCallback) {
        val url = "$baseUrl/quiz_result"

        val requestBody = quiz.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback.onSuccess(response)
                } else {
                    callback.onFailure(IOException("Sending quiz result failed with code ${response.code}"))
                }
                response.close()
            }
        })
    }

    interface ServerCallback {
        fun onSuccess(response: Response)
        fun onFailure(exception: IOException)
    }
}