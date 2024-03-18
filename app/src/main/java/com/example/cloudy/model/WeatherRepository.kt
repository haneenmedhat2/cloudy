package com.example.cloudy.model

import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    //Remote//
     fun getWeatherRepo(latitude: Double, longitude: Double, apiKey: String,units:String): Flow<WeatherResponse?>

    //Local
     suspend fun insertCity(city: MapCity)
    suspend fun deleteCity(city: MapCity)
    fun getAllCities(): Flow<List<MapCity>>

}