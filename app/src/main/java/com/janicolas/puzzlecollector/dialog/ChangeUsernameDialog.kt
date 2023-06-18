package com.janicolas.puzzlecollector.dialog

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.janicolas.puzzlecollector.activity.MainActivity.Companion.user
import com.janicolas.puzzlecollector.model.ResponseUser
import com.janicolas.puzzlecollector.retrofit.RetrofitService
import com.janicolas.puzzlecollector.retrofit.api.UserAPI
import net.iessochoa.puzzlecollector.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeUsernameDialog(val context: Context) {

    private val dialog = Dialog(context)
    private var etUsername: EditText
    private var btOk: Button
    private var btCancel: Button
    private val service = RetrofitService().getRetrofit()
    private val userApi = service.create(UserAPI::class.java)

    init {
        dialog.setTitle(R.string.changeUsername)
        dialog.setContentView(R.layout.change_username_dialog)
        dialog.setCancelable(false)
        etUsername = dialog.findViewById(R.id.etUsername)
        etUsername.setText(user!!.username)
        btOk = dialog.findViewById(R.id.btOk)
        btCancel = dialog.findViewById(R.id.btCancel)

        btOk.setOnClickListener {
            if (etUsername.text.isNullOrEmpty())
                Toast.makeText(
                    context, context.getString(R.string.errorChangeUsername),
                    Toast.LENGTH_SHORT
                ).show()
            else {
                val username = etUsername.text.toString()
                userApi.existsUser(username)
                    .enqueue(object : Callback<Boolean> {
                        override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                            if (response.body() == false) {
                                user!!.username = etUsername.text.toString()
                                userApi.updateUser(user!!)
                                    .enqueue(object : Callback<ResponseUser> {
                                        override fun onResponse(
                                            call: Call<ResponseUser>,
                                            response: Response<ResponseUser>
                                        ) {
                                            if (response.body() != null) {
                                                user = response.body()
                                                Toast.makeText(context,
                                                    context.getString(R.string.changeUsernameSuccess),
                                                    Toast.LENGTH_SHORT).show()
                                                dialog.dismiss()
                                            }
                                        }

                                        override fun onFailure(call: Call<ResponseUser>, t: Throwable) {
                                            Toast.makeText(context, "Connection Error!",
                                                Toast.LENGTH_SHORT).show()
                                        }
                                    })
                            } else if (response.body() == true)
                                Toast.makeText(context, context.getString(R.string.existingUser),
                                    Toast.LENGTH_SHORT
                                ).show()
                        }

                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                            Toast.makeText(context, "Connection Error!",
                                Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
        btCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}