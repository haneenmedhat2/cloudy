package com.example.cloudy.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import com.example.cloudy.R
import com.example.cloudy.databinding.FragmentAlertBinding
import com.example.cloudy.databinding.FragmentSettingsBinding
import com.example.cloudy.favorite.view.MapsActivity


class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonMap!!.setOnClickListener{
            val intent = Intent(requireContext(), SettingsMapsActivity::class.java)
            startActivity(intent)
        }

    }


}