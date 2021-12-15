package com.company.avishkar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.company.avishkar.databinding.ActivityLogInBinding

class LogIn : AppCompatActivity() {

    lateinit var binding : ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginRegUserBtn.setOnClickListener{
            val intent = Intent(this , UserReg::class.java)
            startActivity(intent)
        }

        binding.loginRegSportsBtn.setOnClickListener{
            val intent = Intent(this , SportsReg::class.java)
            startActivity(intent)
        }

    }
}