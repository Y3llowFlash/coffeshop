package com.example.coffeeshopapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AnalyticsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            navigateToLogin()
            return
        }

        setContentView(createContentView())
    }

    private fun createContentView(): LinearLayout {
        val currentUser = auth.currentUser

        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(48, 48, 48, 48)

            addView(TextView(context).apply {
                text = "Analytics"
                textSize = 28f
                gravity = Gravity.CENTER
            })

            addView(TextView(context).apply {
                text = currentUser?.displayName ?: currentUser?.email ?: "Signed in user"
                textSize = 18f
                gravity = Gravity.CENTER
                setPadding(0, 24, 0, 24)
            })

            addView(Button(context).apply {
                text = "Open Coffee Shop"
                setOnClickListener {
                    startActivity(Intent(this@AnalyticsActivity, MainActivity::class.java))
                }
            })

            addView(Button(context).apply {
                text = "Sign Out"
                setOnClickListener {
                    auth.signOut()
                    navigateToMain()
                }
            })
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
