package com.janicolas.puzzlecollector.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import net.iessochoa.puzzlecollector.databinding.ActivityWishlistBinding

class WishlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWishlistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}