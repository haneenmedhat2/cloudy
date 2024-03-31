package com.example.cloudy.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cloudy.model.IWeatherRepositoryImp
import com.example.cloudy.model.WeatherRepositoryImp

class CityViewModelFactory( private val _repo: IWeatherRepositoryImp) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CityViewModel::class.java)) {
            CityViewModel(_repo) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }

}