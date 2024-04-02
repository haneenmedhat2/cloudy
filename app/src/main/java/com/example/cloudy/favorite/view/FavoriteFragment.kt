package com.example.cloudy.favorite.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
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
import com.example.cloudy.databinding.FragmentAlertBinding
import com.example.cloudy.databinding.FragmentFavoriteBinding
import com.example.cloudy.db.LocalDataSourceImp
import com.example.cloudy.favorite.viewmodel.CityViewModel
import com.example.cloudy.favorite.viewmodel.CityViewModelFactory
import com.example.cloudy.model.MapCity
import com.example.cloudy.model.WeatherRepositoryImp
import com.example.cloudy.network.WeatherRemoteDataSourceImp
import com.example.cloudy.settings.SettingsFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment(),CityAdapter.OnClickListener {

    private lateinit var binding: FragmentFavoriteBinding
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
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if(SettingsFragment.languageSP){
            activity?.title = "Favorites"
        }else{
            activity?.title = "صفحة المفضلة"

        }

    }
    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rv_city)
        cityAdapter = CityAdapter(this)
        cityLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerView.apply {
            adapter = cityAdapter
            layoutManager = cityLayoutManager
        }


        viewFactory = CityViewModelFactory(
            WeatherRepositoryImp.getInstance
                (WeatherRemoteDataSourceImp.getInstance(), LocalDataSourceImp(requireContext()))
        )


        viewModel = ViewModelProvider(this, viewFactory).get(CityViewModel::class.java)

        binding.ivNot.visibility=View.VISIBLE
        binding.tv.visibility=View.VISIBLE

        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected = networkInfo?.isConnectedOrConnecting == true

        binding.btnAdd.setOnClickListener {
            if(!isConnected){
                val snackbar =
                    Snackbar.make(binding.cl, "No Network Connection", Snackbar.LENGTH_SHORT)
                        .setAction("Dismiss") {
                        }.setActionTextColor(R.color.md_red_900)
                snackbar.show()
            }else{
                val intent = Intent(requireContext(), MapsActivity::class.java)
                startActivity(intent)
            }

        }

        lifecycleScope.launch {
            viewModel.cityList.collectLatest { city ->
                if (city!=null){
                    binding.ivNot.visibility=View.GONE
                    binding.tv.visibility=View.GONE
                }
                cityAdapter.submitList(city)
            }
        }
    }

    override fun onDeleteClick(city: MapCity) {
        lifecycleScope.launch {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure you want to delete this?")
                .setPositiveButton("Yes") { dialog, id ->
                    viewModel.deleteCity(city)
                    dialog.dismiss()
                    Toast.makeText(requireContext(),"City deleted Successfully", Toast.LENGTH_SHORT).show()

                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun onCardClick(city: MapCity) {
       val intent=Intent(requireContext(), CityWeatherActivity::class.java)
        intent.putExtra("latitude",city.lat)
        intent.putExtra("longitude",city.lon)
        startActivity(intent)
    }

}