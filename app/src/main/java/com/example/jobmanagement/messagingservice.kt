package com.example.jobmanagement

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")

        // Send token in background
        CoroutineScope(Dispatchers.IO).launch {
            sendTokenToServer(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle the notification payload
        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }

        // Handle data messages if any
        if (remoteMessage.data.isNotEmpty()) {
            // You can extract data here and do something with it
            val jobTitle = remoteMessage.data["jobTitle"]
            val jobDescription = remoteMessage.data["jobDescription"]
            // Process the data here if needed
        }
    }

    private fun showNotification(title: String?, body: String?) {
        val channelId = "job_alerts_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Check for notification permissions on Android 13+ (Tiramisu)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
//                != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this,
//                    arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
//            }
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Creating the notification channel
            val channel = NotificationChannel(
                channelId,
                "Job Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for job posts and applications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.bell)
            .setContentTitle(title ?: "New Notification")
            .setContentText(body ?: "New job post available!")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Use a unique ID to avoid overwriting notifications
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private suspend fun sendTokenToServer(token: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Log.e("FCM", "UID not found. Cannot send FCM token.")
            return
        }

        // Endpoint for sending token (replace with your actual endpoint)
        val url = "http://192.168.71.52:5000/api/candidates/$uid/token"

        val json = JSONObject().apply {
            put("fcmToken", token)
        }

        val requestBody = RequestBody.create("application/json".toMediaType(), json.toString())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = OkHttpClient().newCall(request).execute()
            if (response.isSuccessful) {
                Log.d("FCM", "Token sent to candidate endpoint successfully.")
            } else {
                Log.e("FCM", "Failed to send token: ${response.message}")
            }
        } catch (e: Exception) {
            Log.e("FCM", "Error sending token: ${e.message}")
        }
    }
}
