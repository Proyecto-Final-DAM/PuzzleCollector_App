package com.janicolas.puzzlecollector.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.janicolas.puzzlecollector.activity.MainActivity.Companion.user
import com.janicolas.puzzlecollector.adapter.PuzzleAdapter
import com.janicolas.puzzlecollector.dialog.PuzzleDialog
import com.janicolas.puzzlecollector.model.ResponseCollection
import com.janicolas.puzzlecollector.model.ResponsePuzzle
import com.janicolas.puzzlecollector.retrofit.RetrofitService
import com.janicolas.puzzlecollector.retrofit.api.CollectionAPI
import net.iessochoa.puzzlecollector.R
import net.iessochoa.puzzlecollector.databinding.FragmentCollectionBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CollectionFragment : Fragment() {

    private lateinit var binding: FragmentCollectionBinding
    private lateinit var puzzleAdapter: PuzzleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCollectionBinding.inflate(layoutInflater)

        binding.rvCollection.layoutManager = GridLayoutManager(context, 2)

        loadPuzzles()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    fun loadPuzzles(){
        val service = RetrofitService().getRetrofit()
        val collectionAPI = service.create(CollectionAPI::class.java)
        if(user != null) {
            collectionAPI.getCollectionByUserId(user!!.id)
                .enqueue(object : Callback<List<ResponseCollection>>{
                    override fun onResponse(
                        call: Call<List<ResponseCollection>>,
                        response: Response<List<ResponseCollection>>
                    ) {
                        if(response.body().isNullOrEmpty()){
                            Toast.makeText(context, getString(R.string.emptyCollection),
                                Toast.LENGTH_SHORT).show()
                        }
                        val puzzles:MutableList<ResponsePuzzle> = ArrayList()
                        response.body()?.forEach{
                            puzzles.add(it.puzzle)
                        }
                        puzzleAdapter = PuzzleAdapter(puzzles, 0)
                        binding.rvCollection.adapter = puzzleAdapter
                        puzzleAdapter.onPuzzleClickListener = object :
                            PuzzleAdapter.OnPuzzleClickListener {
                            override fun onPuzzleClick(id: Long) {
                                PuzzleDialog(id, requireContext(), this@CollectionFragment)
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<ResponseCollection>>, t: Throwable) {
                        Toast.makeText(context, "Connection Error!", Toast.LENGTH_SHORT).show()
                        println(t)
                    }
                })
        } else {
            Toast.makeText(context, getString(R.string.accountError), Toast.LENGTH_SHORT).show()
        }
    }
}