package com.example.cloudy.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cloudy.R
import com.example.cloudy.databinding.FragmentSettingsBinding
import com.example.cloudy.model.SettingsData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         locationSP = sharedPreferences.getInt("selectedRadioButtonId", -1)
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

         languageSP = sharedPreferences.getBoolean("isEnglish", false)
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


        temperatureSP = sharedPreferences.getInt("selectedTemperature", 1)
        if (temperatureSP == 1) {
            binding.buttonKelvin.isChecked = true
        } else if (temperatureSP == 2) {
            binding.buttonCelsius.isChecked = true
        }else if (temperatureSP==3){
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


        windSP = sharedPreferences.getInt("selectedWindUnit", 1)
        if (windSP == 1) {
            binding.buttonSec.isChecked = true
        } else if (windSP == 2) {
            binding.buttonHour.isChecked = true
        }

        binding.segmented4.setOnCheckedChangeListener { group, checkedId ->
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


    }
}