package com.week2.minigame

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.week2.minigame.databinding.ActivityPauseBinding

class PauseActivity : AppCompatActivity(){
    private lateinit var binding : ActivityPauseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPauseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val resumebtn = binding.btnPauseResume
        val exitbtn = binding.btnPauseExit

        resumebtn.setOnClickListener {
            finish()
        }

        exitbtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}