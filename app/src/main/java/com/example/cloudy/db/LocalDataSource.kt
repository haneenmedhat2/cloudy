package com.example.cloudy.db

import com.example.cloudy.model.Alert
import com.example.cloudy.model.AlertData
import com.example.cloudy.model.MapCity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun insertCity(city: MapCity)
   suspend fun deleteCity(city: MapCity)
     fun getAllCities(): Flow<List<MapCity>>

    //Alert
    suspend fun insertAlert(alert:Alert)
    fun getAllAlerts():Flow<List<Alert>>

    // Alert Data
    suspend fun insertAlertData(alert:AlertData)
}