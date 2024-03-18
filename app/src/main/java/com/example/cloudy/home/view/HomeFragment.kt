package com.example.cloudy.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudy.ApiState
import com.example.cloudy.R
import com.example.cloudy.Util
import com.example.cloudy.db.CityLocalDataSourceImp
import com.example.cloudy.home.viewmodel.HomeViewModel
import com.example.cloudy.home.viewmodel.HomeViewModelFactory
import com.example.cloudy.model.WeatherItem
import com.example.cloudy.model.WeatherRepositoryImp
import com.example.cloudy.network.WeatherRemoteDataSourceImp
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

private const val TAG = "HomeFragment"
const val REQUEST_LOCATION_CODE= 2005

class HomeFragment : Fragment() {
    lateinit var fudedLocation: FusedLocationProviderClient
    lateinit var location: Location
    lateinit var weatherFactory: HomeViewModelFactory
    lateinit var viewModel: HomeViewModel
    private lateinit var recyclerView1: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var dayAdapter: DayWeatherAdapter
    private lateinit var weakAdapter: WeakAdapter
    private lateinit var dayLayoutManager: LinearLayoutManager
    private lateinit var weakLayoutManager: LinearLayoutManager
    private lateinit var tvLocation: TextView
    private lateinit var tvDate: TextView
    private lateinit var _weather: TextView
    private lateinit var degree: TextView
    private lateinit var humidity: TextView
    private lateinit var wind: TextView
    private lateinit var pressure: TextView
    private lateinit var cloud: TextView
    private lateinit var image: ImageView
    private lateinit var progressBar: ProgressBar

    var dayList = mutableListOf<WeatherItem>()
    var weakList = mutableListOf<WeatherItem>()


    var longitude=0.0
    var latitude=0.0
override  fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
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
            WeatherRepositoryImp.getInstance(WeatherRemoteDataSourceImp.getInstance(),CityLocalDataSourceImp(requireContext())))
        viewModel= ViewModelProvider(this,weatherFactory).get(HomeViewModel::class.java)

        recyclerView1=view.findViewById(R.id.rv_day)
        recyclerView2=view.findViewById(R.id.rv_weak)

        tvLocation=view.findViewById(R.id.tv_current_location)
        tvDate=view.findViewById(R.id.tv_date)
        _weather=view.findViewById(R.id.tv_weather)
        degree =view.findViewById(R.id.tv_degree)
        humidity =view.findViewById(R.id.tv_humidity)
        wind = view.findViewById(R.id.tv_wind_speed)
        cloud=view.findViewById(R.id.tv_cloud)
        pressure = view.findViewById(R.id.tv_pressure)
        image =view.findViewById(R.id.ivPhoto)
        progressBar=view.findViewById(R.id.progress)


        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM")
        val current = LocalDateTime.now().format(formatter)
        tvDate.text=current

        dayAdapter = DayWeatherAdapter()
        dayLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recyclerView1.apply {
            adapter = dayAdapter
            layoutManager = dayLayoutManager
        }

        weakAdapter = WeakAdapter()
        weakLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerView2.apply {
            adapter = weakAdapter
            layoutManager = weakLayoutManager
        }

        setUpLocationRequest()

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
        viewModel.getWeather(latitude, longitude, Util.API_KEY, "metric")
        lifecycleScope.launch {
            viewModel.weatherList.collectLatest { weatherList ->
                when(weatherList) {
                    is ApiState.Loading -> {
                        recyclerView1.visibility = View.GONE
                        recyclerView2.visibility = View.GONE
                        progressBar.visibility = View.VISIBLE
                    }

                    is ApiState.Success -> {
                        recyclerView1.visibility = View.VISIBLE
                        recyclerView2.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        val weatherData = weatherList.data
                        weatherData?.let {
                            _weather.text = weatherData.list[0].weather[0].description
                            degree.text = "${weatherData.list[0].main.temp}°C"
                            humidity.text = "${weatherData.list[0].main.humidity}%"
                            pressure.text = "${weatherData.list[0].main.pressure} hPa"
                            wind.text = "${weatherData.list[0].wind.speed} m/s"
                            tvLocation.text = weatherData.city.name
                            cloud.text = weatherData.list[0].clouds.all.toString()
                            var icon = weatherData.list[0].weather[0].icon

                            if (icon == "01d" || icon == "01n") {
                                image.setImageResource(R.drawable.sunny)
                            }
                            if (icon == "02d" || icon == "02n" || icon == "03d" || icon == "03n" || icon == "04d" || icon == "04n") {
                                image.setImageResource(R.drawable.cloud)
                            }
                            if (icon == "09d" || icon == "09n" || icon == "10d" || icon == "10n") {
                                image.setImageResource(R.drawable.rain)
                            }
                            if (icon == "11d" || icon == "11n") {
                                image.setImageResource(R.drawable.thunder)
                            }
                            if (icon == "13d" || icon == "13n") {
                                image.setImageResource(R.drawable.snow)
                            }
                            if (icon == "50d" || icon == "50n") {
                                image.setImageResource(R.drawable.mist)
                            }

                            Log.i(TAG, "onCreate: ${weatherData.city.name}")

                            val currentDateString =
                                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))


                            var dayWeather = weatherData.list.subList(0, minOf(6, weatherData.list.size))

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
                        progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                }

                }
            }
        }
}