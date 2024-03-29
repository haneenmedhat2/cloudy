package com.example.cloudy.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.cloudy.R
import com.example.cloudy.databinding.FragmentAlertBinding
import com.example.cloudy.databinding.FragmentSettingsBinding
import com.example.cloudy.favorite.view.MapsActivity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor :SharedPreferences.Editor

   companion object {
        private val _selectedOption = MutableSharedFlow<String>()
        val selectedOption: SharedFlow<String> = _selectedOption

        suspend fun setSelectedOption(option: String) {
            _selectedOption.emit(option)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("radio_button_prefs", Context.MODE_PRIVATE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            val locarionSP=sharedPreferences.getInt("selectedRadioButtonId", -1)
             editor=sharedPreferences.edit()

        if (locarionSP==1){
            binding.buttonMap.isChecked=true
        }else if(locarionSP==0){
            binding.buttonGps.isChecked=true
        }

        binding.segmented1.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId==R.id.button_map){
                editor.putInt("selectedRadioButtonId",1)
            }else if (checkedId== R.id.button_gps){
                editor.putInt("selectedRadioButtonId",0)
            }
            editor.commit()
        }

        binding.buttonMap.setOnClickListener {
            lifecycleScope.launch {
             setSelectedOption("Map")
            }
            val intent = Intent(requireContext(), SettingsMapsActivity::class.java)
            startActivity(intent)
        }

        binding.buttonGps.setOnClickListener {
            lifecycleScope.launch {
               setSelectedOption("GPS")
            }

        }

    }
}