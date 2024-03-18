package com.example.cloudy.network

import com.example.cloudy.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {

     fun getWeather(latitude: Double, longitude: Double, apiKey: String,units:String): Flow<WeatherResponse?>
}