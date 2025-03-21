package com.example.forecastify.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.forecastify.R
import com.example.forecastify.settings.ui.theme.AppTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                SettingScreen()
            }
        }
    }
}

@Composable
fun SettingScreen() {
    val settingsOptions = listOf(
        stringResource(R.string.select_language) to listOf(
            stringResource(R.string.arabic),
            stringResource(
                R.string.english
            ), stringResource(R.string.defaultt)
        ),
        stringResource(R.string.select_temperature_unit) to listOf(
            stringResource(R.string.celsius_c),
            stringResource(
                R.string.kelvin_k
            ), stringResource(R.string.fahrenheit_f)
        ),
        stringResource(R.string.select_location) to listOf(
            stringResource(R.string.gps),
            stringResource(
                R.string.map
            )
        ),
        stringResource(R.string.select_wind_speed_unit) to listOf(
            stringResource(R.string.meter_second),
            stringResource(
                R.string.mile_hour
            )
        )
    )

    val selectedOptions = remember {
        mutableStateMapOf<String, String>().apply {
            settingsOptions.forEach { (title, options) -> put(title, options.first()) }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(settingsOptions) { (title, options) ->
            SettingSection(title, options, selectedOptions)
        }
    }
}

@Composable
fun SettingSection(
    title: String,
    options: List<String>,
    selectedOptions: MutableMap<String, String>,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 16.sp, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.selectableGroup()) {
                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (option == selectedOptions[title]),
                                onClick = { selectedOptions[title] = option },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (option == selectedOptions[title]),
                            onClick = { selectedOptions[title] = option }
                        )
                        Text(
                            text = option,
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
