package com.example.cloudy.favorite

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.cloudy.MapsActivity
import com.example.cloudy.R

class FavoriteFragment : Fragment() {

    lateinit var add:ImageView
    lateinit var text:TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add=view.findViewById(R.id.btnAdd)
        text=view.findViewById(R.id.tv)

        add.setOnClickListener {

        }
    }


}