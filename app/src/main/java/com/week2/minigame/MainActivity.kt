package com.week2.minigame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.week2.minigame.databinding.ActivityMainBinding




class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val img = binding.ivMainHeader
        val btn = binding.btnMainStart

        Glide.with(this)
            .load(R.drawable.gameheader)
            .into(img)

        btn.setOnClickListener {
            val intent = Intent(this, IngameActivity::class.java)
            startActivity(intent)
        }
    }
}