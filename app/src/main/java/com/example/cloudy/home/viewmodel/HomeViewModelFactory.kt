package com.example.cloudy.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cloudy.model.WeatherRepositoryImp

class HomeViewModelFactory (private val _repo: WeatherRepositoryImp) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create (modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                HomeViewModel(_repo) as T
            } else {
                throw IllegalArgumentException("ViewModel Class not found")
            }
        }
}