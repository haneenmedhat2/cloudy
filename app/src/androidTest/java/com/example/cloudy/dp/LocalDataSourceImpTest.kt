package com.example.cloudy.dp

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import com.example.cloudy.db.CityDao
import com.example.cloudy.db.CityDatabase
import com.example.cloudy.db.LocalDataSourceImp
import com.example.cloudy.getOrAwaitValue
import com.example.cloudy.model.MapCity
import com.google.common.truth.Truth.assertThat
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.internal.matchers.Equals
import retrofit2.http.GET

@MediumTest
class LocalDataSourceImpTest {

    lateinit var localDataSource: LocalDataSourceImp

    @get:Rule
    val rule= InstantTaskExecutorRule()
    @Before
    fun setup(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        localDataSource= LocalDataSourceImp(context)


    }

    @Test
    fun insertCity_addOneCity() = runBlockingTest {
        //Given
        val city= MapCity("city",1.0,2.0)

        //When
        localDataSource.insertCity(city)

        //That
        val cities = localDataSource.getAllCities().getOrAwaitValue {  }
        assertThat(cities).contains(city)

        // Clean up
        localDataSource.deleteCity(city)
    }

    @Test
    fun testDeleteCity_removeOneCity() = runBlockingTest {

        //Given
        val city= MapCity("city",1.0,2.0)

        // When
        localDataSource.deleteCity(city)
        val cities = localDataSource.getAllCities().first()

        //Then
        assertThat(cities).doesNotContain(city)
    }


    @Test
    fun getAllCities_addOneCity_retrieveList() = runBlockingTest {
        //Given
        val city= MapCity("city",1.0,2.0)
        localDataSource.insertCity(city)

        //When
        val cities = localDataSource.getAllCities().getOrAwaitValue {  }

        //That
        assertThat(cities).contains(city)
        assertNotNull(cities)


        // Clean up
        localDataSource.deleteCity(city)
    }


}