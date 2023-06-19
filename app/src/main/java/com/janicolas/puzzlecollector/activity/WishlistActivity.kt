package com.janicolas.puzzlecollector.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.janicolas.puzzlecollector.activity.MainActivity.Companion.user
import com.janicolas.puzzlecollector.adapter.PuzzleAdapter
import com.janicolas.puzzlecollector.dialog.PuzzleDialog
import net.iessochoa.puzzlecollector.databinding.ActivityWishlistBinding

class WishlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWishlistBinding
    private lateinit var puzzleAdapter: PuzzleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ibBack.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.rvWishList.layoutManager = GridLayoutManager(this, 2)

        loadPuzzles()
    }

    fun loadPuzzles() {
        puzzleAdapter = PuzzleAdapter(user!!.puzzles, 1)
        puzzleAdapter.onPuzzleClickListener = object :
            PuzzleAdapter.OnPuzzleClickListener {
            override fun onPuzzleClick(id: Long) {
                PuzzleDialog(id, this@WishlistActivity, 0, this@WishlistActivity)
            }
        }
        binding.rvWishList.adapter = puzzleAdapter
    }
}