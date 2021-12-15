package com.company.avishkar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.company.avishkar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
         const val SPLASH_SCREEN = 4000     //5 seconds
    }

    lateinit var binding : ActivityMainBinding
    lateinit var topAnim : Animation
    lateinit var bottomAnim : Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //animations
        topAnim = AnimationUtils.loadAnimation(this , R.anim.top_animation)
        bottomAnim = AnimationUtils.loadAnimation(this , R.anim.bottom_animation)

        binding.logo.animation = topAnim
        binding.logoTagline.animation = bottomAnim

        //delaying opening of other activity
        Handler(Looper.getMainLooper()). postDelayed({
            val intent = Intent(this , LogIn::class.java)
            startActivity(intent)
            finish()                                  //finish -> to close the mainActivity(splash_screen)
        } , Companion.SPLASH_SCREEN.toLong())

    }

}