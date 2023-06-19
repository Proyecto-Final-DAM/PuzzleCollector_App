package com.janicolas.puzzlecollector.activity

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.janicolas.puzzlecollector.util.SettingsChangeListener
import com.janicolas.puzzlecollector.activity.MainActivity.Companion.user
import com.janicolas.puzzlecollector.dialog.ChangePassDialog
import com.janicolas.puzzlecollector.dialog.ChangeUsernameDialog
import com.janicolas.puzzlecollector.retrofit.RetrofitService
import com.janicolas.puzzlecollector.retrofit.api.UserAPI
import com.janicolas.puzzlecollector.util.CifradoNicolas
import net.iessochoa.puzzlecollector.R
import net.iessochoa.puzzlecollector.databinding.ActivityConfigBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfigActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfigBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }

        binding.ibBack.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private lateinit var sharedPreferences: SharedPreferences
        private val settingsChangeListener = SettingsChangeListener()

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            sharedPreferences = preferenceManager.sharedPreferences!!

            if (user != null) {
                preferenceScreen.findPreference<Preference>("delAccount")?.title =
                    getString(R.string.deleteAccountFormated)
                preferenceScreen.findPreference<PreferenceCategory>("user")?.isEnabled = true
            } else {
                sharedPreferences.edit()
                    .putString("username", "").apply()
                preferenceScreen.findPreference<PreferenceCategory>("user")?.isEnabled = false
            }

            preferenceScreen.findPreference<Preference>("changeUsername")
                ?.setOnPreferenceClickListener {
                    ChangeUsernameDialog(requireContext())
                    true
                }

            preferenceScreen.findPreference<Preference>("changePass")
                ?.setOnPreferenceClickListener {
                    ChangePassDialog(requireContext())
                    true
                }

            preferenceScreen.findPreference<Preference>("logoff")
                ?.setOnPreferenceClickListener {
                    AlertDialog.Builder(requireContext())
                        .setCancelable(false)
                        .setTitle(getString(R.string.logOff))
                        .setMessage(getString(R.string.logOffMessage))
                        .setPositiveButton(R.string.yes) { dialog, _ ->
                            sharedPreferences.edit()
                                .putString("username", "").apply()
                            user = null
                            dialog.dismiss()
                            startActivity(Intent(context, MainActivity::class.java))
                        }
                        .setNegativeButton(R.string.no) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                    true
                }

            preferenceScreen.findPreference<Preference>("delAccount")
                ?.setOnPreferenceClickListener {
                    val dialog = Dialog(requireContext())
                    dialog.setContentView(R.layout.delete_account_dialog)
                    dialog.setCancelable(false)
                    val etPass: EditText = dialog.findViewById(R.id.etPass)
                    val btOk: Button = dialog.findViewById(R.id.btOk)
                    val btCancel: Button = dialog.findViewById(R.id.btCancel)

                    btOk.setOnClickListener{
                        if(etPass.text.toString() != CifradoNicolas.descifrador(user!!.password)){
                            Toast.makeText(context, getString(R.string.errorLoginPass),
                            Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        val service = RetrofitService().getRetrofit()
                        val userApi = service.create(UserAPI::class.java)

                        userApi.deleteUser(user!!.Id)
                            .enqueue(object: Callback<Boolean>{
                                override fun onResponse(call: Call<Boolean>,
                                    response: Response<Boolean>) {
                                    if(response.body() == true){
                                        user = null
                                        Toast.makeText(context, getString(R.string.delAccountSuccess),
                                        Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(context, MainActivity::class.java))
                                    }
                                }

                                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                                    Toast.makeText(context, "Connection Error!",
                                        Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                    btCancel.setOnClickListener{
                        dialog.dismiss()
                    }

                    dialog.show()
                    true
                }
        }

        override fun onResume() {
            super.onResume()
            sharedPreferences.registerOnSharedPreferenceChangeListener(settingsChangeListener)
        }

        override fun onPause() {
            super.onPause()
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(settingsChangeListener)
        }
    }
}