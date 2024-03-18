package com.example.cloudy.model

import com.example.cloudy.db.CityLocalDataSource
import com.example.cloudy.network.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow


class WeatherRepositoryImp(val remoteDataSource: WeatherRemoteDataSource,val localDataSource: CityLocalDataSource):WeatherRepository {

    companion object{
        private var repo:WeatherRepositoryImp?=null
        fun getInstance(remoteDataSource: WeatherRemoteDataSource, localDataSource: CityLocalDataSource):WeatherRepositoryImp{
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

    override suspend fun insertCity(city: MapCity) {
        localDataSource.insertCity(city)
    }

    override suspend fun deleteCity(city: MapCity) {
        localDataSource.deleteCity(city)
    }

    override fun getAllCities(): Flow<List<MapCity>> {
        return localDataSource.getAllCities()
    }

}