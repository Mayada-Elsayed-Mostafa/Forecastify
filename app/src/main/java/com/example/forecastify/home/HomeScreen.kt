package com.example.forecastify.home

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.forecastify.R
import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherItem
import com.example.forecastify.data.models.WeatherResponse
import com.example.forecastify.ui.theme.onPrimaryLight
import com.example.forecastify.ui.theme.primaryContainerLight
import com.example.forecastify.ui.theme.secondaryLight
import com.example.forecastify.utils.LanguageConverter
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(navHostController: NavHostController, viewModel: HomeViewModel, location: Location) {

    MainContent(navHostController, viewModel, location)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    navHostController: NavHostController,
    viewModel: HomeViewModel,
    location: Location,
) {

    val currentWeather = viewModel.weather.collectAsState()
    val currentForecast = viewModel.forecast.collectAsState()


    var lastFetchedLocation by remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(location) {
        if (lastFetchedLocation != location) {
            viewModel.getWeather(location)
            viewModel.getUpcomingForecast(location)
            lastFetchedLocation = location
        }
    }

    var state by remember { mutableIntStateOf(0) }
    val titles = listOf(
        stringResource(R.string.today), stringResource(R.string.tomorrow), stringResource(
            R.string._5_days
        )
    )

    Scaffold {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                CurrentWeatherComponent(currentWeather.value)

                PrimaryTabRow(selectedTabIndex = state) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = state == index,
                            onClick = { state = index },
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 4.dp, bottom = 4.dp)
                        ) {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = primaryContainerLight
                            )
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    when (state) {
                        0 -> {
                            DailyDetails(currentWeather.value)
                            HourlyDetails(getTodayForecast(currentForecast.value))
                        }

                        1 -> {
                            HourlyDetails(getTomorrowForecast(currentForecast.value))
                        }

                        2 -> {
                            if (currentForecast.value != null) {
                                Upcoming5DaysForecast(currentForecast.value!!)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrentWeatherComponent(weather: WeatherResponse?) {
    val currentDateTime = LocalDateTime.now()
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val formattedDate = currentDateTime.format(dateFormatter)
    val formattedTime = currentDateTime.format(timeFormatter)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {

        WeatherAnimation()

        Card(
            modifier = Modifier.padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Row {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row {
                        Text(
                            text = weather?.name ?: stringResource(R.string.unknown_location),
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Row {
                        Column(modifier = Modifier.weight(1f)) {
                            Row {
                                Text(
                                    text = LanguageConverter.formatNumber(
                                        weather?.main?.temp?.toInt() ?: 0
                                    ), fontSize = 42.sp, color = MaterialTheme.colorScheme.onPrimary
                                )
                                val unit = getTemperatureUnit(weather?.main?.temp ?: 0.0)
                                Text(
                                    text = unit,
                                    fontSize = 24.sp,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                            Row {
                                Text(
                                    text = stringResource(R.string.feels_like),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "${weather?.main?.feelsLike?.toInt() ?: "--"}",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                )
                                val unit = getTemperatureUnit(weather?.main?.feelsLike ?: 0.0)
                                Text(
                                    text = unit,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                )

                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter("https://openweathermap.org/img/wn/${weather?.weather?.firstOrNull()?.icon}.png"),
                                contentDescription = "Weather Icon",
                                modifier = Modifier.size(85.dp)
                            )
                            Text(
                                text = weather?.weather?.getOrNull(0)?.main ?: "--",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(text = weather?.weather?.getOrNull(0)?.description?.replaceFirstChar { it.uppercase() }
                                ?: "--",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                        }

                    }
                    Row {
                        Text(
                            text = formattedDate,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = formattedTime,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun WeatherAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.weather_animation))
    val progress by animateLottieCompositionAsState(composition)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = primaryContainerLight)
    ) {
        LottieAnimation(
            composition = composition, progress = { progress }, modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun DailyDetails(weather: WeatherResponse?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = primaryContainerLight)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.wind_speed),
                        contentDescription = "Wind Speed Icon",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            stringResource(R.string.wind_speed),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = onPrimaryLight
                        )
                        Text(
                            "${weather?.wind?.speed} m/s", fontSize = 14.sp, color = onPrimaryLight
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = primaryContainerLight)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pressure),
                        contentDescription = "Pressure Icon",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            stringResource(R.string.pressure),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = onPrimaryLight
                        )
                        Text(
                            "${weather?.main?.pressure} hPa",
                            fontSize = 14.sp,
                            color = onPrimaryLight
                        )
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = primaryContainerLight)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.rain_chance),
                        contentDescription = "Rain Chance Icon",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            stringResource(R.string.rain_chance),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = onPrimaryLight
                        )
                        Text("${weather?.clouds?.all}%", fontSize = 14.sp, color = onPrimaryLight)
                    }
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = primaryContainerLight)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.uv_index),
                        contentDescription = "Humidity Icon",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            stringResource(R.string.humidity),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = onPrimaryLight
                        )
                        Text(
                            "${weather?.main?.humidity}%", fontSize = 14.sp, color = onPrimaryLight
                        )
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            val sunriseUtc = weather?.sys?.sunrise ?: 0
            val sunsetUtc = weather?.sys?.sunset ?: 0
            val timezoneOffset = weather?.timezone ?: 0

            val sunriseLocal =
                Instant.ofEpochSecond(sunriseUtc + timezoneOffset).atZone(ZoneId.of("UTC"))
                    .toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))

            val sunsetLocal =
                Instant.ofEpochSecond(sunsetUtc + timezoneOffset).atZone(ZoneId.of("UTC"))
                    .toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))

            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = primaryContainerLight)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sunrise),
                        contentDescription = "Sun Rise Icon",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            stringResource(R.string.sunrise),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = onPrimaryLight
                        )
                        Text(text = sunriseLocal, fontSize = 14.sp, color = onPrimaryLight)
                    }
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = primaryContainerLight)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sunset),
                        contentDescription = "Sunset Icon",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            stringResource(R.string.sunset),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = onPrimaryLight
                        )
                        Text(text = sunsetLocal, fontSize = 14.sp, color = onPrimaryLight)
                    }
                }
            }
        }
    }
}

@Composable
fun HourlyDetails(forecastResponse: ForecastResponse?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = primaryContainerLight)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val forecastList = forecastResponse?.list ?: emptyList()
                items(forecastList.take(8)) { weatherItem ->
                    val time = Instant.ofEpochSecond(weatherItem.dt).atZone(ZoneId.systemDefault())
                        .toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = time, fontSize = 14.sp, color = onPrimaryLight)
                        Image(
                            painter = rememberAsyncImagePainter("https://openweathermap.org/img/wn/${weatherItem.weather.firstOrNull()?.icon}.png"),
                            contentDescription = "Weather Icon",
                            modifier = Modifier.size(50.dp)
                        )
                        Row {
                            Text(
                                text = "${weatherItem.main.temp.toInt()}",
                                fontSize = 14.sp,
                                color = onPrimaryLight
                            )
                            val unit = getTemperatureUnit(weatherItem.main.temp)
                            Text(
                                text = unit, fontSize = 14.sp, color = onPrimaryLight
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Upcoming5DaysForecast(forecastResponse: ForecastResponse) {

    val filteredForecast = remember(forecastResponse) {
        forecastResponse.list.groupBy { it.dt_txt.substring(0, 10) }
            .map { (_, forecasts) -> forecasts.first() }
    }

    LazyColumn(
        modifier = Modifier.padding(bottom = 105.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filteredForecast) { weatherItem ->
            Upcoming5DaysForecastItem(weatherItem)
        }
    }
}

@Composable
fun Upcoming5DaysForecastItem(weatherItem: WeatherItem) {
    val date = remember(weatherItem.dt) {
        SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(Date(weatherItem.dt * 1000))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = primaryContainerLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.titleMedium,
                    color = onPrimaryLight
                )
                Text(
                    text = weatherItem.weather.firstOrNull()?.description ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium,
                    color = secondaryLight
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Row {
                    Text(
                        text = stringResource(R.string.min),
                        style = MaterialTheme.typography.bodySmall,
                        color = onPrimaryLight
                    )
                    Text(
                        text = "${weatherItem.main.temp_min}",
                        style = MaterialTheme.typography.bodySmall,
                        color = onPrimaryLight
                    )
                    val unit = getTemperatureUnit(weatherItem.main.temp)
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodySmall,
                        color = onPrimaryLight
                    )
                }
                Row {
                    Text(
                        text = stringResource(R.string.max),
                        style = MaterialTheme.typography.bodySmall,
                        color = onPrimaryLight
                    )
                    Text(
                        text = "${weatherItem.main.temp_max}",
                        style = MaterialTheme.typography.bodySmall,
                        color = onPrimaryLight
                    )
                    val unit = getTemperatureUnit(weatherItem.main.temp)
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodySmall,
                        color = onPrimaryLight
                    )
                }
            }

            Image(
                painter = rememberAsyncImagePainter("https://openweathermap.org/img/wn/${weatherItem.weather.firstOrNull()?.icon}.png"),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(60.dp)
            )

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "Navigate to details",
                    tint = secondaryLight
                )
            }
        }
    }
}

fun getTodayForecast(forecastResponse: ForecastResponse?): ForecastResponse? {
    val tomorrow = LocalDateTime.now().toLocalDate()

    val filteredList = forecastResponse?.list?.filter { weatherItem ->
        val itemDate =
            Instant.ofEpochSecond(weatherItem.dt).atZone(ZoneId.systemDefault()).toLocalDate()
        itemDate == tomorrow
    }

    return forecastResponse?.copy(list = filteredList ?: emptyList())
}

fun getTomorrowForecast(forecastResponse: ForecastResponse?): ForecastResponse? {
    val tomorrow = LocalDateTime.now().plusDays(1).toLocalDate()

    val filteredList = forecastResponse?.list?.filter { weatherItem ->
        val itemDate =
            Instant.ofEpochSecond(weatherItem.dt).atZone(ZoneId.systemDefault()).toLocalDate()
        itemDate == tomorrow
    }

    return forecastResponse?.copy(list = filteredList ?: emptyList())
}

@Composable
fun getTemperatureUnit(tempInKelvin: Double): String {
    val unit = when {
        tempInKelvin < 273.15 -> stringResource(R.string.celsius_unit)
        tempInKelvin in 273.15..373.15 -> stringResource(R.string.k)
        else -> stringResource(R.string.fahrenheit_unit)
    }
    return unit
}
