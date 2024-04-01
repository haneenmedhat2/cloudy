package com.example.cloudy.favorite.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cloudy.MainDispatcherRule
import com.example.cloudy.city.viewmodel.CityWeatherViewModel
import com.example.cloudy.model.FakeRepository
import com.example.cloudy.model.MapCity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)

class CityViewModelTest {
    private lateinit var viewModel: CityViewModel
    private lateinit var fakeRepository: FakeRepository

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    @Before
    fun setUp() {
        fakeRepository = FakeRepository()
        viewModel = CityViewModel(fakeRepository)
    }

    @Test
    fun insertCity_cityData(){
        //Given
        val city= MapCity("city",0.0,0.0)

        //When
        viewModel.insertCity(city)

        //Then
        assertEquals(city,fakeRepository.cityList.first())
    }


    @Test
    fun deleteCity_cityData(){
        //Given
        val city= MapCity("city",0.0,0.0)

        //When
        viewModel.deleteCity(city)

        //Then
        assertFalse(fakeRepository.cityList.contains(city))
    }

    @Test
    fun getAllCity_listOfCities(){
        //Given

        //When
       viewModel.getAllCity()

        //Then
        assertEquals(fakeRepository.cityList,viewModel.cityList.value)
    }
}