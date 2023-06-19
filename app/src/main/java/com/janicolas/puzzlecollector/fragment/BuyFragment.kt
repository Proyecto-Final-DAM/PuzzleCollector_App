package com.janicolas.puzzlecollector.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.janicolas.puzzlecollector.adapter.PuzzleAdapter
import com.janicolas.puzzlecollector.dialog.PuzzleDialog
import com.janicolas.puzzlecollector.model.ResponsePuzzle
import com.janicolas.puzzlecollector.retrofit.RetrofitService
import com.janicolas.puzzlecollector.retrofit.api.PuzzleAPI
import net.iessochoa.puzzlecollector.databinding.FragmentBuyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuyFragment : Fragment() {

    private lateinit var binding: FragmentBuyBinding
    private lateinit var puzzleAdapter: PuzzleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentBuyBinding.inflate(layoutInflater)

        binding.rvBuy.layoutManager = GridLayoutManager(context, 2)

        loadPuzzles()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun loadPuzzles(){
        val service = RetrofitService().getRetrofit()
        val puzzleAPI = service.create(PuzzleAPI::class.java)

        puzzleAPI.getPuzzles()
            .enqueue(object : Callback<List<ResponsePuzzle>>{
                override fun onResponse(call: Call<List<ResponsePuzzle>>, response: Response<List<ResponsePuzzle>>) {
                    puzzleAdapter = PuzzleAdapter(response.body(), 1)
                    binding.rvBuy.adapter = puzzleAdapter
                    puzzleAdapter.onPuzzleClickListener = object :
                        PuzzleAdapter.OnPuzzleClickListener {
                        override fun onPuzzleClick(id: Long) {
                            PuzzleDialog(id, requireContext(), 0, null)
                        }
                    }
                }

                override fun onFailure(call: Call<List<ResponsePuzzle>>, t: Throwable) {
                    Toast.makeText(context, "Connection Error!", Toast.LENGTH_SHORT).show()
                }
            })
    }
}