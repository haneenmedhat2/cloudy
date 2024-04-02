package com.example.cloudy.model

import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    //Remote//
     fun getWeatherRepo(latitude: Double, longitude: Double, apiKey: String,language:String,units:String): Flow<WeatherResponse?>
    fun getWeatherAlert(latitude: Double, longitude: Double, apiKey: String): Flow<AlertResponse?>


    //Local

     //city
     suspend fun insertCity(city: MapCity)
    suspend fun deleteCity(city: MapCity)
    fun getAllCities(): Flow<List<MapCity>>

    //Alert Data
    suspend fun insertAlertData(alert: AlertData)
    fun getAlertData():Flow<List<AlertData>>
    suspend fun deleteAlertData(alert: AlertData)
}