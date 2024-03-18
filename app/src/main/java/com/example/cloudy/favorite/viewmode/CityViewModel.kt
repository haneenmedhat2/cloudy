package com.example.cloudy.favorite.viewmode

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloudy.ApiState
import com.example.cloudy.model.MapCity
import com.example.cloudy.model.WeatherRepositoryImp
import com.example.cloudy.model.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CityViewModel (private val repositoryImp: WeatherRepositoryImp): ViewModel() {

    private var _cityList = MutableStateFlow<List<MapCity?>>(emptyList())
    var cityList= _cityList.asStateFlow()

    init {
        getAllCity()
    }

  fun insertCity(cityName:MapCity) {
        viewModelScope.launch {
            repositoryImp.insertCity(cityName)

        }
    }

    fun deleteCity(cityName:MapCity) {
        viewModelScope.launch {
            repositoryImp.deleteCity(cityName)
            getAllCity()

        }
    }

    fun getAllCity(){
     viewModelScope.launch{
         repositoryImp.getAllCities().collect { cities ->
             _cityList.value = cities
         }
     }
    }
}