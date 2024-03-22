package com.example.cloudy.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey


data class WeatherResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherItem>,
    val city: City

)


data class WeatherItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val rain: Rain?,
    val sys: Sys?,
    val dt_txt: String
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Double
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Clouds(
    val all: Int
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

data class Rain(
    val `3h`: Double
)

data class Sys(
    val pod: String
)

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class Coord(
    val lat: Double,
    val lon: Double
)
@Entity(tableName = "city")
data class MapCity(
    @NonNull
    @PrimaryKey
    val cityName:String,
    @NonNull
    val lat: Double,
    @NonNull
    val lon: Double
)

@Entity(tableName = "alert")
data class Alert(
    @NonNull
    @PrimaryKey
    val cityName:String,
    @NonNull
    val lat: Double,
    @NonNull
    val lon: Double,

)

@Entity(tableName = "alert_data")
data class AlertData(
    @NonNull
    @PrimaryKey
    val cityName:String,
    @NonNull
    val lat: Double,
    @NonNull
    val lon: Double,
      @NonNull
       val date: String,
       @NonNull
       val time: String,
       @NonNull
       val alertType:Int
)
