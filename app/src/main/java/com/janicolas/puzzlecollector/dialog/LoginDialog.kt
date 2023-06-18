package com.janicolas.puzzlecollector.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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

class LoginDialog(userPanel: UserPanel, context: Context) {

    init {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.login_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        val etName: EditText = dialog.findViewById(R.id.etName)
        val etPass: EditText = dialog.findViewById(R.id.etPass)
        val btLogin: Button = dialog.findViewById(R.id.btLogin)
        val btClose: ImageButton = dialog.findViewById(R.id.ibClose)

        dialog.create()
        dialog.show()

        val service = RetrofitService()
        val userApi = service.getRetrofit().create(UserAPI::class.java)

        btClose.setOnClickListener{
            dialog.dismiss()
        }

        btLogin.setOnClickListener{
            if(etName.text.isNullOrEmpty() || etPass.text.isNullOrEmpty()) {
                Toast.makeText(context, context.resources.getString(R.string.errorLogin), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            userApi.getUserByName(etName.text.toString())
                .enqueue(object: Callback<ResponseUser>{
                    override fun onResponse(call: Call<ResponseUser>, response:  Response<ResponseUser>) {
                        if(response.body() != null) {
                            val pass = CifradoNicolas.descifrador(response.body()!!.password)
                            if(pass == etPass.text.toString()){
                                user = response.body()
                                dialog.dismiss()
                                userPanel.initUser()
                            } else
                                Toast.makeText(context, context.getString(R.string.errorLoginPass),
                                    Toast.LENGTH_SHORT).show()
                        } else
                            Toast.makeText(context, context.getString(R.string.errorLoginUser),
                                Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(call: Call<ResponseUser>, t: Throwable) {
                        Toast.makeText(context, "Connection Error!", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}