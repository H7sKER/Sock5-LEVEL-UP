package com.zuyceo.levelupbot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var isRunning = false
    private lateinit var btnStartStop: Button
    private lateinit var tvStatusBadge: TextView
    private lateinit var tvStatusText: TextView
    private lateinit var tvActionHint: TextView
    private lateinit var glowRing: View

    private val statusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val running = intent?.getBooleanExtra(ProxyService.EXTRA_RUNNING, false) ?: false
            updateUI(running)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStartStop = findViewById(R.id.btnStartStop)
        tvStatusBadge = findViewById(R.id.tvStatusBadge)
        tvStatusText = findViewById(R.id.tvStatusText)
        tvActionHint = findViewById(R.id.tvActionHint)
        glowRing = findViewById(R.id.glowRing)

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        btnStartStop.startAnimation(fadeIn)

        btnStartStop.setOnClickListener {
            if (isRunning) {
                stopProxy()
            } else {
                startProxy()
            }
        }

        val btnAbout = findViewById<TextView>(R.id.btnAbout)
        btnAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
            overridePendingTransition(R.anim.slide_up, android.R.anim.fade_out)
        }

        registerReceiver(
            statusReceiver,
            IntentFilter(ProxyService.BROADCAST_STATUS),
            RECEIVER_NOT_EXPORTED
        )

        updateUI(false)
    }

    private fun startProxy() {
        val intent = Intent(this, ProxyService::class.java)
        intent.action = ProxyService.ACTION_START
        startForegroundService(intent)

        btnStartStop.isEnabled = false
        btnStartStop.text = "..."
        btnStartStop.postDelayed({ btnStartStop.isEnabled = true }, 1500)
    }

    private fun stopProxy() {
        val intent = Intent(this, ProxyService::class.java)
        intent.action = ProxyService.ACTION_STOP
        startService(intent)

        btnStartStop.isEnabled = false
        btnStartStop.postDelayed({ btnStartStop.isEnabled = true }, 1500)
    }

    private fun updateUI(running: Boolean) {
        isRunning = running
        if (running) {
            btnStartStop.text = "STOP"
            btnStartStop.background = getDrawable(R.drawable.bg_button_stop)
            tvStatusBadge.text = getString(R.string.status_connected)
            tvStatusBadge.setTextColor(getColor(R.color.green_online))
            tvStatusBadge.background = getDrawable(R.drawable.bg_status_connected)
            tvStatusText.text = "Active"
            tvStatusText.setTextColor(getColor(R.color.green_online))
            tvActionHint.text = "Tap to stop proxy"
            glowRing.animate().alpha(0.9f).setDuration(400).start()

            val pulse = AnimationUtils.loadAnimation(this, R.anim.pulse)
            glowRing.startAnimation(pulse)
        } else {
            btnStartStop.text = "START"
            btnStartStop.background = getDrawable(R.drawable.bg_button_start)
            tvStatusBadge.text = getString(R.string.status_disconnected)
            tvStatusBadge.setTextColor(getColor(R.color.red_offline))
            tvStatusBadge.background = getDrawable(R.drawable.bg_status_disconnected)
            tvStatusText.text = "Idle"
            tvStatusText.setTextColor(getColor(R.color.text_grey))
            tvActionHint.text = "Tap to start proxy"
            glowRing.clearAnimation()
            glowRing.animate().alpha(0f).setDuration(400).start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(statusReceiver) } catch (e: Exception) { }
    }
}
