package com.janicolas.puzzlecollector.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.janicolas.puzzlecollector.activity.MainActivity.Companion.user
import com.janicolas.puzzlecollector.fragment.CollectionFragment
import com.janicolas.puzzlecollector.model.ResponseCollection
import com.janicolas.puzzlecollector.model.ResponsePuzzle
import com.janicolas.puzzlecollector.retrofit.RetrofitService
import com.janicolas.puzzlecollector.retrofit.api.CollectionAPI
import com.janicolas.puzzlecollector.retrofit.api.PuzzleAPI
import com.janicolas.puzzlecollector.util.StringToBitMapConverter.StringToBitMap
import net.iessochoa.puzzlecollector.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PuzzleDialog(id: Long, val context: Context?) {

    private val service = RetrofitService().getRetrofit()
    val collectionAPI: CollectionAPI = service.create(CollectionAPI::class.java)
    private lateinit var puzzle: ResponsePuzzle
    private var dialog: Dialog = Dialog(context!!)
    private var ivPuzzleImg: ImageView
    private var tvName: TextView
    private var tvPrice: TextView
    private var tvBrand: TextView
    private var tvType: TextView
    private var btClose: ImageButton
    private var btWishlist: ImageButton
    private var btCollection: Button

    init {
        dialog.setContentView(R.layout.puzzle_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        ivPuzzleImg = dialog.findViewById(R.id.ivPuzzleImg)
        tvName = dialog.findViewById(R.id.tvName)
        tvPrice = dialog.findViewById(R.id.tvPrice)
        tvBrand = dialog.findViewById(R.id.tvBrand)
        tvType = dialog.findViewById(R.id.tvType)
        btClose = dialog.findViewById(R.id.ibClose)
        btWishlist = dialog.findViewById(R.id.ibWishlist)
        btCollection = dialog.findViewById(R.id.btCollection)

        dialog.create()
        dialog.show()

        btClose.setOnClickListener {
            dialog.dismiss()
        }
        val puzzleAPI = service.create(PuzzleAPI::class.java)
        puzzleAPI.getPuzzleById(id)
            .enqueue(object : Callback<ResponsePuzzle> {
                override fun onResponse(
                    call: Call<ResponsePuzzle>,
                    response: Response<ResponsePuzzle>
                ) {
                    if (response.body() != null) {
                        puzzle = response.body()!!
                        ivPuzzleImg.setImageBitmap(StringToBitMap(puzzle.puzzleImg))
                        tvName.text = puzzle.name
                        tvPrice.text = String.format(
                            context!!.getString(R.string.tvPrice),
                            puzzle.price
                        )
                        tvBrand.text = String.format(
                            context.getString(R.string.tvBrand),
                            puzzle.brand
                        )
                        val types = context.resources.getStringArray(R.array.tvType)
                        tvType.text = when (puzzle.type) {
                            1 -> types[0]
                            2 -> types[1]
                            3 -> types[2]
                            4 -> types[3]
                            else -> "Error in Code"
                        }
                    }
                }

                override fun onFailure(call: Call<ResponsePuzzle>, t: Throwable) {
                    Toast.makeText(context, "Connection Error!", Toast.LENGTH_SHORT).show()
                }
            })
        btCollection.text = context!!.getString(R.string.btAddCollection)
        btCollection.setOnClickListener {
            if (user != null)
                addToCollection(user!!.id, puzzle.id)
            else
                Toast.makeText(
                    context, context.getString(R.string.accountError),
                    Toast.LENGTH_SHORT).show()
        }
    }

    constructor(id: Long, context: Context?, activity:CollectionFragment): this(id, context) {
        tvPrice.visibility = View.GONE
        btCollection.text = context!!.getString(R.string.btDelCollection)
        btCollection.setOnClickListener {
            delFromCollection(user!!.id, puzzle.id, activity)
        }
    }
    private fun addToCollection(userId: Long, puzzleId: Long){
        collectionAPI.existsCollection(userId, puzzleId)
            .enqueue(object: Callback<Boolean>{
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if(response.body() == true)
                        Toast.makeText(context, context?.getString(R.string.alreadyInCollection),
                            Toast.LENGTH_SHORT).show()
                    else {
                        collectionAPI.createCollection(userId, puzzleId, null)
                            .enqueue(object: Callback<ResponseCollection>{
                                override fun onResponse(call: Call<ResponseCollection>,
                                    response: Response<ResponseCollection>) {
                                    Toast.makeText(context, context!!.getString(R.string.addedToCollection),
                                        Toast.LENGTH_SHORT).show()
                                }

                                override fun onFailure(call: Call<ResponseCollection>, t: Throwable) {
                                    Toast.makeText(context, "Connection Error!", Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Toast.makeText(context, "Connection Error!", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun delFromCollection(userId: Long, puzzleId: Long, activity: CollectionFragment){
        collectionAPI.deleteCollection(userId, puzzleId)
            .enqueue(object: Callback<Boolean>{
                override fun onResponse(call: Call<Boolean>,
                                        response: Response<Boolean>) {
                    Toast.makeText(context, context?.getString(R.string.removedFromCollection),
                        Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    activity.loadPuzzles()
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Toast.makeText(context, "Connection Error!", Toast.LENGTH_SHORT).show()
                }
            })
    }
}