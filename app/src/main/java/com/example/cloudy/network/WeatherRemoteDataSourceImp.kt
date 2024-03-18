package com.example.cloudy.network

import android.util.Log
import com.example.cloudy.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.Response

private const val TAG = "ProductRemoteDataSource"

class WeatherRemoteDataSourceImp private constructor():WeatherRemoteDataSource{

    private val serviceObj:ApiService by lazy {
        RetrofitHelper.retrofitInstance.create(ApiService::class.java)
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
        apiKey: String,units:String
    ): Flow<WeatherResponse?> {
        return flow {
            val weather =   serviceObj.getWeather(latitude, longitude, apiKey, units)
            emit(weather)
        }

    }


}

