package com.example.cloudy.alert.view

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
import com.example.cloudy.R
import com.example.cloudy.alert.viewmodel.AlertViewModel
import com.example.cloudy.model.AlertData
import com.example.cloudy.model.MapCity

class AlertAdapter(var listener: OnClickListener) : ListAdapter <AlertData, AlertViewHolder>
    (AlertDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val inflater: LayoutInflater =parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view= inflater.inflate(R.layout.alert_card,parent,false)
        return  AlertViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {

        val alert = getItem(position)

        holder.city.text=alert.cityName
        holder.time.text=alert.time
        holder.date.text=alert.date
        holder.image.setOnClickListener {
            listener.onDeleteClick(alert)
        }



    }
    interface OnClickListener {
        fun onDeleteClick(alert: AlertData)
    }


}
class AlertViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val time: TextView = view.findViewById(R.id.tv_time)
    val date: TextView = view.findViewById(R.id.tv_date)
    val city: TextView = view.findViewById(R.id.tv_city)
    val image: ImageView =view.findViewById(R.id.ivPhoto)
    val card: CardView =view.findViewById(R.id.favcard)
}



class AlertDiffCallback : DiffUtil.ItemCallback<AlertData>() {
    override fun areItemsTheSame(oldItem: AlertData, newItem: AlertData): Boolean {
        return oldItem.cityName == newItem.cityName
    }

    override fun areContentsTheSame(oldItem: AlertData, newItem: AlertData): Boolean {
        return oldItem == newItem
    }
}
