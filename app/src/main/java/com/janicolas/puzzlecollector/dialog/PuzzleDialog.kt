package com.janicolas.puzzlecollector.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.janicolas.puzzlecollector.activity.MainActivity.Companion.user
import com.janicolas.puzzlecollector.activity.WishlistActivity
import com.janicolas.puzzlecollector.fragment.CollectionFragment
import com.janicolas.puzzlecollector.model.ResponseCollection
import com.janicolas.puzzlecollector.model.ResponsePuzzle
import com.janicolas.puzzlecollector.model.ResponseUser
import com.janicolas.puzzlecollector.retrofit.RetrofitService
import com.janicolas.puzzlecollector.retrofit.api.CollectionAPI
import com.janicolas.puzzlecollector.retrofit.api.PuzzleAPI
import com.janicolas.puzzlecollector.retrofit.api.UserAPI
import com.janicolas.puzzlecollector.util.StringToBitMapConverter.StringToBitMap
import net.iessochoa.puzzlecollector.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PuzzleDialog(id: Long, val context: Context, collection:Int, val wishlist: WishlistActivity?) {

    private val service = RetrofitService().getRetrofit()
    private val userAPI = service.create(UserAPI::class.java)
    private val puzzleAPI = service.create(PuzzleAPI::class.java)
    private val collectionAPI: CollectionAPI = service.create(CollectionAPI::class.java)
    private lateinit var puzzle: ResponsePuzzle
    private var dialog: Dialog = Dialog(context)
    private var ivPuzzleImg: ImageView
    private var tvName: TextView
    private var tvPrice: TextView
    private var tvBrand: TextView
    private var tvType: TextView
    private var tvLinksType: TextView
    private var btClose: ImageButton
    private var btWishlist: ImageButton
    private var btNotes: Button
    private var btLink1: ImageButton
    private var btLink2: ImageButton
    private var btLink3: ImageButton
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
        tvLinksType = dialog.findViewById(R.id.tvLinksType)
        btClose = dialog.findViewById(R.id.ibClose)
        btNotes = dialog.findViewById(R.id.btNotes)
        btWishlist = dialog.findViewById(R.id.ibWishlist)
        btLink1 = dialog.findViewById(R.id.btLink1)
        btLink2 = dialog.findViewById(R.id.btLink2)
        btLink3 = dialog.findViewById(R.id.btLink3)
        btCollection = dialog.findViewById(R.id.btCollection)

        dialog.create()
        dialog.show()

        btClose.setOnClickListener {
            dialog.dismiss()
        }

        if(!this::puzzle.isInitialized && collection == 0)
            initPuzzle(id, "buy")

        btCollection.text = context.getString(R.string.btAddCollection)
        btCollection.setOnClickListener {
            if (user != null)
                addToCollection(user!!.id, puzzle.id)
            else
                Toast.makeText(
                    context, context.getString(R.string.accountError),
                    Toast.LENGTH_SHORT).show()
        }
    }

    constructor(id: Long, context: Context, activity:CollectionFragment):
            this(id, context, 1, null) {
        if(!this::puzzle.isInitialized)
            initPuzzle(id, "collection")
        tvPrice.visibility = View.GONE
        btNotes.visibility = View.VISIBLE
        btNotes.setOnClickListener{
            NotesDialog(context, user!!.id, puzzle.id)
        }
        btCollection.text = context.getString(R.string.btDelCollection)
        btCollection.setOnClickListener {
            delFromCollection(user!!.id, puzzle.id, activity)
        }
    }

    private fun initPuzzle(id: Long, type: String){
        puzzleAPI.getPuzzleById(id)
            .enqueue(object: Callback<ResponsePuzzle>{
                override fun onResponse(call: Call<ResponsePuzzle>,
                    response: Response<ResponsePuzzle>
                ) {
                    if(response.body() != null) {
                        puzzle = response.body()!!

                        btWishlist.setImageResource(getDrawable("WishList"))

                        btWishlist.setOnClickListener{
                            if(user != null){
                                if(user!!.puzzles.contains(puzzle)) {
                                    user!!.puzzles.remove(puzzle)
                                    btWishlist.setImageResource(R.drawable.no_wishlist_icon)
                                    if(wishlist != null) {
                                        wishlist.loadPuzzles()
                                        dialog.dismiss()
                                    }
                                    updateUser()
                                } else {
                                    user!!.puzzles.add(puzzle)
                                    btWishlist.setImageResource(R.drawable.wishlist_icon)
                                    updateUser()
                                }
                            }
                        }

                        ivPuzzleImg.setImageBitmap(StringToBitMap(puzzle.puzzleImg))
                        tvName.text = puzzle.name
                        tvPrice.text = String.format(
                            context.getString(R.string.tvPrice),
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
                        if(type == "buy"){
                            tvLinksType.text = context.getString(R.string.buyLinks)
                            val buyLinks = puzzle.links.substring(0,
                                puzzle.links.indexOf("\r\n\r\n#TutorialLinks"))
                            val linkList = buyLinks.split("\r\n")
                            loadLinkButtons(linkList)
                        } else if(type == "collection"){
                            tvLinksType.text = context.getString(R.string.tutorialLinks)
                            if(puzzle.links.contains("#TutorialLinks\r\n")){
                                val collectionLinks = puzzle.links.substring(
                                    puzzle.links.indexOf("#TutorialLinks\r\n")+16,
                                    puzzle.links.length)
                                println(collectionLinks)
                                val linkList = collectionLinks.split("\r\n")
                                loadLinkButtons(linkList)
                            } else {
                                btLink1.visibility = View.GONE
                                btLink2.visibility = View.GONE
                                btLink3.visibility = View.GONE
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponsePuzzle>, t: Throwable) {
                    Toast.makeText(context, "Connection Error!", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun addToCollection(userId: Long, puzzleId: Long){
        collectionAPI.existsCollection(userId, puzzleId)
            .enqueue(object: Callback<Boolean>{
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if(response.body() == true)
                        Toast.makeText(context, context.getString(R.string.alreadyInCollection),
                            Toast.LENGTH_SHORT).show()
                    else {
                        collectionAPI.createCollection(userId, puzzleId, "")
                            .enqueue(object: Callback<ResponseCollection>{
                                override fun onResponse(call: Call<ResponseCollection>,
                                    response: Response<ResponseCollection>) {
                                    Toast.makeText(context, context.getString(R.string.addedToCollection),
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
                    Toast.makeText(context, context.getString(R.string.removedFromCollection),
                        Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    activity.loadPuzzles()
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Toast.makeText(context, "Connection Error!", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateUser(){
        userAPI.updateUser(user!!)
            .enqueue(object: Callback<ResponseUser>{
                override fun onResponse(call: Call<ResponseUser>, response: Response<ResponseUser>) {
                    if(response.body() != null){
                        user = response.body()
                    }
                }

                override fun onFailure(call: Call<ResponseUser>, t: Throwable) {
                    Toast.makeText(context, "Connection Error!", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadLinkButtons(linkList: List<String>){
        when (linkList.size){
            1 -> {
                btLink1.setImageResource(getDrawable(linkList[0].split(" || ")[1]))
                btLink1.setOnClickListener{
                    val uri = Uri.parse(linkList[0].split(" || ")[0])
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                btLink2.visibility = View.GONE
                btLink3.visibility = View.GONE
            }
            2 -> {
                btLink1.setImageResource(getDrawable(linkList[0].split(" || ")[1]))
                btLink1.setOnClickListener{
                    val uri = Uri.parse(linkList[0].split(" || ")[0])
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                btLink2.setImageResource(getDrawable(linkList[1].split(" || ")[1]))
                btLink2.setOnClickListener{
                    val uri = Uri.parse(linkList[1].split(" || ")[0])
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                btLink3.visibility = View.GONE
            }
            3 -> {
                btLink1.setImageResource(getDrawable(linkList[0].split(" || ")[1]))
                btLink1.setOnClickListener{
                    val uri = Uri.parse(linkList[0].split(" || ")[0])
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                btLink2.setImageResource(getDrawable(linkList[1].split(" || ")[1]))
                btLink2.setOnClickListener{
                    val uri = Uri.parse(linkList[1].split(" || ")[0])
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                btLink3.setImageResource(getDrawable(linkList[2].split(" || ")[1]))
                btLink3.setOnClickListener{
                    val uri = Uri.parse(linkList[2].split(" || ")[0])
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
            }
        }
    }

    private fun getDrawable(key:String):Int {
        return when (key) {
            "KubeKings" -> R.drawable.kubekings
            "Casa Del Puzzle" -> R.drawable.casa_del_puzzle
            "Amazon" -> R.drawable.amazon
            "Juegos Besa" -> R.drawable.juegosbesa
            "Mundos De Rubik" -> R.drawable.mundosderubik
            "YouTube" -> R.drawable.youtube
            "WishList" -> {
                if(user != null){
                    if(user!!.puzzles.contains(puzzle))
                        R.drawable.wishlist_icon
                    else
                        R.drawable.no_wishlist_icon
                } else
                    R.drawable.no_wishlist_icon
            }
            else -> R.drawable.web
        }
    }
}