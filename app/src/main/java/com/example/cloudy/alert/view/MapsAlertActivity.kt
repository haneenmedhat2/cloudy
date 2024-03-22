package com.example.cloudy.alert.view

import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cloudy.R
import com.example.cloudy.alert.viewmodel.AlertViewModel
import com.example.cloudy.alert.viewmodel.AlertViewModelFactory

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.cloudy.databinding.ActivityMapsAlertBinding
import com.example.cloudy.db.LocalDataSourceImp
import com.example.cloudy.model.Alert
import com.example.cloudy.model.WeatherRepositoryImp
import com.example.cloudy.network.WeatherRemoteDataSourceImp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MapsAlertActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsAlertBinding
    var cityList:MutableList<Address> = mutableListOf()
    var sharedFlow= MutableSharedFlow<String>()
    private lateinit var city: Alert
    private lateinit var viewFactory: AlertViewModelFactory
    private lateinit var viewModel: AlertViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewFactory = AlertViewModelFactory(
            WeatherRepositoryImp.getInstance
                (WeatherRemoteDataSourceImp.getInstance(), LocalDataSourceImp(this@MapsAlertActivity))
        )

        viewModel = ViewModelProvider(this, viewFactory).get(AlertViewModel::class.java)

        val geocoder = Geocoder(this@MapsAlertActivity)
        var query=""
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        binding.addDatabase.setOnClickListener {
            if (query.isNotBlank()){
                val cityName = query
                val cityAddress = cityList[0]
                 city = Alert(cityName, cityAddress.latitude, cityAddress.longitude)
                Toast.makeText(this@MapsAlertActivity, "City added successfully", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    viewModel.inserAlert(city)
                }

            }
            else{
                Toast.makeText(
                    this@MapsAlertActivity,
                    "No city to be added! please right a city name first",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
       /* binding.exit.setOnClickListener {
            if (::city.isInitialized) {

                val fragment = AlertFragment()
                val bundle = Bundle().apply {
                    putString("cityName", city.cityName)
                    putDouble("cityLat", city.lat)
                    putDouble("cityLong", city.lon)
                }
                fragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit()

            }else {
                Toast.makeText(
                    this@MapsAlertActivity,
                    "City not saved!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }*/




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
                        Toast.makeText(
                            this@MapsAlertActivity,
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