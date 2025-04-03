package com.example.forecastify.alerts

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.forecastify.data.models.AlarmItem
import com.example.forecastify.data.repository.RepositoryImp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class AlertsViewModel(private val repository: RepositoryImp) : ViewModel() {

    private val _alarms = MutableStateFlow<List<AlarmItem>>(emptyList())
    val alarms: StateFlow<List<AlarmItem>> = _alarms.asStateFlow()

    init {
        loadAlarms()
    }

    fun loadAlarms() {
        viewModelScope.launch {
            repository.getAllAlarms().collect { alarmsList ->
                _alarms.emit(alarmsList)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    suspend fun scheduleAlarm(alarm: AlarmItem, context: Context, locationState: Location) {
        val calendar = Calendar.getInstance().apply {
            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

            val date = dateFormat.parse(alarm.selectedDate)
            val time = timeFormat.parse(alarm.selectedTime)

            if (date != null && time != null) {
                timeInMillis = date.time
                val timeCalendar = Calendar.getInstance().apply { this.time = time }
                set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
            }
        }

        val delay = calendar.timeInMillis - System.currentTimeMillis()
        if (delay > 0) {
            val workManager = WorkManager.getInstance(context)

            val inputData = Data.Builder().putString("selectedTime", alarm.selectedTime)
                .putDouble("lat", locationState.latitude).putDouble("lon", locationState.longitude)
                .build()

            val request = OneTimeWorkRequestBuilder<AlertWorker>().setInitialDelay(
                    delay,
                    TimeUnit.MILLISECONDS
                ).setInputData(inputData).build()

            workManager.enqueue(request)

            viewModelScope.launch {
                repository.addAlarm(alarm)
                loadAlarms()
            }
        }
    }

    fun deleteAlarm(alarm: AlarmItem, context: Context) {
        viewModelScope.launch {
            repository.removeAlarm(alarm)
            WorkManager.getInstance(context).cancelAllWorkByTag(alarm.alarmName)
        }
    }
}

class AlertsFactory(private val repository: RepositoryImp) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlertsViewModel(repository) as T
    }
}
