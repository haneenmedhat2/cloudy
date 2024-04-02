package com.example.cloudy.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cloudy.favorite.viewmodel.CityViewModel
import com.example.cloudy.model.WeatherRepositoryImp

class AlertViewModelFactory ( private val _repo: WeatherRepositoryImp) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            AlertViewModel(_repo) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }

}