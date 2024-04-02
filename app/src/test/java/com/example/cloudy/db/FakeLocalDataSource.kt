package com.example.cloudy.db

import com.example.cloudy.model.AlertData
import com.example.cloudy.model.MapCity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSource(private var fakeCityList: MutableList<MapCity>,
private var fakeAlertList:MutableList<AlertData>):LocalDataSource {

    override suspend fun insertCity(city: MapCity) {
        fakeCityList.add(city)
    }

    override suspend fun deleteCity(city: MapCity) {
        fakeCityList.remove(city)
    }

    override fun getAllCities(): Flow<List<MapCity>> {
        return flowOf(fakeCityList)
    }

    override suspend fun insertAlertData(alert: AlertData) {
        fakeAlertList.add(alert)
    }

    override fun getAlertData(): Flow<List<AlertData>> {
        return flowOf(fakeAlertList)
    }

    override suspend fun deleteAlertData(alert: AlertData) {
       fakeAlertList.remove(alert)
    }
}