package com.example.cloudy.dp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Query
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.cloudy.db.CityDao
import com.example.cloudy.db.CityDatabase
import com.example.cloudy.getOrAwaitValue
import com.example.cloudy.model.City
import com.example.cloudy.model.MapCity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class CityDaoTest {

    lateinit var database: CityDatabase
    lateinit var dao: CityDao

    @get:Rule
    val rule= InstantTaskExecutorRule()

    @Before
    fun setUp(){
        database= Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CityDatabase::class.java
        ).build()
        dao= database.getAllCities()
    }
    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun  getAllCities_retrieveCities()= runBlockingTest{
        //given
        val city1= MapCity("city1",1.0,2.0)
        val city2= MapCity("city2",3.0,4.0)

        dao.addCity(city1)
        dao.addCity(city2)

        //when
        val result= dao.getAllCities().getOrAwaitValue()

        //Then
        assertThat(result, not(nullValue()))
        assertThat(result.size, equalTo(2))
        assertThat(result, hasItems(city1, city2))

    }

    @Test
    fun  addCity_addToCities_theSameCities()= runBlockingTest{
        //given
        val city1= MapCity("city1",1.0,2.0)
        val city2= MapCity("city2",3.0,4.0)

        //when
       dao.addCity(city1)
       dao.addCity(city2)

        val result= dao.getAllCities().getOrAwaitValue()

        //Then
        assertThat(result, not(nullValue()))
        assertThat(result, hasItems(city1, city2))

    }



    @Test
    fun  deleteCity_removeTwoCities()= runBlockingTest{
        //given
        val city1= MapCity("city1",1.0,2.0)
        val city2= MapCity("city2",3.0,4.0)

        dao.addCity(city1)
        dao.addCity(city2)
        val assertResult= dao.getAllCities().getOrAwaitValue()
        assertThat(assertResult.size,`is`(2))

        //when
        dao.deleteCity(city1)
        dao.deleteCity(city2)

        val removeResult= dao.getAllCities().getOrAwaitValue()

        //Then
        assertThat(removeResult, not(hasItems(city1, city2)))
        assertThat(removeResult.size,`is`(0))
    }



}