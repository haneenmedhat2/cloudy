package com.example.cloudy.favorite.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudy.R
import com.example.cloudy.city.view.CityWeatherActivity
import com.example.cloudy.db.CityLocalDataSourceImp
import com.example.cloudy.favorite.viewmode.CityViewModel
import com.example.cloudy.favorite.viewmode.CityViewModelFactory
import com.example.cloudy.model.MapCity
import com.example.cloudy.model.WeatherRepositoryImp
import com.example.cloudy.network.WeatherRemoteDataSourceImp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment(),CityAdapter.OnClickListener {

    lateinit var add:ImageView
    lateinit var text:TextView
    private lateinit var viewFactory: CityViewModelFactory
    private lateinit var viewModel: CityViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var cityAdapter: CityAdapter
    private lateinit var cityLayoutManager: LinearLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add = view.findViewById(R.id.btnAdd)
        text = view.findViewById(R.id.tv)
        recyclerView = view.findViewById(R.id.rv_city)
        cityAdapter = CityAdapter(this)
        cityLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerView.apply {
            adapter = cityAdapter
            layoutManager = cityLayoutManager
        }


        viewFactory = CityViewModelFactory(
            WeatherRepositoryImp.getInstance
                (WeatherRemoteDataSourceImp.getInstance(), CityLocalDataSourceImp(requireContext()))
        )


        viewModel = ViewModelProvider(this, viewFactory).get(CityViewModel::class.java)
        add.setOnClickListener {
            val intent = Intent(requireContext(), MapsActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch {
            viewModel.cityList.collectLatest { city ->
                if (city!=null){
                    text.visibility=View.GONE
                }
                cityAdapter.submitList(city)
            }
        }
    }

    override fun onDeleteClick(city: MapCity) {
        lifecycleScope.launch {
            viewModel.deleteCity(city)
            Toast.makeText(requireContext(),"City deleted Successfully", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCardClick(city: MapCity) {
       val intent=Intent(requireContext(), CityWeatherActivity::class.java)
        startActivity(intent)
    }

}