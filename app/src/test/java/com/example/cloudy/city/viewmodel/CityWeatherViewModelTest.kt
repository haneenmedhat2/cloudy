package com.example.cloudy.city.viewmodel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cloudy.MainDispatcherRule
import com.example.cloudy.model.City
import com.example.cloudy.model.Coord
import com.example.cloudy.model.FakeRepository
import com.example.cloudy.model.WeatherItem
import com.example.cloudy.model.WeatherResponse
import com.example.cloudy.utility.ApiState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith (AndroidJUnit4::class)
    class CityWeatherViewModelTest {
        private lateinit var viewModel: CityWeatherViewModel
        private lateinit var fakeRepository: FakeRepository

        @get:Rule
        val coroutineRule = MainDispatcherRule()

        @Before
        fun setUp() {
            fakeRepository = FakeRepository()
            viewModel = CityWeatherViewModel(fakeRepository)
        }

    @Test
    fun getWeather_LatitudeLongitudeApikeyLanguageUnits_success() = runBlockingTest {
        // Given
        val latitude = 0.0
        val longitude = 0.0
        val apiKey = "fake_api_key"
        val metric = "metric"
        var language="language"

        val weatherResponse = WeatherResponse("", 0, 0, emptyList<WeatherItem>(),
            City(0, "", Coord(0.0, 0.0), "", 0, 0, 0, 0))

        // When
        viewModel.getWeather(latitude, longitude, apiKey, language,metric)

        // Then
        delay(500)
        assertTrue(viewModel.weatherList.value is ApiState.Success)
        assertEquals(weatherResponse, (viewModel.weatherList.value as ApiState.Success<List<WeatherResponse?>>).data)
    }

/*    @Test
    fun getWeather_LatitudeLongitudeApikeyLanguageUnits_failure() = runBlockingTest {
        // Given
        val latitude = 0.0
        val longitude = 0.0
        val apiKey = "fake_api_key"
        val metric = "metric"
        val language = "language"
        val error = Throwable("Fake error message")
        val expectedState = ApiState.Failure(error)

        // When
        viewModel.getWeather(latitude, longitude, apiKey, language, metric)
        // Simulate repository failure
        viewModel.weatherList.value = ApiState.Failure(error)

        delay(100)

        // Then
        assertEquals(expectedState, viewModel.weatherList.value)
    }*/

}

