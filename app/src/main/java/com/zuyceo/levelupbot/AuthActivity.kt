package com.zuyceo.levelupbot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AuthActivity : AppCompatActivity() {

    private val VALID_USERNAME = "bot"
    private val VALID_PASSWORD = "bot"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnConnect = findViewById<Button>(R.id.btnConnect)
        val tvError = findViewById<TextView>(R.id.tvError)
        val cardAuth = findViewById<LinearLayout>(R.id.cardAuth)

        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        cardAuth.startAnimation(slideUp)

        btnConnect.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString()

            if (username == VALID_USERNAME && password == VALID_PASSWORD) {
                tvError.visibility = View.GONE
                btnConnect.text = "✓ CONNECTED"
                btnConnect.isEnabled = false

                it.postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }, 600)
            } else {
                tvError.text = getString(R.string.error_wrong_creds)
                tvError.visibility = View.VISIBLE

                val shake = android.view.animation.AnimationUtils.loadAnimation(
                    this, android.R.anim.cycle_interpolator
                )
                cardAuth.animate()
                    .translationX(16f).setDuration(60)
                    .withEndAction {
                        cardAuth.animate().translationX(-16f).setDuration(60)
                            .withEndAction {
                                cardAuth.animate().translationX(8f).setDuration(60)
                                    .withEndAction {
                                        cardAuth.animate().translationX(0f).setDuration(60).start()
                                    }.start()
                            }.start()
                    }.start()
            }
        }
    }

    override fun onBackPressed() {
    }
}
