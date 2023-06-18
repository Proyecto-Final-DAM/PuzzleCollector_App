package com.janicolas.puzzlecollector.dialog

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.janicolas.puzzlecollector.activity.MainActivity.Companion.user
import com.janicolas.puzzlecollector.model.ResponseUser
import com.janicolas.puzzlecollector.retrofit.RetrofitService
import com.janicolas.puzzlecollector.retrofit.api.UserAPI
import com.janicolas.puzzlecollector.util.CifradoNicolas
import net.iessochoa.puzzlecollector.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePassDialog(val context: Context) {

    private val dialog = Dialog(context)
    private var etActualPass: EditText
    private var etNewPass: EditText
    private var etRepeatPass: EditText
    private var tvErrorMessage: TextView
    private var btOk: Button
    private var btCancel: Button
    private val service = RetrofitService().getRetrofit()
    private val userApi = service.create(UserAPI::class.java)

    init {
        dialog.setTitle(R.string.changePassword)
        dialog.setContentView(R.layout.change_pass_dialog)
        dialog.setCancelable(false)
        etActualPass = dialog.findViewById(R.id.etActualPass)
        etNewPass = dialog.findViewById(R.id.etNewPass)
        etRepeatPass = dialog.findViewById(R.id.etRepeatPass)
        tvErrorMessage = dialog.findViewById(R.id.tvErrorMessage)
        btOk = dialog.findViewById(R.id.btOk)
        btCancel = dialog.findViewById(R.id.btCancel)

        btOk.setOnClickListener{
            if(!checkPassword())
                return@setOnClickListener

            user!!.password = CifradoNicolas.cifrador(etNewPass.text.toString())

            userApi.updateUser(user!!)
                .enqueue(object: Callback<ResponseUser>{
                    override fun onResponse(call: Call<ResponseUser>,
                        response: Response<ResponseUser>) {
                        if(response.body() != null){
                            user = response.body()
                            Toast.makeText(context, context.getString(R.string.changePassSuccess),
                            Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }

                    override fun onFailure(call: Call<ResponseUser>, t: Throwable) {
                        Toast.makeText(context, "Connection Error!", Toast.LENGTH_SHORT).show()
                    }
                })
        }
        btCancel.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun checkPassword():Boolean{
        if (etActualPass.text.isNullOrEmpty() || etNewPass.text.isNullOrEmpty() ||
            etRepeatPass.text.isNullOrEmpty()) {
            Toast.makeText(context, context.getString(R.string.errorFields),
                Toast.LENGTH_SHORT).show()
            return false
        }
        if (etActualPass.text.toString() != CifradoNicolas.descifrador(user!!.password)){
            Toast.makeText(context, context.getString(R.string.errorLoginPass),
                Toast.LENGTH_SHORT).show()
            return false
        }
        if (etActualPass.text.toString() == etNewPass.text.toString()){
            tvErrorMessage.text = context.getString(R.string.errorChangePass)
            tvErrorMessage.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                tvErrorMessage.visibility = View.GONE
            }, 4000)
            return false
        }
        if (etNewPass.toString().length < 8 ||
            !etNewPass.text.toString().contains(Regex("^(?=.*\\d)(?=.*[A-Z]).+\$"))){
            tvErrorMessage.text = context.getString(R.string.errorPassReq)
            tvErrorMessage.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                tvErrorMessage.visibility = View.GONE
            }, 4000)
            return false
        }
        if (etNewPass.text.toString() != etRepeatPass.text.toString()){
            tvErrorMessage.text = context.getString(R.string.errorPassMatch)
            tvErrorMessage.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                tvErrorMessage.visibility = View.GONE
            }, 4000)
            return false
        }
        return true
    }
}