package com.janicolas.puzzlecollector.activity

import android.content.Intent
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.janicolas.puzzlecollector.activity.MainActivity.Companion.user
import com.janicolas.puzzlecollector.dialog.LoginDialog
import com.janicolas.puzzlecollector.dialog.RegisterDialog
import com.janicolas.puzzlecollector.model.ResponseUser
import com.janicolas.puzzlecollector.retrofit.RetrofitService
import com.janicolas.puzzlecollector.retrofit.api.UserAPI
import com.janicolas.puzzlecollector.util.StringToBitMapConverter.StringToBitMap
import net.iessochoa.puzzlecollector.R
import net.iessochoa.puzzlecollector.databinding.ActivityUserPanelBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import kotlin.math.ceil


class UserPanel : AppCompatActivity() {

    private lateinit var binding:ActivityUserPanelBinding
    private val pickMedia = registerForActivityResult(PickVisualMedia()) {uri ->
        if(uri != null) {
            binding.ivUserIcon.setImageURI(uri)
            val drawable = binding.ivUserIcon.drawable as BitmapDrawable
            val bitmap = drawable.bitmap
            val bos = ByteArrayOutputStream()
            bitmap.compress(CompressFormat.JPEG, 100, bos)
            val base64 = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT)
            val sizeInBytes = 4 * ceil(((base64.length / 3).toDouble())) * 0.5624896334383812
            if (sizeInBytes/1000 > 100){
                Toast.makeText(this, getString(R.string.imgTooBig),
                    Toast.LENGTH_SHORT).show()
                initUser()
            } else {
                user!!.iconImg = base64
                updateUser()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUser()

        binding.ibBack.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.ibConf.setOnClickListener{
            startActivity(Intent(this, ConfigActivity::class.java))
        }
        binding.btLogin.setOnClickListener{
            LoginDialog(this, this)
        }
        binding.btRegister.setOnClickListener{
            RegisterDialog(this, this)
        }

        binding.ibEditIcon.setOnClickListener{
            if(user!!.iconImg == null)
                pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            else{
                AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(getString(R.string.imgSelector))
                    .setMessage(getString(R.string.imgSelectorMessage))
                    .setPositiveButton(R.string.changeImg) { dialog, _ ->
                        dialog.dismiss()
                        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                    }
                    .setNegativeButton(R.string.deleteImg) { dialog, _ ->
                        dialog.dismiss()
                        user!!.iconImg = "null"
                        binding.ivUserIcon.setImageResource(R.drawable.user_icon)
                        updateUser()
                    }
                    .show()
            }
        }

        binding.btWishlist.setOnClickListener{
            if(user != null)
                startActivity(Intent(this, WishlistActivity::class.java))
            else
                Toast.makeText(this, getString(R.string.accountError), Toast.LENGTH_SHORT).show()
        }

        binding.btSuggest.setOnClickListener{
            val uri = Uri.parse(resources.getString(R.string.linkToForm))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    fun initUser(){
        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        if(user == null){
            binding.noUserPanel.visibility = View.VISIBLE
            binding.userPanel.visibility = View.INVISIBLE
            settings.edit().putString("username", "").apply()
        } else {
            binding.noUserPanel.visibility = View.INVISIBLE
            binding.userPanel.visibility = View.VISIBLE
            if (!user?.iconImg.isNullOrEmpty())
                binding.ivUserIcon.setImageBitmap(StringToBitMap(user!!.iconImg!!))
            else
                binding.ivUserIcon.setImageResource(R.drawable.user_icon)
            binding.tvUserName.text = user!!.username
            settings.edit().putString("username", user?.username).apply()
        }
    }

    private fun updateUser(){
        val service = RetrofitService().getRetrofit()
        val userApi = service.create(UserAPI::class.java)

        userApi.updateUser(user!!)
            .enqueue(object: Callback<ResponseUser>{
                override fun onResponse(call: Call<ResponseUser>,
                    response: Response<ResponseUser>) {
                    if(response.body() != null){
                        user = response.body()
                        Toast.makeText(this@UserPanel, getString(R.string.imgUploadedSuccess),
                        Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseUser>, t: Throwable) {
                    Toast.makeText(this@UserPanel, "Connection Error!", Toast.LENGTH_SHORT).show()
                }
            })
    }
}