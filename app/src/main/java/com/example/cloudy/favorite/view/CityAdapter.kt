package com.example.cloudy.favorite.view

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.cloudy.R
import com.example.cloudy.model.MapCity
import com.example.cloudy.model.WeatherItem
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CityAdapter(var listener: OnClickListener) : ListAdapter<MapCity, CityViewHolder>
    (CityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val inflater:LayoutInflater=parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view= inflater.inflate(R.layout.fav_card,parent,false)
        return  CityViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {

        val city = getItem(position)

        holder.cityName.text=city.cityName
        holder.image.setOnClickListener {
          listener.onDeleteClick(city)
        }
        holder.card.setOnClickListener {
         listener.onCardClick(city)
        }


    }
    interface OnClickListener {
        fun onDeleteClick(city: MapCity)
        fun onCardClick(city: MapCity)
    }


}
class CityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
   val cityName:TextView = view.findViewById(R.id.tv_city)
    val image: ImageView =view.findViewById(R.id.ivPhoto)
    val card: CardView =view.findViewById(R.id.favcard)
}



class CityDiffCallback : DiffUtil.ItemCallback<MapCity>() {
    override fun areItemsTheSame(oldItem: MapCity, newItem: MapCity): Boolean {
        return oldItem.cityName == newItem.cityName
    }

    override fun areContentsTheSame(oldItem: MapCity, newItem: MapCity): Boolean {
        return oldItem == newItem
    }
}
