package com.zuyceo.levelupbot

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DURATION = 5000L
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val tvAppName = findViewById<TextView>(R.id.tvAppName)
        val tvTagline = findViewById<TextView>(R.id.tvTagline)
        val tvLoading = findViewById<TextView>(R.id.tvLoading)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in)

        tvAppName.startAnimation(bounceIn)

        handler.postDelayed({
            tvTagline.animate().alpha(1f).setDuration(600).start()
        }, 700)

        val loadingMessages = listOf(
            "Initializing...",
            "Loading modules...",
            "Setting up proxy engine...",
            "Almost ready...",
            "Welcome!"
        )

        var progress = 0
        val totalSteps = 100
        val stepDelay = SPLASH_DURATION / totalSteps
        var msgIndex = 0

        val progressRunnable = object : Runnable {
            override fun run() {
                if (progress < totalSteps) {
                    progress++
                    progressBar.progress = progress

                    val newMsgIndex = (progress / 25).coerceAtMost(loadingMessages.size - 1)
                    if (newMsgIndex != msgIndex) {
                        msgIndex = newMsgIndex
                        tvLoading.text = loadingMessages[msgIndex]
                    }

                    handler.postDelayed(this, stepDelay)
                }
            }
        }
        handler.post(progressRunnable)

        handler.postDelayed({
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, SPLASH_DURATION)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
