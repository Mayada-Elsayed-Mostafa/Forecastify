package com.example.forecastify.alerts

import android.app.Activity
import android.app.NotificationManager
import android.location.Location
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.forecastify.R
import com.example.forecastify.data.models.AlarmItem
import com.example.forecastify.ui.theme.primaryContainerLight
import com.example.forecastify.utils.NotificationConstants.NOTIFICATION_ID
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    navController: NavController,
    viewModel: AlertsViewModel,
    locationState: Location,
) {

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }

    var showTimePicker by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf("") }

    var alarmName by remember { mutableStateOf("") }


    val timePickerState = rememberTimePickerState(
        initialHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        initialMinute = Calendar.getInstance().get(Calendar.MINUTE),
        is24Hour = false
    )

    val alarms by viewModel.alarms.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.loadAlarms()
    }

    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = { showBottomSheet = true },
            shape = CircleShape,
            modifier = Modifier.padding(bottom = 80.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Alert")
        }
    }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (alarms.isEmpty()) {
                    NoAlertsScreen()
                } else {
                    LazyColumn {
                        items(alarms.size) { index ->
                            val alarm = alarms[index]
                            AlarmItem(alarm, onDelete = { viewModel.deleteAlarm(alarm, context) })
                        }
                    }
                }
                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        sheetState = sheetState,
                        modifier = Modifier.padding(bottom = 150.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                stringResource(R.string.set_alarm),
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Card {
                                Box(modifier = Modifier.padding(8.dp)) {
                                    Card {
                                        OutlinedTextField(
                                            value = alarmName,
                                            onValueChange = { alarmName = it },
                                            label = { Text("Enter Alarm Name") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(64.dp),
                                            singleLine = true,
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.padding(bottom = 18.dp))
                            Card{
                                Box(modifier = Modifier.padding(8.dp)) {
                                    Column {
                                        OutlinedTextField(value = selectedDate,
                                            onValueChange = { },
                                            label = { Text(stringResource(R.string.date)) },
                                            readOnly = true,
                                            trailingIcon = {
                                                IconButton(onClick = {
                                                    showDatePicker = true
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Default.DateRange,
                                                        contentDescription = "Select date"
                                                    )
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(64.dp)
                                        )
                                    }
                                }
                            }
                            if (showDatePicker) {
                                DatePickerModal(onDismiss = { showDatePicker = false },
                                    onDateSelected = { millis ->
                                        if (millis != null) {
                                            selectedDate = convertMillisToDate(millis)
                                        }
                                        showDatePicker = false
                                    })
                            }
                            Spacer(modifier = Modifier.padding(bottom = 18.dp))
                            Card {
                                Box(modifier = Modifier.padding(8.dp)) {
                                    Column {
                                        OutlinedTextField(value = selectedTime,
                                            onValueChange = { },
                                            label = { Text(stringResource(R.string.time)) },
                                            readOnly = true,
                                            trailingIcon = {
                                                IconButton(onClick = {
                                                    showTimePicker = true
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Filled.DateRange,
                                                        contentDescription = "Select time"
                                                    )
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(64.dp)
                                        )
                                    }
                                }
                            }
                            if (showTimePicker) {
                                TimePickerDialog(timePickerState = timePickerState,
                                    onTimeSelected = { hour, minute ->
                                        val formattedTime = Calendar.getInstance().apply {
                                            set(Calendar.HOUR_OF_DAY, hour)
                                            set(Calendar.MINUTE, minute)
                                        }.time

                                        val timeFormat =
                                            SimpleDateFormat("hh:mm a", Locale.getDefault())
                                        selectedTime = timeFormat.format(formattedTime)
                                        showTimePicker = false
                                    },
                                    onDismiss = { showTimePicker = false })
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                Button(modifier = Modifier.padding(end = 8.dp), onClick = {
                                    scope.launch {
                                        sheetState.hide()
                                        showBottomSheet = false
                                    }
                                }) {
                                    Text(stringResource(R.string.cancel))
                                }
                                Button(modifier = Modifier.padding(end = 8.dp), onClick = {

                                    if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                                        val notificationManager =
                                            context.getSystemService(NotificationManager::class.java) as NotificationManager
                                        if (!notificationManager.areNotificationsEnabled()) {
                                            ActivityCompat.requestPermissions(
                                                context as Activity,
                                                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                                                NOTIFICATION_ID
                                            )
                                        }
                                    }

                                    if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                                        scope.launch {
                                            try {
                                                val dateFormat = SimpleDateFormat(
                                                    "MM/dd/yyyy", Locale.getDefault()
                                                )
                                                val timeFormat = SimpleDateFormat(
                                                    "hh:mm a", Locale.getDefault()
                                                )

                                                val date = dateFormat.parse(selectedDate)
                                                var time = timeFormat.parse(selectedTime)

                                                if (date != null && time != null) {
                                                    val calendar = Calendar.getInstance().apply {
                                                        time = date
                                                        val timeCalendar =
                                                            Calendar.getInstance().apply {
                                                                this.time = time
                                                            }
                                                        set(
                                                            Calendar.HOUR_OF_DAY,
                                                            timeCalendar.get(Calendar.HOUR_OF_DAY)
                                                        )
                                                        set(
                                                            Calendar.MINUTE,
                                                            timeCalendar.get(Calendar.MINUTE)
                                                        )
                                                        set(
                                                            Calendar.SECOND, 0
                                                        )
                                                    }

                                                    val alarmItem = AlarmItem(
                                                        lat = locationState.latitude,
                                                        lon = locationState.latitude,
                                                        alarmName = alarmName,
                                                        selectedDate = selectedDate,
                                                        selectedTime = selectedTime
                                                    )

                                                    viewModel.scheduleAlarm(
                                                        alarmItem, context, locationState
                                                    )
                                                } else {
                                                }

                                                scope.launch {
                                                    sheetState.hide()
                                                    showBottomSheet = false
                                                }

                                            } catch (e: Exception) {
                                            }
                                        }
                                    }
                                }) {
                                    Text(stringResource(R.string.save))
                                }
                            }
                            Spacer(modifier = Modifier.padding(bottom = 24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlarmItem(
    alarm: AlarmItem,
    onDelete: (AlarmItem) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)

    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = alarm.alarmName, style = MaterialTheme.typography.bodyLarge)

                Text(
                    text = alarm.selectedDate, style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = alarm.selectedTime, style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = { onDelete(alarm) }) {
                Image(
                    painter = rememberAsyncImagePainter(R.drawable.delete_icon),
                    contentDescription = "Delete Alarm",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = {
            onDateSelected(datePickerState.selectedDateMillis)
            onDismiss()
        }) {
            Text(stringResource(R.string.ok))
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.cancel))
        }
    }) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    timePickerState: TimePickerState,
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = {
            onTimeSelected(timePickerState.hour, timePickerState.minute)
            onDismiss()
        }) {
            Text(stringResource(R.string.ok))
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.cancel))
        }
    }, text = {
        TimeInput(state = timePickerState)
    })
}


fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Composable
fun NoAlertsScreen() {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_clock))
    val progress by animateLottieCompositionAsState(composition)

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition, progress = progress, modifier = Modifier.size(250.dp)
        )
    }
}
