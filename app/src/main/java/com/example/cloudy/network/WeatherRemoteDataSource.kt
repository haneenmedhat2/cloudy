package com.example.cloudy.network

import com.example.cloudy.model.AlertResponse
import com.example.cloudy.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {

     fun getWeather(latitude: Double, longitude: Double, apiKey: String,language:String,units:String): Flow<WeatherResponse?>
     fun getWeatherAlert(latitude: Double, longitude: Double, apiKey: String): Flow<AlertResponse?>
}