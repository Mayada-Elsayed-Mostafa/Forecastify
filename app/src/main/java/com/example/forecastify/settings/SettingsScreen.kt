package com.example.forecastify.settings

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.forecastify.R
import com.example.forecastify.data.local.SettingsDataStore
import com.example.forecastify.utils.LanguageConverter

@Composable
fun SettingsScreen(navHostController: NavHostController, viewModel: SettingsViewModel) {
    val language by viewModel.language.collectAsState()
    val temperatureUnit by viewModel.temperatureUnit.collectAsState()
    val windSpeedUnit by viewModel.windSpeedUnit.collectAsState()
    val locationMethod by viewModel.locationMethod.collectAsState()

    viewModel.loadSettings()

    val context = LocalContext.current

    val settingsOptions = listOf(
        "Language" to listOf(
            stringResource(R.string.english),
            stringResource(R.string.arabic),
            stringResource(R.string.defaultt)
        ),
        "Temperature Unit" to listOf(
            stringResource(R.string.kelvin_k),
            stringResource(R.string.celsius_c),
            stringResource(R.string.fahrenheit_f)
        ),
        "Wind Speed Unit" to listOf(
            stringResource(R.string.meter_sec),
            stringResource(R.string.mile_hour)
        ),
        "Location Method" to listOf(stringResource(R.string.gps), stringResource(R.string.map))
    )

    val selectedOptions = remember { mutableStateMapOf<String, String>() }

    selectedOptions["Language"] = language
    selectedOptions["Temperature Unit"] = temperatureUnit
    selectedOptions["Wind Speed Unit"] = windSpeedUnit
    selectedOptions["Location Method"] = locationMethod

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(settingsOptions) { (title, options) ->
            SettingSection(title, options, selectedOptions) { key, value ->
                when (key) {
                    "Language" -> {
                        viewModel.saveSetting(SettingsDataStore.LANGUAGE_KEY, value)
                        if (value == "Arabic" || value == "العربية") {
                            LanguageConverter.changeLanguage(context, "ar")
                        }
                        if (value == "English" || value == "الإنجليزية") {
                            LanguageConverter.changeLanguage(context, "en")
                        }
                    }

                    "Temperature Unit" -> viewModel.saveSetting(
                        SettingsDataStore.TEMPERATURE_UNIT_KEY,
                        value
                    )

                    "Wind Speed Unit" -> viewModel.saveSetting(
                        SettingsDataStore.WIND_SPEED_UNIT_KEY,
                        value
                    )

                    "Location Method" -> viewModel.saveSetting(
                        SettingsDataStore.LOCATION_METHOD_KEY,
                        value
                    )
                }
            }
        }
    }
}

@Composable
fun SettingSection(
    title: String,
    options: List<String>,
    selectedOptions: MutableMap<String, String>,
    onOptionSelected: (String, String) -> Unit,
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
                                onClick = {
                                    selectedOptions[title] = option
                                    onOptionSelected(title, option)
                                },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (option == selectedOptions[title]),
                            onClick = {
                                selectedOptions[title] = option
                                onOptionSelected(title, option)
                            }
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