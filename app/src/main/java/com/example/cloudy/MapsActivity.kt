package com.example.cloudy

import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.cloudy.databinding.ActivityMapsBinding
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "MapsActivity"
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var search:EditText
    private lateinit var add:Button
    var cityList:MutableList<Address> = mutableListOf()
    var sharedFlow=MutableSharedFlow<String>()
    var dbList:MutableList<Address> = mutableListOf()



    // private lateinit var searchView: androidx.appcompat.widget.SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        search=findViewById(R.id.etName)
        add=findViewById(R.id.add_database)
        // searchView=findViewById(R.id.idSearchView)
        val geocoder = Geocoder(this@MapsActivity)
        var query=""
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        add.setOnClickListener {
            if (query.isNotBlank()){
                Toast.makeText(this@MapsActivity,"City added successfully",Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    sharedFlow.collectLatest {
                        dbList.add(cityList[0])
                        Log.i(TAG, "Button clicked: $dbList")
                    }
                }
            }else{
                Toast.makeText(this@MapsActivity,"No city to be added! please right a city name first",Toast.LENGTH_SHORT).show()
            }


        }
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                 query = s.toString()
                lifecycleScope.launch {
                    sharedFlow.emit(query)
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        lifecycleScope.launch {
            sharedFlow.collect { query ->
               if (query.isNotBlank()){
                   try {
                       cityList= geocoder.getFromLocationName(query,1)!!
                       val address = cityList[0]
                       val latLng = LatLng(address.latitude, address.longitude)

                       mMap.addMarker(MarkerOptions().position(latLng).title(query))
                       mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                   }catch (e:Exception){
                       Toast.makeText(this@MapsActivity,"please enter correct name",Toast.LENGTH_SHORT).show()

                   }
               }
            }
        }

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}