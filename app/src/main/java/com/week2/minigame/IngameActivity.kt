package com.week2.minigame

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.week2.minigame.databinding.ActivityIngameBinding

class IngameActivity : AppCompatActivity(){
    private lateinit var binding: ActivityIngameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIngameBinding.inflate(layoutInflater)

        setContentView(binding.root)


    }
}