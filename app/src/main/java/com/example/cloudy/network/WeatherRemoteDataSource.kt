package com.example.cloudy.network

import com.example.cloudy.model.WeatherResponse

interface WeatherRemoteDataSource {

    suspend fun getWeather(latitude: Double, longitude: Double, apiKey: String,units:String): WeatherResponse?
}