package com.example.moodbook.ui.splash

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.moodbook.LoginActivity
import com.example.moodbook.MainActivity
import com.example.moodbook.R



class SplashActivity : AppCompatActivity() {


    var top: Animation? = null
    var bottom: Animation? = null
    lateinit var image: ImageView
    lateinit var t: TextView


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)
        top = AnimationUtils.loadAnimation(this, R.anim.slide_anim)
        bottom = AnimationUtils.loadAnimation(this, R.anim.bottomanimation)
        image = findViewById(R.id.splashlogo)
        t = findViewById(R.id.sub)
        image.setAnimation(top)
        t.setAnimation(bottom)
        Handler().postDelayed({
            val i = Intent(this@SplashActivity, LoginActivity::class.java)
            val p =  listOf<Pair<View, String>> (
           Pair<View, String>(image, "logoimage"))
            val o = ActivityOptions.makeSceneTransitionAnimation(this@SplashActivity, p[0])
            startActivity(i, o.toBundle())

        }, SPLASH_SCREEN.toLong())
    }

    companion object {
        private const val SPLASH_SCREEN = 5000
    }
}