package com.janicolas.puzzlecollector.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import net.iessochoa.puzzlecollector.databinding.ActivityInfoBinding

class InfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ibBack.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
    }
}