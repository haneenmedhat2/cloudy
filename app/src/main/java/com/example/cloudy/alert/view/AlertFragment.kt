package com.example.cloudy.alert.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudy.R
import com.example.cloudy.alert.viewmodel.AlertViewModel
import com.example.cloudy.alert.viewmodel.AlertViewModelFactory
import com.example.cloudy.databinding.FragmentAlertBinding
import com.example.cloudy.db.LocalDataSourceImp
import com.example.cloudy.favorite.viewmodel.CityViewModel
import com.example.cloudy.favorite.viewmodel.CityViewModelFactory
import com.example.cloudy.model.Alert
import com.example.cloudy.model.AlertData
import com.example.cloudy.model.WeatherRepositoryImp
import com.example.cloudy.network.WeatherRemoteDataSourceImp
import com.example.cloudy.utility.ApiState
import com.example.cloudy.utility.Util
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar


const val NOTFICATION_PERM=1023
private const val TAG = "AlertFragment"
class AlertFragment : Fragment(),AlertAdapter.OnClickListener {
    private lateinit var binding: FragmentAlertBinding
    private lateinit var viewFactory: AlertViewModelFactory
    private lateinit var viewModel: AlertViewModel

    private lateinit var viewFactory2: CityViewModelFactory
    private lateinit var viewModel2: CityViewModel

    private lateinit var alertAdapter: AlertAdapter
    private lateinit var alertLayoutManager: LinearLayoutManager


    var date: String = " "
    var time: String = " "
    var radioValue: Int = -1
    var alertDescription:String=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alertAdapter = AlertAdapter(this)
        alertLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvAlert.apply {
            adapter = alertAdapter
            layoutManager = alertLayoutManager
        }

        viewFactory2 = CityViewModelFactory(
            WeatherRepositoryImp.getInstance
                (WeatherRemoteDataSourceImp.getInstance(), LocalDataSourceImp(requireContext()))
        )
        viewModel2 = ViewModelProvider(this, viewFactory2).get(CityViewModel::class.java)

        viewFactory = AlertViewModelFactory(
            WeatherRepositoryImp.getInstance
                (WeatherRemoteDataSourceImp.getInstance(), LocalDataSourceImp(requireContext()))
        )

        viewModel = ViewModelProvider(this, viewFactory).get(AlertViewModel::class.java)


        lifecycleScope.launch {
            viewModel.alertData.collectLatest { alertData ->
                if (alertData.isNotEmpty()) {
                    alertAdapter.submitList(alertData)
                    binding.tvAlert.visibility = View.GONE
                    binding.ivNot.visibility = View.GONE

                    for (i in alertData.indices) {
                        val alertDatabase = alertData[i]
                        viewModel.getWeatherAlert(
                            alertDatabase!!.lat,
                            alertDatabase.lon,
                            Util.API_KEY
                        )

                        viewModel.alertList.collectLatest { alert ->
                            when (alert) {
                                is ApiState.Loading -> {
                                    Log.i(TAG, "onViewCreated: Loading")
                                }

                                is ApiState.Success -> {
                                    val date = alertDatabase.date
                                    val parts = date.split("/")
                                    val day = parts[0].toInt()
                                    val month = parts[1].toInt() - 1
                                    val year = parts[2].toInt()

                                    val time = alertDatabase.time
                                    val timeParts = time.split(":")
                                    val hour = timeParts[0].toInt()
                                    val minute = timeParts[1].toInt()

                                    val calendar = Calendar.getInstance().apply {
                                        set(Calendar.YEAR, year)
                                        set(Calendar.MONTH, month)
                                        set(Calendar.DAY_OF_MONTH, day)
                                        set(Calendar.HOUR_OF_DAY, hour)
                                        set(Calendar.MINUTE, minute)
                                    }
                                    if (alert.data!!.alerts!=null){
                                        alertDescription=  alert.data.alerts[0].event + alert.data.alerts[0].description
                                        Log.i(TAG, "onViewCreated: $alertDescription")
                                    }else{
                                        alertDescription="Weather is fine,no alerts for this scheduled time."
                                    }

                                    var timeDifference = calendar.timeInMillis
                                    var current = System.currentTimeMillis()
                                    if (timeDifference - current >= 0) {
                                        if (alertDatabase.alertType==1){
                                            createChannel()
                                            scheduleNotification(timeDifference, i)
                                        }
                                        if (alertDatabase.alertType==2){
                                            setAlarmWithSound(requireContext(),timeDifference)
                                        }

                                    }

                                }

                                else -> {
                                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }
                }
            }
        }
        var isVisible = false
        binding.btnSecFloat.hide()
        binding.btnThirdFloat.hide()
        binding.btnAdd.setOnClickListener {
            if (!isVisible) {
                binding.btnSecFloat.show()
                binding.btnThirdFloat.show()
                isVisible = true
            } else {
                binding.btnSecFloat.hide()
                binding.btnThirdFloat.hide()
                isVisible = false
            }
        }
        binding.btnSecFloat.setOnClickListener {

            val dialog = AddDialogFragment()
            dialog.show(childFragmentManager, "AddDialogFragment")
            dialog.setOnSaveClickListener(object : AddDialogFragment.OnSaveClickListener {
                override fun onSaveClick(
                    selectedDate: String,
                    selectedTime: String,
                    radioButtonValue: Int
                ) {
                    val message =
                        "Date: $selectedDate\nTime: $selectedTime\nSelected Option: $radioButtonValue"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                    date = selectedDate
                    time = selectedTime
                    radioValue = radioButtonValue

                    lifecycleScope.launch {
                        viewModel2.cityList.collectLatest { alert ->
                            if (alert.isNotEmpty()) {
                                for (myAlert in alert) {
                                    if (!date.isNullOrBlank() && !time.isNullOrBlank() && radioValue != -1) {
                                        val alertData = AlertData(
                                            myAlert!!.cityName,
                                            myAlert.lat,
                                            myAlert.lon,
                                            date,
                                            time,
                                            radioValue
                                        )
                                        viewModel.inserAlertData(alertData)
                                        date = ""
                                        time = ""
                                        radioValue = -1
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "No alerts available",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }


                }
            })

        }
        binding.btnThirdFloat.setOnClickListener {
            val intent = Intent(requireContext(), MapsAlertActivity::class.java)
            startActivity(intent)
        }


    }

    override fun onDeleteClick(alert: AlertData) {
        lifecycleScope.launch {
            viewModel.deleteAlertData(alert)
        }

    }


    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelID,
                "Weather Alert",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            serviceChannel.description = "Alert description"
            val manager: NotificationManager =
                requireContext().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(serviceChannel)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(timeDifference: Long, notificationID: Int) {
        checkNotificationPermission()
        val notificationIntent = Intent(requireContext(), NotificationAlert::class.java)
        notificationIntent.putExtra("alertDescription",alertDescription)
        Log.i(TAG, "onViewCreated: $alertDescription")
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationID,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            timeDifference,
            pendingIntent
        )
    }

    fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.SET_ALARM
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the SET_ALARM permission
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.SET_ALARM),
                    NOTFICATION_PERM
                )
                return
            }
        }

    }

    @SuppressLint("ScheduleExactAlarm")
    fun setAlarmWithSound(context: Context, alarmTime: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, SoundAlert::class.java).let { intent ->
            intent.putExtra("alertDescription",alertDescription)

            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent)

    }

}
