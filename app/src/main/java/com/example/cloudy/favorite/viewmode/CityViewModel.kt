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



  fun getCity(cityName:MapCity) {
        viewModelScope.launch {
            repositoryImp.insertCity(cityName)

        }
    }
}