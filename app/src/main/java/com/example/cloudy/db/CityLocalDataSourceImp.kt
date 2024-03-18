package com.example.cloudy.db

import android.content.Context
import com.example.cloudy.model.MapCity

class CityLocalDataSourceImp(context: Context) :CityLocalDataSource{


   private val dao:CityDao by lazy {
      val db:CityDatabase=  CityDatabase.getInstance(context)
       db.getAllCities()
    }

    override suspend fun insertCity(city: MapCity){
        dao.addCity(city)
    }

   /* override suspend fun deleteProduct(product: Product) {
        dao.deleteProduct(product)
    }*/

   /* override  fun getAllProducts(): Flow<List<Product>> {
      return dao.getAllProducts()
    }*/

}