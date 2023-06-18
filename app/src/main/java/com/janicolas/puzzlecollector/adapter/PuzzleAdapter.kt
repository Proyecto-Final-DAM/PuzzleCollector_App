package com.janicolas.puzzlecollector.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.janicolas.puzzlecollector.model.ResponsePuzzle
import net.iessochoa.puzzlecollector.databinding.PuzzleCardBinding
import java.io.ByteArrayInputStream
import java.io.InputStream

class PuzzleAdapter(private var puzzles: List<ResponsePuzzle>?, private val dialogType: Int):
    RecyclerView.Adapter<PuzzleAdapter.PuzzleViewHolder>() {

    var onPuzzleClickListener: OnPuzzleClickListener?=null

    inner class PuzzleViewHolder(val binding: PuzzleCardBinding)
        : RecyclerView.ViewHolder(binding.root){
        init {
            binding.puzzleCard.setOnClickListener{
                val id = binding.puzzleId.text.toString().toLong()
                onPuzzleClickListener?.onPuzzleClick(id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PuzzleViewHolder {
        val binding = PuzzleCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PuzzleViewHolder(binding)
    }

    override fun onBindViewHolder(puzzleHolder: PuzzleViewHolder, pos: Int) {
        with(puzzleHolder) {
            with(puzzles!![pos]) {
                binding.puzzleId.text = id.toString()

                val decodedString: ByteArray = Base64.decode(puzzleImg, Base64.DEFAULT)
                val inputStream: InputStream = ByteArrayInputStream(decodedString)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.puzzleImg.setImageBitmap(bitmap)

                binding.tvName.text = name

                binding.tvPrice.text = String.format("%.2f €", price)
                if(dialogType != 1)
                    binding.tvPrice.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = puzzles?.size?:0

    interface OnPuzzleClickListener {
        fun onPuzzleClick(id: Long)
    }
}