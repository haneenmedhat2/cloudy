package com.example.cloudy.onboarding
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.airbnb.lottie.LottieAnimationView
import com.example.cloudy.R


class MainActivity : AppCompatActivity() {
    lateinit var animationView: LottieAnimationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        animationView = findViewById(R.id.animationView)
        supportActionBar?.hide()
        Handler().postDelayed({
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }, 4000)
    }
}