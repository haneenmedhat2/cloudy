package com.example.cloudy.city.view

import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudy.utility.ApiState
import com.example.cloudy.R
import com.example.cloudy.utility.Util
import com.example.cloudy.city.viewmodel.CityWeatherViewModel
import com.example.cloudy.city.viewmodel.CityWeatherViewModelFactory
import com.example.cloudy.db.LocalDataSourceImp
import com.example.cloudy.home.view.DayWeatherAdapter
import com.example.cloudy.home.view.WeakAdapter
import com.example.cloudy.model.WeatherItem
import com.example.cloudy.model.WeatherRepositoryImp
import com.example.cloudy.network.WeatherRemoteDataSourceImp
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val REQUEST_LOCATION_CODE= 2005

class CityWeatherActivity : AppCompatActivity() {
    lateinit var fudedLocation: FusedLocationProviderClient
    lateinit var location: Location
    lateinit var weatherFactory: CityWeatherViewModelFactory
    lateinit var viewModel: CityWeatherViewModel
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
    var lat=0.0
    var lon=0.0

    var dayList = mutableListOf<WeatherItem>()
    var weakList = mutableListOf<WeatherItem>()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_weather)
         lat=intent.getDoubleExtra("latitude",0.0)
         lon=intent.getDoubleExtra("longitude",0.0)

        weatherFactory= CityWeatherViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp.getInstance(),
                LocalDataSourceImp(this)
            ))
        viewModel= ViewModelProvider(this,weatherFactory).get(CityWeatherViewModel::class.java)

        recyclerView1=findViewById(R.id.rv_day)
        recyclerView2=findViewById(R.id.rv_weak)

        tvLocation=findViewById(R.id.tv_current_location)
        tvDate=findViewById(R.id.tv_date)
        _weather=findViewById(R.id.tv_weather)
        degree =findViewById(R.id.tv_degree)
        humidity =findViewById(R.id.tv_humidity)
        wind = findViewById(R.id.tv_wind_speed)
        cloud=findViewById(R.id.tv_cloud)
        pressure =findViewById(R.id.tv_pressure)
        image =findViewById(R.id.ivPhoto)
        progressBar=findViewById(R.id.progress)


        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM")
        val current = LocalDateTime.now().format(formatter)
        tvDate.text=current

        dayAdapter = DayWeatherAdapter()
        dayLayoutManager = LinearLayoutManager(this@CityWeatherActivity, RecyclerView.HORIZONTAL, false)
        recyclerView1.apply {
            adapter = dayAdapter
            layoutManager = dayLayoutManager
        }

        weakAdapter = WeakAdapter()
        weakLayoutManager = LinearLayoutManager(this@CityWeatherActivity, RecyclerView.VERTICAL, false)
        recyclerView2.apply {
            adapter = weakAdapter
            layoutManager = weakLayoutManager
        }
        if (lat!=0.0 && lon!=0.0){
            fetchWeatherData()
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchWeatherData() {
        viewModel.getWeather(lat, lon, Util.API_KEY, "metric")
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
                        Toast.makeText(this@CityWeatherActivity, "Error", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }
}