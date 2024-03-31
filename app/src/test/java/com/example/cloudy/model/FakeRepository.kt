package com.example.cloudy.model

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository:IWeatherRepositoryImp {
    private var weatherResponse: WeatherResponse? = null
    private var error: Throwable? = null
    private val fakeAlertResponse: AlertResponse? = null
    private val alertList: MutableList<AlertData> = mutableListOf()
     val cityList: MutableList<MapCity> = mutableListOf()


    override fun getWeatherRepo(
        latitude: Double,
        longitude: Double,
        apiKey: String,
        language: String,
        units: String
    ): Flow<WeatherResponse?> {
        return if (error != null) {
            flow { throw error as Throwable }
        } else {
            flowOf(weatherResponse ?: WeatherResponse(
                "", 0, 0, emptyList<WeatherItem>(),
                City(0, "", Coord(0.0, 0.0), "", 0, 0, 0, 0)))
        }
    }

    override fun getWeatherAlert(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): Flow<AlertResponse?> {
        return flowOf(fakeAlertResponse)
    }

    override suspend fun insertAlertData(alert: AlertData) {
        alertList.add(alert)
    }

    override fun getAlertData(): Flow<List<AlertData>> {
        return flow {
            emit(alertList)
        }
    }

    override suspend fun deleteAlertData(alert: AlertData) {
        alertList.remove(alert)
    }

    override suspend fun deleteCity(city: MapCity) {
        cityList.remove(city)
    }

    override fun getAllCities(): Flow<List<MapCity>> {
        return flow {
            emit(cityList)
        }
    }

    override suspend fun insertCity(city: MapCity) {
        cityList.add(city)
    }
}