package com.example.cloudy.model

interface WeatherRepository {

    //Remote//
    suspend fun getWeatherRepo(latitude: Double, longitude: Double, apiKey: String,units:String):WeatherResponse?

}