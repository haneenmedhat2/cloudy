package com.example.cloudy.db

import android.content.Context
import com.example.cloudy.model.MapCity
import kotlinx.coroutines.flow.Flow

class CityLocalDataSourceImp(context: Context) :CityLocalDataSource{


   private val dao:CityDao by lazy {
      val db:CityDatabase=  CityDatabase.getInstance(context)
       db.getAllCities()
    }

    override suspend fun insertCity(city: MapCity){
        dao.addCity(city)
    }

    override suspend fun deleteCity(city: MapCity) {
        dao.deleteCity(city)
    }

    override fun getAllCities(): Flow<List<MapCity>> {
       return dao.getAllCities()
    }

}