package com.example.cloudy.model

import com.example.cloudy.db.FakeLocalDataSource
import com.example.cloudy.network.FakeRemoteDataSource
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Before
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not


class WeatherRepositoryImpTest{

    private var weather=WeatherResponse("",0,0, emptyList<WeatherItem>(),
        City(0,"", Coord(0.0,0.0),"",0,0,0,0))


    private var alert=AlertResponse(0.0,0.0,"",0,
        Current(0,0,0,0.0,0.0,0,0,
            0.0,0.0,0,0,0.0,0,0.0, emptyList()
        ),emptyList(), emptyList(), emptyList(), emptyList())

    var fakeCityList:MutableList<MapCity> = mutableListOf(MapCity("city",0.0,0.0))
     var fakeAlertList:MutableList<AlertData> = mutableListOf(AlertData("city",0.0,0.0,"","",0))

    lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    lateinit var fakeLocalDataSource: FakeLocalDataSource
    lateinit var repositoryImp: WeatherRepositoryImp

    @Before
    fun setup(){
        fakeRemoteDataSource= FakeRemoteDataSource(weather,alert)
        fakeLocalDataSource= FakeLocalDataSource(fakeCityList,fakeAlertList)
        repositoryImp = WeatherRepositoryImp.getInstance(fakeRemoteDataSource, fakeLocalDataSource)

    }

    //Remote Data Source
    @Test
    fun getWeatherRepo_longLatApi_weatherResponse()= runBlockingTest{
        //given
        var lon=0.0
        var lat=0.0
        var api="api"
        var units="metric"
        var language="language"

        //when
       var result= repositoryImp.getWeatherRepo(lon,lat,api,language,units).first()

        //then
     assertThat(result,`is` (weather))
}

    @Test
    fun getWeatherAlert_longLatApi_weatherAlert()= runBlockingTest{
        //Given
        var lon=0.0
        var lat=0.0
        var api="api"

        //When
        var result= repositoryImp.getWeatherAlert(lon,lat,api).first()

        //Then
        assertThat(result,`is` (alert))
    }


    //Local Data Source
    @Test
    fun insertAlertData_alert()= runBlockingTest{
        //Given
        val alertData=AlertData("city",0.0,0.0,"date","time",0)

        //When
         repositoryImp.insertAlertData(alertData)

        //Then
        assertThat(fakeAlertList,hasItem(alertData))
    }

    @Test
    fun getAlertData_alertDataList()= runBlockingTest{
        //Given

        //When
       val result= repositoryImp.getAlertData().first()

        //Then
        assertThat(fakeAlertList,equalTo(result))
    }

    @Test
    fun deleteAlertData_alertData()= runBlockingTest{
        //Given
        val alertData=AlertData("city",0.0,0.0,"date","time",0)
        //When
         repositoryImp.deleteAlertData(alertData)

        //Then
        assertThat(fakeAlertList, not(hasItem(repositoryImp)))
    }

    @Test
    fun insertCity_city()= runBlockingTest{
        //Given
        val city= MapCity ("city",0.0,0.0)

        //When
        repositoryImp.insertCity(city)

        //Then
        assertThat(fakeCityList,hasItem(city))
    }

    @Test
    fun getAllCities_cityList()= runBlockingTest{
        //Given

        //When
        val result= repositoryImp.getAllCities().first()

        //Then
        assertThat(fakeCityList,equalTo(result))
    }

    @Test
    fun deleteCity_city()= runBlockingTest{
        //Given
        val city= MapCity ("city",0.0,0.0)
        //When
        repositoryImp.deleteCity(city)

        //Then
        assertThat(fakeCityList, not(hasItem(repositoryImp)))
    }



}