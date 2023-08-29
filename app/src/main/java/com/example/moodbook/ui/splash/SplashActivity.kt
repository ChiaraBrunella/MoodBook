package com.example.moodbook.ui.splash

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
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
import com.example.moodbook.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding


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
            val i_logged = Intent(this@SplashActivity, MainActivity::class.java)
            val p =  listOf<Pair<View, String>> (
           Pair<View, String>(image, "logoimage"))
            val o = ActivityOptions.makeSceneTransitionAnimation(this@SplashActivity, p[0])
           /*val sharedPref = getSharedPreferences("com.example.moodbook", Context.MODE_PRIVATE)
            val prefEditor = sharedPref.edit()
            val sp_id = sharedPref.getString("loggedUser", null)
            if (sp_id == null) {
                //Open the login activity and set this so that next it value is 1 then this conditin will be false.*/
            startActivity(i, o.toBundle())
            /*   Log.i("per login: isLogged:, ", sp_id.toString())
            } else {
                //Open this Home activity
                Log.i("per home: isLogged:, ", sp_id.toString())
                startActivity(i_logged, o.toBundle())

            }*/

        }, SPLASH_SCREEN.toLong())
    }

    companion object {
        private const val SPLASH_SCREEN = 5000
    }
}