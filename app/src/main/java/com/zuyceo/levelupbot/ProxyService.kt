package com.zuyceo.levelupbot

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ProxyService : Service() {

    private var socks5Server: Socks5Server? = null
    private val CHANNEL_ID = "levelupbot_channel"
    private val NOTIFICATION_ID = 1

    companion object {
        const val ACTION_START = "com.zuyceo.levelupbot.START"
        const val ACTION_STOP = "com.zuyceo.levelupbot.STOP"
        const val BROADCAST_STATUS = "com.zuyceo.levelupbot.STATUS"
        const val EXTRA_RUNNING = "is_running"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startProxy()
            ACTION_STOP -> stopProxy()
        }
        return START_STICKY
    }

    private fun startProxy() {
        startForeground(NOTIFICATION_ID, buildNotification("Proxy running..."))
        socks5Server = Socks5Server()
        socks5Server?.start()
        broadcastStatus(true)
    }

    private fun stopProxy() {
        socks5Server?.stop()
        socks5Server = null
        broadcastStatus(false)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun broadcastStatus(running: Boolean) {
        val intent = Intent(BROADCAST_STATUS)
        intent.putExtra(EXTRA_RUNNING, running)
        sendBroadcast(intent)
    }

    private fun buildNotification(message: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("⚡ Level Up Bot")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_menu_share)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Level Up Bot Service",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "SOCKS5 Proxy Service"
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        socks5Server?.stop()
        super.onDestroy()
    }
}
