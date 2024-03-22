package com.example.cloudy.alert.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cloudy.alert.viewmodel.AlertViewModel
import com.example.cloudy.alert.viewmodel.AlertViewModelFactory
import com.example.cloudy.databinding.CuatomAlertDialogBinding
import com.example.cloudy.databinding.FragmentAlertBinding
import com.example.cloudy.db.LocalDataSourceImp
import com.example.cloudy.model.AlertData
import com.example.cloudy.model.WeatherRepositoryImp
import com.example.cloudy.network.WeatherRemoteDataSourceImp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

private const val TAG = "AlertFragment"
class AlertFragment : Fragment() {
    private lateinit var binding: FragmentAlertBinding
    private lateinit var viewFactory: AlertViewModelFactory
    private lateinit var viewModel: AlertViewModel
   var date: String=" "
    var time: String=" "
   var  radioValue: Int=-1
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

        viewFactory = AlertViewModelFactory(
            WeatherRepositoryImp.getInstance
                (WeatherRemoteDataSourceImp.getInstance(), LocalDataSourceImp(requireContext()))
        )
        
        viewModel = ViewModelProvider(this, viewFactory).get(AlertViewModel::class.java)
        
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
            showAddDialog()
            lifecycleScope.launch {
                viewModel.alert.collectLatest { alert ->
                    if (alert.isNotEmpty()) {
                        val myAlert = alert.lastOrNull()
                        myAlert?.let {
                            if (!date.isNullOrBlank() && !time.isNullOrBlank() && radioValue != -1) {
                                val alertData = AlertData(it.cityName, it.lat, it.lon, date, time, radioValue)
                                viewModel.inserAlertData(alertData)
                            } else {
                                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "No alerts available", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.btnThirdFloat.setOnClickListener{
           val intent=Intent(requireContext(), MapsAlertActivity::class.java)
            startActivity(intent)
        }



        }
    private fun showAddDialog() {
        val dialog = AddDialogFragment()
        dialog.show(childFragmentManager, "AddDialogFragment")
        dialog.setOnSaveClickListener(object : AddDialogFragment.OnSaveClickListener {
            override fun onSaveClick(
                selectedDate: String,
                selectedTime: String,
                radioButtonValue: Int
            ) {
                val message = "Date: $selectedDate\nTime: $selectedTime\nSelected Option: $radioButtonValue"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                date=selectedDate
                time=selectedTime
                radioValue=radioButtonValue
            }
        })
    }

}

class AddDialogFragment : DialogFragment() {
    private lateinit var binding: CuatomAlertDialogBinding
    private var onSaveClickListener: OnSaveClickListener? = null

    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private var radioButtonValue: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = CuatomAlertDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnSave.setOnClickListener {
            if (selectedDate == null || selectedTime == null || radioButtonValue == -1) {
                Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                onSaveClickListener?.onSaveClick(selectedDate!!, selectedTime!!, radioButtonValue)
                dismiss()
            }
        }


        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.calendarImageView.setOnClickListener { showDatePicker() }
        binding.clockImageView.setOnClickListener { showTimePicker() }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = binding.root.findViewById<RadioButton>(checkedId)
            val selectedText = radioButton?.text?.toString()
            radioButtonValue = when (selectedText) {
                "Notification" -> 1
                "Alarm" -> 2
                else -> -1
            }
        }
    }

        fun setOnSaveClickListener(listener: OnSaveClickListener) {
            onSaveClickListener = listener
        }

        private fun showDatePicker() {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                    selectedDate = "$dayOfMonth/${month + 1}/$year"
                }, year, month, day)
            datePickerDialog.show()
        }

        private fun showTimePicker() {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                selectedTime = "$hourOfDay:$minute"
            }, hour, minute, true)
            timePickerDialog.show()
        }


        interface OnSaveClickListener {
            fun onSaveClick(selectedDate: String, selectedTime: String, radioButtonValue: Int)
        }

}