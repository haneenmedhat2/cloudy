package com.example.cloudy.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.LocaleList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cloudy.HomeActivity
import com.example.cloudy.R
import com.example.cloudy.databinding.FragmentSettingsBinding
import com.example.cloudy.utility.LanguageConfig
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.Locale


class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    companion object {
         var locationSP =0
        var languageSP=true
        var temperatureSP=1
        var windSP=1

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("radio_button_prefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        sharedPreferences =
            requireContext().getSharedPreferences("radio_button_prefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected = networkInfo?.isConnectedOrConnecting == true


        locationSP = sharedPreferences.getInt("selectedRadioButtonId", 0)
        if (locationSP == 1) {
            binding.buttonMap.isChecked = true
        } else if (locationSP == 0) {
            binding.buttonGps.isChecked = true
        }

        binding.segmented1.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.button_map -> {
                    editor.putInt("selectedRadioButtonId", 1)
                    editor.apply()
                }
                R.id.button_gps -> {
                    editor.putInt("selectedRadioButtonId", 0)
                    editor.apply()
                }
            }
        }

        binding.buttonMap.setOnClickListener {
            if(!isConnected) {
                val snackbar =
                    Snackbar.make(binding.cl, "No Network Connection", Snackbar.LENGTH_SHORT)
                        .setAction("Dismiss") {
                        }.setActionTextColor(R.color.md_red_900)
                snackbar.show()
            }
                else{
                    val intent=Intent(requireContext(),SettingsMapsActivity::class.java)
                    startActivity(intent)
                }

        }

         languageSP = sharedPreferences.getBoolean("isEnglish", true)
        if (languageSP) {
            binding.buttonEng.isChecked = true
        } else {
            binding.buttonAr.isChecked = true
        }

        binding.segmented2.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.button_eng -> {
                    editor.putBoolean("isEnglish", true)
                    editor.putBoolean("isArabic", false)
                    editor.apply()
                }
                R.id.button_ar -> {
                    editor.putBoolean("isEnglish", false)
                    editor.putBoolean("isArabic", true)
                    editor.apply()

                }
            }
        }
        binding.buttonAr.setOnClickListener {
            setArabicLocale()
            editor.putBoolean("isEnglish", false)
            editor.putBoolean("isArabic", true)
            editor.apply()

            // Restart the activity to reflect the changes
            requireActivity().recreate()
        }

        binding.buttonEng.setOnClickListener {
            setEnglishLocale()
            editor.putBoolean("isEnglish", true)
            editor.putBoolean("isArabic", false)
            editor.apply()

            // Restart the activity to reflect the changes
            requireActivity().recreate()
        }



        temperatureSP = sharedPreferences.getInt("selectedTemperature", 1)
        if (temperatureSP == 1) {
            binding.buttonKelvin.isChecked = true
        }
        else if (temperatureSP == 2) {
            binding.buttonCelsius.isChecked = true
        }
        else {
            binding.buttonFahrenheit.isChecked=true
        }

        binding.segmented4.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.button_kelvin -> {
                    editor.putInt("selectedTemperature",1)
                    editor.apply()
                }
                R.id.button_celsius -> {
                    editor.putInt("selectedTemperature", 2)
                    editor.apply()
                }
                R.id.button_fahrenheit -> {
                    editor.putInt("selectedTemperature", 3)
                    editor.apply()
                }
            }
        }

        binding.segmented3.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.button_sec -> {
                    editor.putInt("selectedWindUnit",1)
                    editor.apply()
                }
                R.id.button_hour -> {
                    editor.putInt("selectedWindUnit", 2)
                    editor.apply()
                }
            }

        }
        windSP = sharedPreferences.getInt("selectedWindUnit", 1)
        if (windSP == 1) {
            binding.buttonSec.isChecked = true
        } else if (windSP == 2) {
            binding.buttonHour.isChecked = true
        }


    }

    private fun changeAppLocale(context: Context, newLocale: Locale) {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(newLocale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    private fun setArabicLocale() {
        changeAppLocale(requireContext(), Locale("ar"))
        listener.onLanguageChanged(false)
        // Save the language preference
        editor.putBoolean("isEnglish", false)
        editor.putBoolean("isArabic", true)
        editor.apply()

       (activity as? HomeActivity)?.restartActivity()
    }

    private fun setEnglishLocale() {
        changeAppLocale(requireContext(), Locale("en"))
        listener.onLanguageChanged(false)
        // Save the language preference
        editor.putBoolean("isEnglish", true)
        editor.putBoolean("isArabic", false)
        editor.apply()

        (activity as? HomeActivity)?.restartActivity()
    }

    interface OnLanguageChangeListener {
        fun onLanguageChanged(isEnglish: Boolean)
    }

    private lateinit var listener: OnLanguageChangeListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLanguageChangeListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnLanguageChangeListener")
        }
    }


}

