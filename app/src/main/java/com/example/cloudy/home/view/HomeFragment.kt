package com.example.cloudy.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudy.utility.ApiState
import com.example.cloudy.R
import com.example.cloudy.databinding.FragmentHomeBinding
import com.example.cloudy.databinding.FragmentSettingsBinding
import com.example.cloudy.utility.Util
import com.example.cloudy.db.LocalDataSourceImp
import com.example.cloudy.home.viewmodel.HomeViewModel
import com.example.cloudy.home.viewmodel.HomeViewModelFactory
import com.example.cloudy.model.WeatherItem
import com.example.cloudy.model.WeatherRepositoryImp
import com.example.cloudy.network.WeatherRemoteDataSourceImp
import com.example.cloudy.settings.SettingsFragment
import com.example.cloudy.settings.SettingsMapsActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.log

private const val TAG = "HomeFragment"
const val REQUEST_LOCATION_CODE= 2005

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    lateinit var fudedLocation: FusedLocationProviderClient
    lateinit var location: Location

    lateinit var weatherFactory: HomeViewModelFactory
    lateinit var viewModel: HomeViewModel

    private lateinit var dayAdapter: DayWeatherAdapter
    private lateinit var weakAdapter: WeakAdapter

    private lateinit var dayLayoutManager: LinearLayoutManager
    private lateinit var weakLayoutManager: LinearLayoutManager

    private lateinit var citySP: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor




    var dayList = mutableListOf<WeatherItem>()
    var weakList = mutableListOf<WeatherItem>()

    var cityLat= 0.0
    var cityLong=0.0

    var longitude=0.0
    var latitude=0.0

    companion object{
        var unitStr=""
        var windStr=""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        citySP = requireContext().getSharedPreferences("radio_button_prefs", Context.MODE_PRIVATE)
        editor = citySP.edit()
    }

    override  fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
    citySP = requireContext().getSharedPreferences("city_data", Context.MODE_PRIVATE)
    editor = citySP.edit()
    binding = FragmentHomeBinding.inflate(inflater, container, false)
    return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (checkPermissions()){
            if(isLocationEnabled()){
                getFreshLocation()
            }else{
                enableLocationServices()
            }
        }else{
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_LOCATION_CODE
            )
        }


        weatherFactory= HomeViewModelFactory(
            WeatherRepositoryImp.getInstance(WeatherRemoteDataSourceImp.getInstance(),LocalDataSourceImp(requireContext())))
        viewModel= ViewModelProvider(this,weatherFactory).get(HomeViewModel::class.java)


        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM")
        val current = LocalDateTime.now().format(formatter)
        binding.tvDate.text=current

        dayAdapter = DayWeatherAdapter()
        dayLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvDay
            .apply {
            adapter = dayAdapter
            layoutManager = dayLayoutManager
        }

        weakAdapter = WeakAdapter()
        weakLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvWeak.apply {
            adapter = weakAdapter
            layoutManager = weakLayoutManager
        }

        setUpLocationRequest()

        lifecycleScope.launch {
            SettingsMapsActivity.cityList.collectLatest { city ->
                if (city.isNotEmpty()){
                    cityLat= city[0].lat
                    Log.i(TAG, "onViewCreated: $cityLat")
                    cityLong= city[0].lon
                    Log.i(TAG, "onViewCreated: $cityLong")

                    editor.putString("lat", cityLat.toString())
                    editor.putString("lon", cityLong.toString())
                    editor.apply()
                    fetchWeatherData()
                }
            }
        }

        val valueString1 = citySP.getString("lat", null)
         cityLat= (valueString1?.toDoubleOrNull()?: 0.0)

        val valueString2 = citySP.getString("lon", null)
         cityLong= (valueString2?.toDoubleOrNull()?: 0.0)


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode== REQUEST_LOCATION_CODE){
            if (grantResults.size >1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getFreshLocation()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationPermission || coarseLocationPermission
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    fun  getFreshLocation(){
        fudedLocation = LocationServices.getFusedLocationProviderClient(requireContext())
        fudedLocation.requestLocationUpdates(
            LocationRequest.Builder(0)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .build(),
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    location = locationResult.lastLocation!!
                    longitude = location.longitude
                    latitude = location.latitude
                    Log.i(TAG, "onLocationResult: ${location.longitude}, ${location.latitude}")
                    fudedLocation.removeLocationUpdates(this)

                }
            },
            Looper.myLooper()
        )
    }

    fun  enableLocationServices(){
        Toast.makeText(requireContext(),"Please turn on location", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun setUpLocationRequest() {
        fudedLocation = LocationServices.getFusedLocationProviderClient(requireContext())
        if (checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_CODE
            )
            return
        }

        fudedLocation.requestLocationUpdates(
            LocationRequest.Builder(0)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .build(),
            object : LocationCallback() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    location = locationResult.lastLocation!!
                    longitude = location.longitude
                    latitude = location.latitude
                    Log.i(TAG, "onLocationResult: $longitude, $latitude")
                    fudedLocation.removeLocationUpdates(this)
                    fetchWeatherData()
                }
            },
            Looper.myLooper()
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchWeatherData() {
        if (SettingsFragment.windSP==1){
            windStr="m/s"
            if(SettingsFragment.locationSP==1 && SettingsFragment.languageSP && SettingsFragment.temperatureSP==1){
                viewModel.getWeather(cityLat, cityLong, Util.API_KEY, "en","standard")
                unitStr="K"
            }
            else if(SettingsFragment.locationSP==1 && SettingsFragment.languageSP && SettingsFragment.temperatureSP==2){
                viewModel.getWeather(cityLat, cityLong, Util.API_KEY, "en","metric")
                unitStr="°C"
            }
            else if(SettingsFragment.locationSP==1 && !SettingsFragment.languageSP && SettingsFragment.temperatureSP==1){
                viewModel.getWeather(cityLat, cityLong, Util.API_KEY, "ar","standard")
                unitStr="K"
            }
            else if(SettingsFragment.locationSP==1 && !SettingsFragment.languageSP && SettingsFragment.temperatureSP==2){
                viewModel.getWeather(cityLat, cityLong, Util.API_KEY, "ar","metric")
                unitStr="°C"
            }
            else if(SettingsFragment.locationSP==0 && SettingsFragment.languageSP && SettingsFragment.temperatureSP==1){
                viewModel.getWeather(latitude, longitude, Util.API_KEY, "en","standard")
                unitStr="K"
            }
            else if(SettingsFragment.locationSP==0 && SettingsFragment.languageSP && SettingsFragment.temperatureSP==2){
                viewModel.getWeather(latitude, longitude, Util.API_KEY, "en","metric")
                unitStr="°C"
            }
            else if(SettingsFragment.locationSP==0 && !SettingsFragment.languageSP && SettingsFragment.temperatureSP==1){
                viewModel.getWeather(latitude, longitude, Util.API_KEY, "ar","standard")
                unitStr="K"
            }
            else if(SettingsFragment.locationSP==0 && !SettingsFragment.languageSP && SettingsFragment.temperatureSP==2){
                viewModel.getWeather(latitude, longitude, Util.API_KEY, "ar","metric")
                unitStr="°C"
            }else{
                viewModel.getWeather(latitude, longitude, Util.API_KEY, "en","standard")
                unitStr="K"
            }
        }
        else if (SettingsFragment.windSP==2) {
            windStr="m/hr"
            if (SettingsFragment.locationSP == 1 && SettingsFragment.languageSP && SettingsFragment.temperatureSP == 3) {
                viewModel.getWeather(cityLat, cityLong, Util.API_KEY, "en", "imperial")
                unitStr="°F"
            }
            else if (SettingsFragment.locationSP == 1 && !SettingsFragment.languageSP && SettingsFragment.temperatureSP == 3) {
                viewModel.getWeather(cityLat, cityLong, Util.API_KEY, "ar", "imperial")
                unitStr="°F"
            }
            else  if (SettingsFragment.locationSP == 0 && SettingsFragment.languageSP && SettingsFragment.temperatureSP == 3) {
                viewModel.getWeather(latitude, longitude, Util.API_KEY, "en", "imperial")
                unitStr="°F"
            }

            else if (SettingsFragment.locationSP == 0 && !SettingsFragment.languageSP && SettingsFragment.temperatureSP == 3) {
                viewModel.getWeather(latitude, longitude, Util.API_KEY, "ar", "imperial")
                unitStr="°F"
            }else{
                viewModel.getWeather(latitude, longitude, Util.API_KEY, "en","standard")
                unitStr="K"
            }
        }
        else{
            viewModel.getWeather(latitude, longitude, Util.API_KEY, "en","standard")
            unitStr="K"
        }

        lifecycleScope.launch {
            viewModel.weatherList.collectLatest { weatherList ->
                    Log.i(TAG, "fetchWeatherData: first launch")
                    when (weatherList) {
                        is ApiState.Loading -> {
                            binding.apply {
                                rvDay.visibility = View.GONE
                                rvWeak.visibility = View.GONE
                                progress.visibility = View.VISIBLE
                            }
                        }
                        is ApiState.Success -> {
                            binding.rvDay.visibility = View.VISIBLE
                            binding.rvWeak.visibility = View.VISIBLE
                            binding.progress.visibility = View.GONE
                            val weatherData = weatherList.data
                            weatherData?.let {
                                binding.tvWeather.text = weatherData.list[0].weather[0].description
                                binding.tvDegree.text = "${weatherData.list[0].main.temp} $unitStr"
                                binding.tvHumidity.text = "${weatherData.list[0].main.humidity}%"
                                binding.tvPressure.text = "${weatherData.list[0].main.pressure} hPa"
                                binding.tvWindSpeed.text = "${weatherData.list[0].wind.speed} $windStr"
                                binding.tvCurrentLocation.text = weatherData.city.name
                                binding.tvCloud.text = weatherData.list[0].clouds.all.toString()
                                var icon = weatherData.list[0].weather[0].icon

                                if (icon == "01d" || icon == "01n") {
                                    binding.ivPhoto.setImageResource(R.drawable.sunny)
                                }
                                if (icon == "02d" || icon == "02n" || icon == "03d" || icon == "03n" || icon == "04d" || icon == "04n") {
                                    binding.ivPhoto.setImageResource(R.drawable.cloud)
                                }
                                if (icon == "09d" || icon == "09n" || icon == "10d" || icon == "10n") {
                                    binding.ivPhoto.setImageResource(R.drawable.rain)
                                }
                                if (icon == "11d" || icon == "11n") {
                                    binding.ivPhoto.setImageResource(R.drawable.thunder)
                                }
                                if (icon == "13d" || icon == "13n") {
                                    binding.ivPhoto.setImageResource(R.drawable.snow)
                                }
                                if (icon == "50d" || icon == "50n") {
                                    binding.ivPhoto.setImageResource(R.drawable.mist)
                                }

                                Log.i(TAG, "onCreate: ${weatherData.city.name}")

                                val currentDateString =
                                    LocalDate.now()
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))


                                var dayWeather =
                                    weatherData.list.subList(0, minOf(6, weatherData.list.size))

                                dayList.addAll(dayWeather)
                                dayAdapter.submitList(dayList)

                                var weakWeather = weatherData.list
                                var hashSet = HashSet<String>()
                                weakWeather.forEach { weather ->
                                    var apidDate = weather.dt_txt.split(" ")[0]
                                    if (apidDate != currentDateString && hashSet.add(apidDate)) {
                                        weakList.add(weather)
                                    }
                                }
                                weakAdapter.submitList(weakList)
                            }

                        }
                        else -> {
                            binding.progress.visibility = View.GONE
                            Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
        }
}

