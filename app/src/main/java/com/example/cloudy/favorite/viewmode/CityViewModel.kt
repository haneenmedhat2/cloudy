package com.example.cloudy.favorite.viewmode

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloudy.model.MapCity
import com.example.cloudy.model.WeatherRepositoryImp
import com.example.cloudy.model.WeatherResponse
import kotlinx.coroutines.launch

class CityViewModel (private val repositoryImp: WeatherRepositoryImp): ViewModel() {

    private var _cityList = MutableLiveData<List<MapCity>>()
    var cityList: LiveData<List<MapCity>> = _cityList

/*    fun getCity(cityName) {
        viewModelScope.launch {
            val weatherResponse = repositoryImp.insertCity(cityName)
            _cityList.postValue(listOf(MapCity) as List<MapCity>?)

        }
    }*/
}