package com.example.cloudy.model

import com.example.cloudy.network.WeatherRemoteDataSource


class WeatherRepositoryImp(val remoteDataSource: WeatherRemoteDataSource):WeatherRepository {

    companion object{
        private var repo:WeatherRepositoryImp?=null
        fun getInstance(remoteDataSource: WeatherRemoteDataSource):WeatherRepositoryImp{
            return repo?: synchronized(this){
                val temp=WeatherRepositoryImp(remoteDataSource)
                repo=temp
                temp
            }
        }
    }

    //Network//
    override suspend fun getWeatherRepo(
        latitude: Double,
        longitude: Double,
        apiKey: String,units:String
    ): WeatherResponse?{
        return remoteDataSource.getWeather(latitude,longitude,apiKey,units)
    }

}