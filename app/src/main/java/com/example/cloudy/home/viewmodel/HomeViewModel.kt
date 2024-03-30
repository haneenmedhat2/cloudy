package com.example.cloudy.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloudy.utility.ApiState
import com.example.cloudy.model.WeatherRepositoryImp
import com.example.cloudy.model.WeatherResponse
import com.example.cloudy.settings.SettingsFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"
class HomeViewModel(private val repositoryImp:WeatherRepositoryImp):ViewModel() {

    private var _weatherList = MutableStateFlow<ApiState<List<WeatherResponse?>>>(ApiState.Loading)
    var weatherList= _weatherList.asStateFlow()


    fun getWeather(latitude: Double, longitude: Double, apiKey: String,language:String,metric:String) {
        viewModelScope.launch(Dispatchers.IO) {
               repositoryImp.getWeatherRepo(latitude, longitude, apiKey,language,metric)
                   .catch { e ->
                       _weatherList.value= ApiState.Failure(e)
                   }
                   .collect { data -> _weatherList.value= ApiState.Success(data) }
        }
    }


}