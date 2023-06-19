package com.janicolas.puzzlecollector.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.janicolas.puzzlecollector.activity.MainActivity.Companion.user
import com.janicolas.puzzlecollector.activity.UserPanel
import com.janicolas.puzzlecollector.model.ResponseUser
import com.janicolas.puzzlecollector.retrofit.api.UserAPI
import com.janicolas.puzzlecollector.retrofit.RetrofitService
import com.janicolas.puzzlecollector.util.CifradoNicolas
import net.iessochoa.puzzlecollector.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterDialog(userPanel: UserPanel, context: Context) {
    init {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.sign_in_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        val etName: EditText = dialog.findViewById(R.id.etName)
        val etPass: EditText = dialog.findViewById(R.id.etPass)
        val etRepeatPass: EditText = dialog.findViewById(R.id.etRepeatPass)
        val tvError: TextView = dialog.findViewById(R.id.tvError)
        val btRegister: Button = dialog.findViewById(R.id.btRegister)
        val btClose: ImageButton = dialog.findViewById(R.id.ibClose)

        dialog.create()
        dialog.show()

        val service = RetrofitService()
        val userApi = service.getRetrofit().create(UserAPI::class.java)

        btClose.setOnClickListener{
            dialog.dismiss()
        }

        btRegister.setOnClickListener {
            if (etName.text.isNullOrEmpty() || etPass.text.isNullOrEmpty() ||
                etRepeatPass.text.isNullOrEmpty()) {
                Toast.makeText(
                    context, context.getString(R.string.errorRegister),
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (etPass.toString().length < 8 ||
                !etPass.text.toString().contains(Regex("^(?=.*\\d)(?=.*[A-Z]).+\$"))){
                tvError.text = context.getString(R.string.errorPassReq)
                tvError.visibility = View.VISIBLE
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    tvError.visibility = View.GONE
                }, 4000)
                return@setOnClickListener
            }
            if (etPass.text.toString() != etRepeatPass.text.toString()){
                tvError.text = context.getString(R.string.errorPassMatch)
                tvError.visibility = View.VISIBLE
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    tvError.visibility = View.GONE
                }, 3000)
                return@setOnClickListener
            }
            userApi.existsUser(etName.text.toString())
                .enqueue(object: Callback<Boolean>{
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        if(response.body() == false){
                            val newUser = ResponseUser(0, etName.text.toString(),
                                CifradoNicolas.cifrador(etPass.text.toString()),
                                "", null, false,
                                ArrayList())
                            userApi.newUser(newUser)
                                .enqueue(object: Callback<ResponseUser>{
                                    override fun onResponse(
                                        call: Call<ResponseUser>,
                                        response: Response<ResponseUser>
                                    ) {
                                        if(response.body() != null){
                                            user = response.body()
                                            Toast.makeText(context,
                                                context.getString(R.string.registerSuccess),
                                                Toast.LENGTH_SHORT).show()
                                            dialog.dismiss()
                                            userPanel.initUser()
                                        }
                                    }

                                    override fun onFailure(call: Call<ResponseUser>, t: Throwable) {
                                        Toast.makeText(context, "Connection Error!", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        } else {
                            tvError.text = context.getString(R.string.existingUser)
                            tvError.visibility = View.VISIBLE
                            android.os.Handler(Looper.getMainLooper()).postDelayed({
                                tvError.visibility = View.GONE
                            }, 3000)
                        }
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        Toast.makeText(context, "Connection Error!", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}