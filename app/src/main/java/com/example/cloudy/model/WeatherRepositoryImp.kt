package com.example.cloudy.model

import com.example.cloudy.db.LocalDataSource
import com.example.cloudy.network.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow


class WeatherRepositoryImp(val remoteDataSource: WeatherRemoteDataSource,val localDataSource: LocalDataSource):WeatherRepository {

    companion object{
        private var repo:WeatherRepositoryImp?=null
        fun getInstance(remoteDataSource: WeatherRemoteDataSource, localDataSource: LocalDataSource):WeatherRepositoryImp{
            return repo?: synchronized(this){
                val temp=WeatherRepositoryImp(remoteDataSource,localDataSource)
                repo=temp
                temp
            }
        }
    }

    //Network//
    override  fun getWeatherRepo(
        latitude: Double,
        longitude: Double,
        apiKey: String,units:String
    ): Flow<WeatherResponse?> {
        return remoteDataSource.getWeather(latitude,longitude,apiKey,units)
    }

   //Alert
     override suspend fun insertAlert(alert: Alert) {
        localDataSource.insertAlert(alert)
    }

    override fun getAllAlerts(): Flow<List<Alert>> {
        return localDataSource.getAllAlerts()
    }


    //Alert Data
    override suspend fun insertAlertData(alert: AlertData) {
        localDataSource.insertAlertData(alert)
    }

    //City
    override suspend fun deleteCity(city: MapCity) {
        localDataSource.deleteCity(city)
    }

    override fun getAllCities(): Flow<List<MapCity>> {
        return localDataSource.getAllCities()
    }
    override suspend fun insertCity(city: MapCity) {
        localDataSource.insertCity(city)
    }
}