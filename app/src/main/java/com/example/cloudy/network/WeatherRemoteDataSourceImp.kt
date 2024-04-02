package com.example.cloudy.network

import android.util.Log
import com.example.cloudy.model.AlertResponse
import com.example.cloudy.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.Response

private const val TAG = "ProductRemoteDataSource"

class WeatherRemoteDataSourceImp private constructor():WeatherRemoteDataSource{

    private val serviceObj1:ApiService by lazy {
        RetrofitHelper.retrofitInstance1.create(ApiService::class.java)
    }

    private val serviceObj2:ApiService by lazy {
        RetrofitHelper.retrofitInstance2.create(ApiService::class.java)
    }

    companion object{
        private var remote: WeatherRemoteDataSourceImp?=null
        fun getInstance(): WeatherRemoteDataSourceImp {
            return remote?: synchronized(this){
                val temp= WeatherRemoteDataSourceImp()
                remote=temp
                temp
            }
        }
    }

    override  fun getWeather(
        latitude: Double,
        longitude: Double,
        apiKey: String,
        language:String,
        units:String
    ): Flow<WeatherResponse?> {
        return flow {
            val weather =   serviceObj1.getWeather(latitude, longitude, apiKey, language,units)
            emit(weather)
        }

    }

    override fun getWeatherAlert(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): Flow<AlertResponse?> {
        return flow {
            val weatherAlert =   serviceObj2.getWeatherAlert(latitude, longitude, apiKey)
            emit(weatherAlert)
        }
    }


}

