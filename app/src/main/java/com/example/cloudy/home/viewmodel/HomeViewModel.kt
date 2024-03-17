package com.example.cloudy.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloudy.model.WeatherItem
import com.example.cloudy.model.WeatherRepositoryImp
import com.example.cloudy.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"
class HomeViewModel(private val repositoryImp:WeatherRepositoryImp):ViewModel() {

    private var _weatherList = MutableLiveData<List<WeatherResponse>>()
    var weatherList: LiveData<List<WeatherResponse>> = _weatherList
    
   /* init {
        getWeather( latitude: Double,
            longitude: Double,
            apiKey: String)
    }
*/

    fun getWeather(latitude: Double, longitude: Double, apiKey: String,metric:String) {
        viewModelScope.launch {
                val weatherResponse = repositoryImp.getWeatherRepo(latitude, longitude, apiKey,metric)
                _weatherList.postValue(listOf(weatherResponse) as List<WeatherResponse>?)
            Log.i(TAG, "getWeather:${weatherResponse?.list} ")

        }
    }
}