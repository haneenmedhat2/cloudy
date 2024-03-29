package com.example.cloudy.settings

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.cloudy.HomeActivity
import com.example.cloudy.R
import com.example.cloudy.databinding.ActivityMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.cloudy.databinding.ActivitySettingsMapsBinding
import com.example.cloudy.model.Coord
import com.example.cloudy.model.MapCity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "SettingsMapsActivity"
class SettingsMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var search: EditText
    private lateinit var add: FloatingActionButton
    var city:MutableList<Address> = mutableListOf()

    var sharedFlow= MutableSharedFlow<String>()

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivitySettingsMapsBinding

    companion object{
        private var _cityList = MutableStateFlow<List<Coord>>(emptyList())
        var cityList=_cityList.asStateFlow()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val geocoder = Geocoder(this@SettingsMapsActivity)
        var query=""
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        binding.addDatabase.setOnClickListener {
            if (query.isNotBlank()){
                val cityName = query
                val cityAddress = city[0]
                val cityLat =  cityAddress.latitude
                val cityLong = cityAddress.longitude
                _cityList.value= listOf(Coord(cityLat,cityLong))
                Log.i(TAG, "onCreate:${_cityList.value} ")
                lifecycleScope.launch {
                    if (cityLat!=0.0 && cityLong!=0.0){
                        val intent=Intent(this@SettingsMapsActivity,HomeActivity::class.java)
                        startActivity(intent)
                    }

                }

            }
            else{
                Toast.makeText(
                    this@SettingsMapsActivity,
                    "No city to be added! please right a city name first",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }



        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                query = s.toString()
                lifecycleScope.launch {
                    delay(1000)
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
                        city= geocoder.getFromLocationName(query,1)!!
                        val address = city[0]
                        val latLng = LatLng(address.latitude, address.longitude)

                        mMap.addMarker(MarkerOptions().position(latLng).title(query))
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                    }catch (e:Exception){
                        Toast.makeText(
                            this@SettingsMapsActivity,
                            "please enter correct name",
                            Toast.LENGTH_SHORT
                        ).show()

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