package com.example.cloudy.network

import android.util.Log
import com.example.cloudy.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
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

    override suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        apiKey: String,units:String
    ): WeatherResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<WeatherResponse> = serviceObj.getWeather(latitude, longitude, apiKey,units)
                if (response.isSuccessful) {
                    Log.i(TAG, "Successfully network call ${response.body()?.city?.name}")
                    response.body()

                } else {
                    Log.i(TAG, "Error in fetching data")
                    null
                }
            } catch (e: Exception) {
                Log.i(TAG, "getAllWeather details: ${e.message}")
                null
            }
        }

    }

}

