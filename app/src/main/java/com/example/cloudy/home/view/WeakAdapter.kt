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
import com.example.cloudy.R
import com.example.cloudy.model.WeatherItem
import com.example.cloudy.settings.SettingsFragment
import java.text.SimpleDateFormat
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
          if(!SettingsFragment.languageSP){
              if (result=="Sunday")
                  holder.day.text="الأحد"
              if (result=="Monday")
                  holder.day.text="الاثنين"
              if (result=="Tuesday")
                  holder.day.text="الثلاثاء"
              if (result=="Wednesday")
                  holder.day.text="الأربعاء"
              if (result=="Thursday")
                  holder.day.text="الخميس"
              if (result=="Friday")
                  holder.day.text="الجمعة"
              if (result=="Saturday")
                  holder.day.text="السبت"
          }else{
              holder.day.text=result
          }
        holder.degree.text= "${weather.main.temp} ${HomeFragment.unitStr}"

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
