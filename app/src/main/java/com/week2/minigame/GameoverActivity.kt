package com.week2.minigame

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.week2.minigame.databinding.ActivityGameoverBinding

class GameoverActivity : AppCompatActivity(){
    private lateinit var binding : ActivityGameoverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameoverBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val exit = binding.btnGameoverExit
        val retry = binding.btnGameoverRetry

        exit.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        retry.setOnClickListener {
            val intent = Intent(this, IngameActivity::class.java)
            startActivity(intent)
        }
    }
}