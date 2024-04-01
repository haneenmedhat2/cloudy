package com.example.cloudy.alert.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.cloudy.databinding.CuatomAlertDialogBinding
import java.util.Calendar

class AddDialogFragment : DialogFragment() {
    private lateinit var binding: CuatomAlertDialogBinding
    private var onSaveClickListener: OnSaveClickListener? = null

    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private var radioButtonValue: Int = -1
    val REQUEST_OVERLAY_PERMISSION = 123

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
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

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