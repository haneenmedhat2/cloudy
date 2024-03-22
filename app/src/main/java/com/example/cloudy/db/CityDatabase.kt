package com.example.cloudy.db

import android.content.Context
import android.location.Address
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cloudy.model.Alert
import com.example.cloudy.model.AlertData
import com.example.cloudy.model.MapCity

@Database (entities = arrayOf( MapCity::class,Alert::class,AlertData::class), version = 3)
abstract class CityDatabase:RoomDatabase() {
    abstract fun getAllCities(): CityDao
    abstract fun getAllAlerts(): AlertDao
    abstract fun getAlertData():AlertDataDao
    companion object{
        @Volatile
        private var INSTANCE: CityDatabase?=null

        fun getInstance(context: Context): CityDatabase {
            return INSTANCE ?: synchronized(this){
                val instance= Room.databaseBuilder(
                    context.applicationContext, CityDatabase::class.java,"city_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE =instance
                instance
            }
        }
    }

}