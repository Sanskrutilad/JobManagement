package com.example.jobmanagement

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")

        // Send this token to your server
        sendTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
    }

    private fun showNotification(title: String?, body: String?) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "default_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.bell)
            .setAutoCancel(true)

        notificationManager.notify(0, builder.build())
    }

    // Send the FCM token to the server for storage
    private fun sendTokenToServer(token: String) {
        val url = "https://your-backend-api-url/api/candidates/token" // Use the appropriate URL for your API
        val jsonObject = JSONObject()
        jsonObject.put("fcmToken", token)

        val client = OkHttpClient()
        val requestBody = RequestBody.create("application/json".toMediaType(), jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            val responseMessage = response.message // Changed to val to store the response message
            if (response.isSuccessful) {
                Log.d("FCM", "Token sent successfully to server")
            } else {
                Log.e("FCM", "Failed to send token to server: $responseMessage")
            }
        }
    }
}
