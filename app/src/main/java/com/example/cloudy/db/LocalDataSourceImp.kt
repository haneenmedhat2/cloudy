package com.example.cloudy.db

import android.content.Context
import com.example.cloudy.model.Alert
import com.example.cloudy.model.AlertData
import com.example.cloudy.model.MapCity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class LocalDataSourceImp(context: Context) :LocalDataSource{

    private val database by lazy {
        CityDatabase.getInstance(context)
    }

    private val dao: CityDao by lazy {
        database.getAllCities()
    }

    private val alertDataDao: AlertDataDao by lazy {
        database.getAlertData()
    }


    //City
    override suspend fun insertCity(city: MapCity){
        dao.addCity(city)
    }

    override suspend fun deleteCity(city: MapCity) {
        dao.deleteCity(city)
    }

    override fun getAllCities(): Flow<List<MapCity>> {
       return dao.getAllCities()
    }

    //Alert Data
    override suspend fun insertAlertData(alert: AlertData) {
       alertDataDao.addAlertData(alert)
    }

    override fun getAlertData(): Flow<List<AlertData>> {
        return alertDataDao.getAlertData()
    }

    override suspend fun deleteAlertData(alert: AlertData) {
        alertDataDao.deleteAlertData(alert)
    }




}