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
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WeakAdapter() : ListAdapter<WeatherItem,WeakViewHolder >
    (WeakDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeakViewHolder {
        val inflater:LayoutInflater=parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view= inflater.inflate(R.layout.weak_card,parent,false)
        return  WeakViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: WeakViewHolder, position: Int) {

        val weather = getItem(position)

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = simpleDateFormat.parse(weather.dt_txt.split(" ")[0])
        val calendar = Calendar.getInstance()
        calendar.time = date ?: Date()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
         val result= when (dayOfWeek) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> "Unknown"
        }

        holder.day.text=result
        holder.degree.text= "${weather.main.temp} °C"

        val iconUrl = "https://openweathermap.org/img/wn/${weather.weather[0].icon}.png"
        Glide.with(holder.itemView.context)
            .load(iconUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.image)

    }


}
class WeakViewHolder(view: View) : RecyclerView.ViewHolder(view) {
   val day:TextView = view.findViewById(R.id.tv_day)
    val degree:TextView = view.findViewById(R.id.tv_degree)
    val image: ImageView =view.findViewById(R.id.ivPhoto)
}



class WeakDiffCallback : DiffUtil.ItemCallback<WeatherItem>() {

    override fun areItemsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: WeatherItem, newItem: WeatherItem): Boolean {
        return oldItem == newItem
    }
}
