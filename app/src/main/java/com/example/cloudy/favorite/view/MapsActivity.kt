package com.example.cloudy.favorite.view

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cloudy.R
import com.example.cloudy.databinding.ActivityMapsBinding
import com.example.cloudy.db.LocalDataSourceImp
import com.example.cloudy.favorite.viewmodel.CityViewModel
import com.example.cloudy.favorite.viewmodel.CityViewModelFactory
import com.example.cloudy.model.MapCity
import com.example.cloudy.model.WeatherRepositoryImp
import com.example.cloudy.network.WeatherRemoteDataSourceImp
import com.example.cloudy.settings.SettingsFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

private const val TAG = "MapsActivity"

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var search: EditText
    private lateinit var add: FloatingActionButton
    var cityList:MutableList<Address> = mutableListOf()
    var sharedFlow= MutableSharedFlow<String>()


    private lateinit var viewFactory:CityViewModelFactory
    private lateinit var viewModel: CityViewModel

    override fun onResume() {
        super.onResume()
        if(SettingsFragment.languageSP) {
            this.title = "Map"
        }else{
            this.title = " الخريطة "

        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewFactory=CityViewModelFactory(  WeatherRepositoryImp.getInstance
            (WeatherRemoteDataSourceImp.getInstance(),LocalDataSourceImp(this@MapsActivity)))

        viewModel= ViewModelProvider(this,viewFactory).get(CityViewModel::class.java)

        val geocoder = Geocoder(this@MapsActivity)
        var query=""
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        binding.addDatabase.setOnClickListener {
            if (query.isNotBlank()){
                val cityName = query
                val cityAddress = cityList[0]
                val city = MapCity(cityName, cityAddress.latitude, cityAddress.longitude)
                lifecycleScope.launch {
                    viewModel.insertCity(city)
                }
                        Toast.makeText(this@MapsActivity, "City added successfully", Toast.LENGTH_SHORT).show()

                }
         else{
            Toast.makeText(
                this@MapsActivity,
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
                       cityList= geocoder.getFromLocationName(query,1)!!
                       val address = cityList[0]
                       val latLng = LatLng(address.latitude, address.longitude)

                       mMap.addMarker(MarkerOptions().position(latLng).title(query))
                       mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                   }catch (e:Exception){
                       Log.i(TAG, "onCreate: error")

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