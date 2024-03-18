package com.example.cloudy.db

import com.example.cloudy.model.MapCity
import kotlinx.coroutines.flow.Flow

interface CityLocalDataSource {
    suspend fun insertCity(city: MapCity)

   suspend fun deleteCity(city: MapCity)
     fun getAllCities(): Flow<List<MapCity>>
}