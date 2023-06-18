package com.janicolas.puzzlecollector.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.janicolas.puzzlecollector.activity.MainActivity.Companion.user
import com.janicolas.puzzlecollector.dialog.LoginDialog
import com.janicolas.puzzlecollector.dialog.RegisterDialog
import com.janicolas.puzzlecollector.util.StringToBitMapConverter.StringToBitMap
import net.iessochoa.puzzlecollector.R
import net.iessochoa.puzzlecollector.databinding.ActivityUserPanelBinding


class UserPanel : AppCompatActivity() {

    private lateinit var binding:ActivityUserPanelBinding

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
            if (!user?.iconPath.isNullOrEmpty())
                binding.ivUserIcon.setImageBitmap(StringToBitMap(user!!.iconImg!!))
            else
                binding.ivUserIcon.setImageResource(R.drawable.user_icon)
            binding.tvUserName.text = user!!.username
            settings.edit().putString("username", user?.username).apply()
        }
    }
}