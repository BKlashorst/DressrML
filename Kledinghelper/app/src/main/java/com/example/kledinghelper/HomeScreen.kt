package com.example.kledinghelper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kledinghelper.ui.theme.CormorantGaramond
import com.example.kledinghelper.ui.theme.KledingColors
import com.example.kledinghelper.ui.theme.Typography
import com.example.kledinghelper.viewmodel.ClosetViewModel
import com.example.kledinghelper.viewmodel.WeatherUiState
import com.example.kledinghelper.viewmodel.WeatherViewModel
import kotlin.math.roundToInt

fun weatherIconUrl(iconCode: String) = "https://openweathermap.org/img/wn/${iconCode}@2x.png"


// Celsius naar Fahrenheit
fun celsiusToFahrenheit(c: Double) = (c * 9.0 / 5.0 + 32)

@Composable
fun HomeScreen(navController: NavController, weatherViewModel: WeatherViewModel, closetViewModel: ClosetViewModel) {
    val uiState = weatherViewModel.weatherUiState
    val currentCity by weatherViewModel.city.collectAsState()
    val outfits by closetViewModel.outfits.collectAsState()
    val recentItems by closetViewModel.recentItems.collectAsState()

    val context = LocalContext.current
    val settingsPrefs = remember { context.getSharedPreferences("AppSettings", android.content.Context.MODE_PRIVATE) }
    val userPrefs = remember { context.getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE) }

    var tempUnit by remember { mutableStateOf(settingsPrefs.getString("temp_unit", "°C") ?: "°C") }
    var aantalSuggesties by remember { mutableStateOf(settingsPrefs.getInt("aantal_suggesties", 3)) }
    val userName = remember { userPrefs.getString("user_name", "Bram") ?: "Bram" }

    // Herlaad instellingen bij terugkeer
    LaunchedEffect(Unit) {
        tempUnit = settingsPrefs.getString("temp_unit", "°C") ?: "°C"
        aantalSuggesties = settingsPrefs.getInt("aantal_suggesties", 3)
    }

    Column(modifier = Modifier.fillMaxSize().background(KledingColors.WarmCreme)
        .verticalScroll(rememberScrollState()).padding(bottom = 80.dp)) {

        // Groet
        Column(modifier = Modifier.padding(top = 24.dp, start = 20.dp, end = 20.dp)) {
            Text("Goedemorgen,", style = Typography.displayLarge)
            Text(userName, style = Typography.displayLarge.copy(color = KledingColors.KoperAccent))
        }

        // Weerkaart — zoals screenshot
        Box(modifier = Modifier.padding(20.dp).fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF2C1F14), Color(0xFF1C140D))))
            .padding(horizontal = 20.dp, vertical = 18.dp)) {

            when (uiState) {
                is WeatherUiState.Success -> {
                    val weather = uiState.weather
                    val iconCode = weather.weather.firstOrNull()?.icon ?: "01d"
                    val tempC = weather.main.temp
                    val displayTemp = if (tempUnit == "°F") celsiusToFahrenheit(tempC).roundToInt()
                                      else tempC.roundToInt()

                    Column {
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top) {

                            // Links: stad + temp + beschrijving
                            Column(modifier = Modifier.weight(1f)) {
                                // Stad + locatie icoon (geen emoji)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(currentCity, color = Color.White, style = Typography.titleLarge.copy(fontSize = 18.sp, color = Color.White))
                                    Spacer(Modifier.width(6.dp))
                                    Icon(Icons.Outlined.LocationOn, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                                Spacer(Modifier.height(4.dp))
                                // Temperatuur groot
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(SpanStyle(fontSize = 44.sp, fontFamily = CormorantGaramond, color = Color.White)) {
                                            append("$displayTemp")
                                        }
                                        withStyle(SpanStyle(fontSize = 18.sp, color = Color.White)) {
                                            append("°")
                                        }
                                    }
                                )
                                Text(
                                    text = weather.weather.firstOrNull()?.description
                                        ?.replaceFirstChar { it.uppercase() } ?: "",
                                    color = Color.LightGray,
                                    style = Typography.bodyMedium.copy(color = Color.LightGray)
                                )
                            }

                            // Rechts: icoon met zachte radiale fade
                            Box(modifier = Modifier.size(88.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.13f),
                                            Color.White.copy(alpha = 0.04f),
                                            Color.Transparent
                                        )
                                    )
                                ),
                                contentAlignment = Alignment.Center) {
                                AsyncImage(
                                    model = weatherIconUrl(iconCode),
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.25f))
                        Spacer(Modifier.height(10.dp))

                        // Outfit suggesties tekst
                        Text(buildAnnotatedString {
                            withStyle(SpanStyle(color = KledingColors.KoperAccent)) { append("$aantalSuggesties") }
                            append(" ")
                            withStyle(SpanStyle(color = KledingColors.KoperAccent)) { append("outfit") }
                            append(" suggesties voor deze temperatuur")
                        }, style = Typography.bodyMedium.copy(color = Color.White))
                    }
                }
                is WeatherUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = KledingColors.KoperAccent, modifier = Modifier.size(32.dp))
                    }
                }
                is WeatherUiState.Error -> {
                    Text("Kon weerdata niet laden", color = Color.White, style = Typography.bodySmall)
                }
            }
        }

        // Outfits voor vandaag
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(buildAnnotatedString {
                withStyle(SpanStyle(color = KledingColors.KoperAccent, fontFamily = CormorantGaramond)) { append("Outfits") }
                append(" voor vandaag")
            }, style = Typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            if (outfits.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp))
                    .background(KledingColors.KoperLicht), contentAlignment = Alignment.Center) {
                    Text("Nog geen outfits", style = Typography.bodySmall)
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(outfits.take(aantalSuggesties)) { outfit ->
                        AsyncImage(model = outfit.imagePath, contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.width(170.dp).height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { navController.navigate(Screen.OutfitDetail.createRoute(outfit.id)) })
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Kledingkast preview
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("Kledingkast", style = Typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(12.dp))
                    .background(KledingColors.KoperLicht)
                    .clickable { navController.navigate(Screen.Kledingkast.route) }.padding(8.dp)) {
                    if (recentItems.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Lege kast", style = Typography.bodySmall) }
                    } else {
                        LazyVerticalGrid(columns = GridCells.Fixed(2), userScrollEnabled = false,
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            gridItems(recentItems.take(4)) { item ->
                                AsyncImage(model = item.imagePath, contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(8.dp)))
                            }
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(12.dp))
                    .background(KledingColors.KoperLicht)
                    .clickable { navController.navigate(Screen.NieuwItem.route) },
                    contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Add, null, tint = KledingColors.KoperAccent, modifier = Modifier.size(32.dp))
                        Text("Voeg item toe", style = Typography.labelMedium.copy(color = KledingColors.KoperAccent))
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
