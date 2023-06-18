package com.janicolas.puzzlecollector.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.janicolas.puzzlecollector.adapter.MenuAdapter
import com.janicolas.puzzlecollector.fragment.BuyFragment
import com.janicolas.puzzlecollector.fragment.CollectionFragment
import com.janicolas.puzzlecollector.fragment.EventFragment
import net.iessochoa.puzzlecollector.databinding.ActivityMainBinding
import com.janicolas.puzzlecollector.menu.Data_en
import com.janicolas.puzzlecollector.menu.Data_es
import com.janicolas.puzzlecollector.model.ResponseUser
import com.janicolas.puzzlecollector.retrofit.RetrofitService
import com.janicolas.puzzlecollector.retrofit.api.UserAPI
import net.iessochoa.puzzlecollector.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    companion object {
        var user: ResponseUser? = null
    }

    private lateinit var binding:ActivityMainBinding
    private lateinit var adapter: MenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPreferences()
        binding.spMenu.adapter = adapter

        binding.spMenu.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0 -> setFragment(BuyFragment())
                    1 -> setFragment(EventFragment())
                    2 -> setFragment(CollectionFragment())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.ibUser.setOnClickListener{
            startActivity(Intent(this, UserPanel::class.java))
        }
        binding.ibConf.setOnClickListener{
            startActivity(Intent(this, ConfigActivity::class.java))
        }
    }

    private fun checkPreferences(){
        val settings = PreferenceManager.getDefaultSharedPreferences(this)

        if(!settings.getString("username", "").isNullOrEmpty()){
            val username = settings.getString("username", "")!!
            val service = RetrofitService().getRetrofit()
            val userApi = service.create(UserAPI::class.java)
            userApi.getUserByName(username)
                .enqueue(object: Callback<ResponseUser>{
                    override fun onResponse(
                        call: Call<ResponseUser>,
                        response: Response<ResponseUser>
                    ) {
                        if(response.body() != null)
                            user = response.body()
                    }

                    override fun onFailure(call: Call<ResponseUser>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Connection Error!",
                            Toast.LENGTH_SHORT).show()
                    }
                })
        }

        when(settings.getString("language", "0")){
            "en" -> {
                val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("en")
                setApplicationLocales(appLocale)
                adapter = MenuAdapter(this, Data_en.menu)
            }
            "es" -> {
                val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("es")
                setApplicationLocales(appLocale)
                adapter = MenuAdapter(this, Data_es.menu)
            }
            else -> {
                val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("es")
                setApplicationLocales(appLocale)
                adapter = MenuAdapter(this, Data_es.menu)
                settings.edit().putString("language", "es").apply()
            }
        }

        when(settings.getString("mode", "0")?.toInt()){
            1 -> setDefaultNightMode(MODE_NIGHT_NO)
            2 -> setDefaultNightMode(MODE_NIGHT_YES)
            3 -> setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
            else -> {
                setDefaultNightMode(MODE_NIGHT_NO)
                settings.edit().putString("mode", "1").apply()
            }
        }
    }

    private fun setFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment, fragment)
            .commit()
    }
}