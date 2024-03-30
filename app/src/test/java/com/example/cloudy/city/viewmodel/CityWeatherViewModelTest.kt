package com.example.cloudy.city.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cloudy.MainCoroutineRule
import com.example.cloudy.model.City
import com.example.cloudy.model.Coord
import com.example.cloudy.model.FakeRepository
import com.example.cloudy.model.WeatherItem
import com.example.cloudy.getOrAwaitValue
import com.example.cloudy.model.WeatherResponse
import com.example.cloudy.utility.ApiState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith (AndroidJUnit4::class)
    class CityWeatherViewModelTest {
        private lateinit var viewModel: CityWeatherViewModel
        private lateinit var fakeRepository: FakeRepository

        @get:Rule
        val coroutineRule = MainCoroutineRule()

        @Before
        fun setUp() {
            fakeRepository = FakeRepository()
            viewModel = CityWeatherViewModel(fakeRepository)
        }


    @Test
    fun `getWeather success`() = coroutineRule.runBlockingTest {
        // Given
        val repository = FakeRepository()
        val viewModel = CityWeatherViewModel(repository)
        val latitude = 0.0
        val longitude = 0.0
        val apiKey = "fake_api_key"
        val metric = "metric"
        val expectedData = WeatherResponse(
            "", 0, 0, emptyList<WeatherItem>(),
            City(0, "", Coord(0.0, 0.0), "", 0, 0, 0, 0)
        )

        // When
        viewModel.getWeather(latitude, longitude, apiKey, metric)

        // Then
        viewModel.weatherList.collect { state ->
            when (state) {
                is ApiState.Success -> {
                    assertEquals(expectedData, state.data)
                }
                else -> Unit // Ignore other states
            }
        }

    }
}

