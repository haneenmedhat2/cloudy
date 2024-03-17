package com.example.cloudy.home.view

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.cloudy.R
import com.example.cloudy.model.WeatherItem
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DayWeatherAdapter() : ListAdapter<WeatherItem, WeatherViewHolder>
    (WeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val inflater:LayoutInflater=parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view= inflater.inflate(R.layout.day_wather_card,parent,false)
        return  WeatherViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {

        val weather = getItem(position)
        val parts = weather.dt_txt.split(" ")
        val timeString = parts[1].substring(0, 5)
        val time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
        val amOrPm = if (time.hour < 12) "AM" else "PM"
        holder.time.text="$timeString$amOrPm"
        holder.degree.text= "${weather.main.temp}°C"

        var icon=weather.weather[0].icon

        if (icon=="01d"|| icon=="01n" ){
            holder.image.setImageResource(R.drawable.sunny)
        }
        if (icon=="02d"|| icon=="02n"|| icon=="03d"|| icon=="03n"|| icon=="04d"|| icon=="04n" ){
            holder.image.setImageResource(R.drawable.cloud)
        }
        if (icon=="09d"|| icon=="09n"|| icon=="10d"|| icon=="10n" ){
            holder.image.setImageResource(R.drawable.rain)
        }
        if (icon=="11d"|| icon=="11n" ){
            holder.image.setImageResource(R.drawable.thunder)
        }
        if (icon=="13d"|| icon=="13n" ){
            holder.image.setImageResource(R.drawable.snow)
        }
        if (icon=="50d"|| icon=="50n" ){
            holder.image.setImageResource(R.drawable.mist)
        }

    }


}
class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
   val degree:TextView = view.findViewById(R.id.tv_degree)
    val time:TextView = view.findViewById(R.id.tv_Time)
    val image: ImageView =view.findViewById(R.id.ivPhoto)
}



class WeatherDiffCallback : DiffUtil.ItemCallback<WeatherItem>() {

    override fun areItemsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
        return oldItem == newItem
    }
}
