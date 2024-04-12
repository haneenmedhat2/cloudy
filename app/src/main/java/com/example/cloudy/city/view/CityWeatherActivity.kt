package com.example.cloudy.city.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
import com.example.cloudy.databinding.ActivityCityWeatherBinding
import com.example.cloudy.db.LocalDataSourceImp
import com.example.cloudy.home.view.DayWeatherAdapter
import com.example.cloudy.home.view.WeakAdapter
import com.example.cloudy.model.WeatherItem
import com.example.cloudy.model.WeatherRepositoryImp
import com.example.cloudy.network.WeatherRemoteDataSourceImp
import com.example.cloudy.settings.SettingsFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val REQUEST_LOCATION_CODE= 2005

class CityWeatherActivity : AppCompatActivity() {

    lateinit var binding: ActivityCityWeatherBinding

    lateinit var weatherFactory: CityWeatherViewModelFactory
    lateinit var viewModel: CityWeatherViewModel

    private lateinit var dayAdapter: DayWeatherAdapter
    private lateinit var weakAdapter: WeakAdapter

    private lateinit var dayLayoutManager: LinearLayoutManager
    private lateinit var weakLayoutManager: LinearLayoutManager

    var lat = 0.0
    var lon = 0.0

    var dayList = mutableListOf<WeatherItem>()
    var weakList = mutableListOf<WeatherItem>()

    companion object {
        var unitStr = ""
        var windStr = ""
    }

    override fun onResume() {
        super.onResume()
        if(SettingsFragment.languageSP) {
        this.title = "City Weather"
        }else{
            this.title = "طقس المدينة "

        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)


        lat = intent.getDoubleExtra("latitude", 0.0)
        lon = intent.getDoubleExtra("longitude", 0.0)

        weatherFactory = CityWeatherViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp.getInstance(),
                LocalDataSourceImp(this)
            )
        )
        viewModel = ViewModelProvider(this, weatherFactory).get(CityWeatherViewModel::class.java)

        binding.apply {
            tvCurrentLocation.visibility = View.GONE
            tvDate.visibility = View.GONE
            ivPhoto.visibility = View.GONE
            tvDegree.visibility = View.GONE
            tvWeather.visibility = View.GONE
            cardView.visibility = View.GONE
             cardView1!!.visibility=View.GONE
                cardView3!!.visibility=View.GONE
                imageView2!!.visibility=View.GONE

        }


        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM")
        val current = LocalDateTime.now().format(formatter)
        binding.tvDate.text = current

        dayAdapter = DayWeatherAdapter()
        dayLayoutManager =
            LinearLayoutManager(this@CityWeatherActivity, RecyclerView.HORIZONTAL, false)
        binding.rvDay.apply {
            adapter = dayAdapter
            layoutManager = dayLayoutManager
        }

        weakAdapter = WeakAdapter()
        weakLayoutManager =
            LinearLayoutManager(this@CityWeatherActivity, RecyclerView.VERTICAL, false)
        binding.rvWeak.apply {
            adapter = weakAdapter
            layoutManager = weakLayoutManager
        }
        if (lat != 0.0 && lon != 0.0) {
            fetchWeatherData()
        }

        binding.swipper.setOnRefreshListener {
            binding.swipper.isRefreshing=false
        }

    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchWeatherData() {


        val connectivityManager = this?.let {
            it.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
        val networkInfo = connectivityManager?.activeNetworkInfo
        val isConnected = networkInfo?.isConnectedOrConnecting == true

        if (SettingsFragment.windSP == 1) {
            windStr = "m/s"
            if (SettingsFragment.languageSP && SettingsFragment.temperatureSP == 1) {
                viewModel.getWeather(lat, lon, Util.API_KEY, "en", "standard")
                unitStr = "K"
            } else if (SettingsFragment.languageSP && SettingsFragment.temperatureSP == 2) {
                viewModel.getWeather(lat, lon, Util.API_KEY, "en", "metric")
                unitStr = "°C"
            } else if (!SettingsFragment.languageSP && SettingsFragment.temperatureSP == 1) {
                viewModel.getWeather(lat, lon, Util.API_KEY, "ar", "standard")
               unitStr = "K"
            } else if (!SettingsFragment.languageSP && SettingsFragment.temperatureSP == 2) {
                viewModel.getWeather(lat, lon, Util.API_KEY, "ar", "metric")
                unitStr = "°C"
            } else {
                viewModel.getWeather(lat, lon, Util.API_KEY, "en", "standard")
                unitStr = "K"
            }
        } else if (SettingsFragment.windSP == 2) {
                windStr = "m/hr"
            if (SettingsFragment.languageSP && SettingsFragment.temperatureSP == 3) {
                viewModel.getWeather(lat, lon, Util.API_KEY, "en", "imperial")
              unitStr = "°F"
            } else if (!SettingsFragment.languageSP && SettingsFragment.temperatureSP == 3) {
                viewModel.getWeather(lat, lon, Util.API_KEY, "ar", "imperial")
                unitStr = "°F"
            } else {
                viewModel.getWeather(lat, lon, Util.API_KEY, "en", "standard")
                unitStr = "K"
            }
        }

        lifecycleScope.launch {
            viewModel.weatherList.collectLatest { weatherList ->
                when (weatherList) {
                    is ApiState.Loading -> {
                        binding.apply {
                            rvDay.visibility = View.GONE
                            rvWeak.visibility = View.GONE
                            tvCurrentLocation.visibility=View.GONE
                            tvDate.visibility=View.GONE
                            ivPhoto.visibility=View.GONE
                            tvDegree.visibility=View.GONE
                            tvWeather.visibility=View.GONE
                            cardView.visibility=View.GONE
                            cardView1!!.visibility=View.GONE
                            cardView3!!.visibility=View.GONE
                            imageView2!!.visibility=View.GONE
                            progress.visibility = View.VISIBLE
                        }

                    }

                    is ApiState.Success -> {
                        binding.apply {
                            rvDay.visibility = View.VISIBLE
                            rvWeak.visibility = View.VISIBLE
                            tvCurrentLocation.visibility = View.VISIBLE
                            tvDate.visibility = View.VISIBLE
                            ivPhoto.visibility = View.VISIBLE
                            tvDegree.visibility = View.VISIBLE
                            tvWeather.visibility = View.VISIBLE
                            cardView.visibility = View.VISIBLE
                            cardView1!!.visibility=View.VISIBLE
                            cardView3!!.visibility=View.VISIBLE
                            imageView2!!.visibility=View.VISIBLE
                            binding.progress.visibility = View.GONE
                        }
                        val weatherData = weatherList.data
                        weatherData?.let {
                            weatherData?.let {
                                binding.tvWeather.text = weatherData.list[0].weather[0].description
                                binding.tvDegree.text = "${weatherData.list[0].main.temp} ${unitStr}"
                                binding.tvHumidity.text = "${weatherData.list[0].main.humidity}%"
                                binding.tvPressure.text = "${weatherData.list[0].main.pressure} hPa"
                                binding.tvWindSpeed.text =
                                    "${weatherData.list[0].wind.speed} ${windStr}"
                                binding.tvCurrentLocation.text = weatherData.city.name
                               // binding.tvCloud.text = weatherData.list[0].clouds.all.toString()
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
                    }

                    else -> {
                        if (!isConnected) {
                            val snackbar = Snackbar.make(
                                binding.cl,
                                "No Network Connection",
                                Snackbar.LENGTH_SHORT
                            )
                                .setAction("Dismiss") {
                                }.setActionTextColor(R.color.md_red_900)
                            snackbar.show()
                        }
                        binding.progress.visibility = View.GONE
                        Toast.makeText(this@CityWeatherActivity, "Error", Toast.LENGTH_SHORT).show()
                    }


                }
            }
        }
    }
}
