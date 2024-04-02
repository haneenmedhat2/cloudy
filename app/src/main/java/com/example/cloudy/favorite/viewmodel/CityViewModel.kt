package com.example.cloudy.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloudy.model.IWeatherRepositoryImp
import com.example.cloudy.model.MapCity
import com.example.cloudy.model.WeatherRepositoryImp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CityViewModel (private val repositoryImp: IWeatherRepositoryImp): ViewModel() {

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