package com.example.cloudy.alert.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import com.example.cloudy.model.MapCity
import com.example.cloudy.model.WeatherRepositoryImp
import com.example.cloudy.network.WeatherRemoteDataSourceImp
import com.example.cloudy.settings.SettingsFragment
import com.example.cloudy.utility.ApiState
import com.example.cloudy.utility.Util
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
const val ALARM_PERM=100
val REQUEST_OVERLAY_PERMISSION = 123
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

    private val pendingIntentsMap = mutableMapOf<String, PendingIntent>()


    private val soundAlert = SoundAlert()



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

    override fun onResume() {
        super.onResume()
        if(SettingsFragment.languageSP){
            activity?.title = "Alert"
        }else{
            activity?.title = "صفحة المنبه"

        }

    }
    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkNotificationPermission()
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected = networkInfo?.isConnectedOrConnecting == true

        alertAdapter = AlertAdapter(this)
        alertLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvAlert.apply {
            adapter = alertAdapter
            layoutManager = alertLayoutManager
        }
        alertAdapter.notifyDataSetChanged()


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

        binding.swipper.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.getAlertData()
                alertAdapter.submitList(viewModel.alertData.value)
                binding.swipper.isRefreshing = false
                if (viewModel.alertData.value.isNotEmpty()) {
                    binding.tvAlert.visibility = View.GONE
                    binding.ivNot.visibility = View.GONE
                } else {
                    binding.tvAlert.visibility = View.VISIBLE
                    binding.ivNot.visibility = View.VISIBLE
                }

            }
        }

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

        binding.btnSecFloat.setOnClickListener {
            if(!isConnected) {
                val snackbar =
                    Snackbar.make(binding.cl, "No Network Connection", Snackbar.LENGTH_SHORT)
                        .setAction("Dismiss") {
                        }.setActionTextColor(R.color.md_red_900)
                snackbar.show()
            }
            else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + requireContext().packageName)
                    )
                    startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
                }
                 if (viewModel2.cityList.value!= emptyList<MapCity>()) {
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
                             Toast.makeText(requireContext(), "Alert Added successfully", Toast.LENGTH_SHORT).show()

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
                else{
                    Toast.makeText(requireContext(),"Please add city to favorite first",Toast.LENGTH_SHORT).show()
                 }

        }
    }
    }

    override fun onDeleteClick(alert: AlertData) {
        lifecycleScope.launch {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure you want to delete this?")
                .setPositiveButton("Yes") { dialog, id ->
                    viewModel.deleteAlertData(alert)
                    val pendingIntentKey = generatePendingIntentKey(alert)
                    val pendingIntent = pendingIntentsMap[pendingIntentKey]
                    pendingIntent?.let { existingPendingIntent ->
                        val alarmManager =
                            requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        alarmManager.cancel(existingPendingIntent)
                        pendingIntentsMap.remove(pendingIntentKey)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
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

        if(Build.VERSION.SDK_INT >+ Build.VERSION_CODES.TIRAMISU){
            requestPermissions( arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTFICATION_PERM)
        }

        if(Build.VERSION.SDK_INT >+ Build.VERSION_CODES.TIRAMISU){
            requestPermissions( arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM), ALARM_PERM)
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

    private fun generatePendingIntentKey(alert: AlertData): String {
        return "${alert.cityName}_${alert.date}_${alert.time}"
    }

}
