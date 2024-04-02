package com.example.cloudy.network

import com.example.cloudy.model.AlertResponse
import com.example.cloudy.model.City
import com.example.cloudy.model.Coord
import com.example.cloudy.model.Current
import com.example.cloudy.model.Hourly
import com.example.cloudy.model.Minutely
import com.example.cloudy.model.WeatherItem
import com.example.cloudy.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRemoteDataSource(private var weather:WeatherResponse,private var alert:AlertResponse) :WeatherRemoteDataSource{

    override fun getWeather(
        latitude: Double,
        longitude: Double,
        apiKey: String,
        language: String,
        units: String
    ): Flow<WeatherResponse?> {
        val fakeWeatherResponse = weather
        return flowOf(fakeWeatherResponse)
    }

    override fun getWeatherAlert(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): Flow<AlertResponse?> {
        val fakeAlertResponse=alert
        return flowOf(fakeAlertResponse)
    }
}