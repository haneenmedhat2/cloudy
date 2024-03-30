package com.example.cloudy.city.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cloudy.model.IWeatherRepositoryImp
import com.example.cloudy.model.WeatherRepositoryImp

class CityWeatherViewModelFactory (private val _repo: IWeatherRepositoryImp) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create (modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(CityWeatherViewModel::class.java)) {
                CityWeatherViewModel(_repo) as T
            } else {
                throw IllegalArgumentException("ViewModel Class not found")
            }
        }
}