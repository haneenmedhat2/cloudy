package com.example.cloudy.network

import com.example.cloudy.model.AlertResponse
import com.example.cloudy.model.WeatherResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
interface ApiService {
    @GET("forecast")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("lang") language:String,
        @Query("units") units:String
    ): WeatherResponse

    @GET("onecall")
    suspend fun getWeatherAlert(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): AlertResponse
}



object RetrofitHelper {
    // Base URL for the first API
    private const val BASE_URL_1 = "https://api.openweathermap.org/data/2.5/"

    private const val BASE_URL_2 = "https://api.openweathermap.org/data/3.0/"

    val retrofitInstance1 = Retrofit.Builder()
        .baseUrl(BASE_URL_1)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val retrofitInstance2 = Retrofit.Builder()
        .baseUrl(BASE_URL_2)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}