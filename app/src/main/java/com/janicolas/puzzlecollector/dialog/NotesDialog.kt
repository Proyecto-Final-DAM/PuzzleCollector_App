package com.janicolas.puzzlecollector.dialog

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.janicolas.puzzlecollector.model.ResponseCollection
import com.janicolas.puzzlecollector.retrofit.RetrofitService
import com.janicolas.puzzlecollector.retrofit.api.CollectionAPI
import net.iessochoa.puzzlecollector.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotesDialog(val context: Context, val userId: Long, val puzzleId: Long) {

    private val dialog = Dialog(context)
    private val service = RetrofitService().getRetrofit()
    private val collectionAPI = service.create(CollectionAPI::class.java)
    private var etNotes: EditText
    private var btOk: Button

    init {
        dialog.setContentView(R.layout.notes_dialog)
        dialog.setCancelable(false)

        etNotes = dialog.findViewById(R.id.etNotes)
        getNotes()
        btOk = dialog.findViewById(R.id.btOk)

        btOk.setOnClickListener{
            setNotes(etNotes.text.toString())
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getNotes(){
        collectionAPI.getCollectionByUserIdAndPuzzleId(userId, puzzleId)
            .enqueue(object: Callback<ResponseCollection>{
                override fun onResponse(call: Call<ResponseCollection>,
                    response: Response<ResponseCollection>) {
                    if(response.body() != null){
                        etNotes.setText(response.body()!!.notes)
                    }
                }

                override fun onFailure(call: Call<ResponseCollection>, t: Throwable) {
                    Toast.makeText(context, "Connection Error!", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setNotes(notes: String){
        collectionAPI.updateCollection(userId, puzzleId, notes)
            .enqueue(object: Callback<ResponseCollection>{
                override fun onResponse(call: Call<ResponseCollection>,
                                        response: Response<ResponseCollection>) {
                    if(response.body() != null){
                        Toast.makeText(context, context.getString(R.string.notesUpdatedSuccess),
                        Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseCollection>, t: Throwable) {
                    Toast.makeText(context, "Connection Error!", Toast.LENGTH_SHORT).show()
                }
            })
    }
}