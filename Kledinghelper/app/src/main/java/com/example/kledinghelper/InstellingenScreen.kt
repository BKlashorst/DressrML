package com.example.kledinghelper

import android.Manifest
import android.location.Geocoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.example.kledinghelper.ui.theme.KledingColors
import com.example.kledinghelper.ui.theme.PlusJakartaSans
import com.example.kledinghelper.ui.theme.Typography
import com.example.kledinghelper.viewmodel.ClosetViewModel
import com.example.kledinghelper.viewmodel.WeatherViewModel
import java.io.File
import java.util.Locale

@Composable
fun InstellingenScreen(navController: NavController, weatherViewModel: WeatherViewModel, closetViewModel: ClosetViewModel) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE) }
    val settingsPrefs = remember { context.getSharedPreferences("AppSettings", android.content.Context.MODE_PRIVATE) }

    var savedName by remember { mutableStateOf("") }
    var savedPhotoPath by remember { mutableStateOf<String?>(null) }

    // Dynamische stats
    val allItems by closetViewModel.clothingItems.collectAsState()
    val favorites by closetViewModel.favorites.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val obs = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                savedName = prefs.getString("user_name", "Gebruiker") ?: "Gebruiker"
                savedPhotoPath = prefs.getString("user_photo_path", null)
            }
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
    }

    var showLocatieDialog by remember { mutableStateOf(false) }
    var locatieInput by remember { mutableStateOf("") }
    val currentCity by weatherViewModel.city.collectAsState()

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationPermLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        @Suppress("DEPRECATION")
                        val addresses = Geocoder(context, Locale.getDefault()).getFromLocation(loc.latitude, loc.longitude, 1)
                        val city = addresses?.firstOrNull()?.locality ?: addresses?.firstOrNull()?.adminArea ?: return@addOnSuccessListener
                        weatherViewModel.updateCity(city)
                    }
                }
            } catch (_: SecurityException) {}
        }
    }

    var tempUnit by remember { mutableStateOf(settingsPrefs.getString("temp_unit", "°C") ?: "°C") }
    var koudeDrempel by remember { mutableStateOf(settingsPrefs.getInt("koude_drempel", 10)) }
    var aantalSuggesties by remember { mutableStateOf(settingsPrefs.getInt("aantal_suggesties", 3)) }
    var meldingen by remember { mutableStateOf(settingsPrefs.getBoolean("meldingen", true)) }
    var kledingkastCols by remember { mutableStateOf(settingsPrefs.getInt("kledingkast_cols", 2)) }

    fun save() { settingsPrefs.edit().putString("temp_unit", tempUnit).putInt("koude_drempel", koudeDrempel)
        .putInt("aantal_suggesties", aantalSuggesties).putBoolean("meldingen", meldingen)
        .putInt("kledingkast_cols", kledingkastCols).apply() }

    if (showLocatieDialog) {
        AlertDialog(onDismissRequest = { showLocatieDialog = false },
            title = { Text("Locatie aanpassen", style = Typography.headlineSmall) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = locatieInput, onValueChange = { locatieInput = it },
                        placeholder = { Text("Bijv. Amsterdam") }, singleLine = true, colors = fieldColors())
                    OutlinedButton(onClick = { showLocatieDialog = false; locationPermLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = KledingColors.KoperAccent),
                        border = androidx.compose.foundation.BorderStroke(1.dp, KledingColors.KoperAccent)) {
                        Icon(Icons.Outlined.MyLocation, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Huidige locatie gebruiken")
                    }
                }
            },
            confirmButton = { TextButton(onClick = { if (locatieInput.isNotBlank()) weatherViewModel.updateCity(locatieInput.trim()); showLocatieDialog = false; locatieInput = "" }) { Text("Opslaan", color = KledingColors.KoperAccent) } },
            dismissButton = { TextButton(onClick = { showLocatieDialog = false; locatieInput = "" }) { Text("Annuleren") } },
            containerColor = KledingColors.WarmCreme)
    }

    Column(modifier = Modifier.fillMaxSize().background(KledingColors.WarmCreme)
        .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 80.dp)) {

        Text("Instellingen", style = Typography.headlineLarge, modifier = Modifier.padding(top = 24.dp))
        Spacer(Modifier.height(24.dp))

        Surface(color = KledingColors.Wit, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { navController.navigate(Screen.ProfielBewerken.route) }) {
                    val photoFile = savedPhotoPath?.let { File(it) }
                    if (photoFile != null && photoFile.exists()) {
                        AsyncImage(model = photoFile, contentDescription = null, contentScale = ContentScale.Crop,
                            modifier = Modifier.size(48.dp).clip(CircleShape))
                    } else {
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.LightGray))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(savedName.ifBlank { "Gebruiker" }, style = Typography.titleLarge, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ChevronRight, null, tint = KledingColors.KoperAccent)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFEEEEEE))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatBox("${allItems.size}", "Items", Modifier.weight(1f))
                    StatBox("${favorites.size}", "Favorieten", Modifier.weight(1f))
                    StatBox("7x", "Gedragen", Modifier.weight(1f))
                }
            }
        }

        SettingSectionHeader("Locatie & Weer")
        Surface(color = KledingColors.Wit, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column {
                SettingRow("Locatie", trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clickable { showLocatieDialog = true }) {
                        Text(currentCity, color = KledingColors.KoperAccent, fontFamily = PlusJakartaSans, fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                        Icon(Icons.Outlined.MyLocation, null, tint = KledingColors.KoperAccent, modifier = Modifier.size(16.dp))
                    }
                })
                HorizontalDivider(color = Color(0xFFF5F5F5))
                SettingRow("Temperatuur eenheid", trailing = {
                    SegmentedButtons(listOf("°C", "°F"), tempUnit) { tempUnit = it; save() }
                })
                HorizontalDivider(color = Color(0xFFF5F5F5))
                SettingRow("Koude drempel", trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StepperButton("−") { if (koudeDrempel > 0) { koudeDrempel--; save() } }
                        Text("$koudeDrempel ${tempUnit}", style = Typography.bodyMedium)
                        StepperButton("+") { koudeDrempel++; save() }
                    }
                })
            }
        }

        SettingSectionHeader("Kleding")
        Surface(color = KledingColors.Wit, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column {
                SettingRow("Aantal outfit suggesties", trailing = {
                    SegmentedButtons(listOf("2", "3", "5"), aantalSuggesties.toString()) { aantalSuggesties = it.toInt(); save() }
                })
                HorizontalDivider(color = Color(0xFFF5F5F5))
                SettingRow("Meldingen", trailing = {
                    Switch(checked = meldingen, onCheckedChange = { meldingen = it; save() },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = KledingColors.WarmDonker,
                            checkedThumbColor = androidx.compose.ui.graphics.Color.White
                        ))
                })
                HorizontalDivider(color = Color(0xFFF5F5F5))
                SettingRow("Kledingkast kolommen", trailing = {
                    SegmentedButtons(listOf("2", "3"), kledingkastCols.toString()) { kledingkastCols = it.toInt(); save() }
                })
                HorizontalDivider(color = Color(0xFFF5F5F5))
                SettingRow("Categorieën beheren", onClick = { navController.navigate(Screen.Categorieen.route) })
                HorizontalDivider(color = Color(0xFFF5F5F5))
                SettingRow("Verborgen categorieën", onClick = { navController.navigate(Screen.Zichtbaarheid.route) })
            }
        }
    }
}

@Composable
fun SegmentedButtons(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFEBE6DF)) {
        Row(modifier = Modifier.padding(2.dp)) {
            options.forEach { option ->
                val isActive = option == selected
                Surface(shape = RoundedCornerShape(6.dp),
                    color = if (isActive) KledingColors.WarmDonker else Color.Transparent,
                    modifier = Modifier.clickable { onSelect(option) }) {
                    Text(option, style = Typography.labelMedium,
                        color = if (isActive) Color.White else KledingColors.MutedGrijs,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp))
                }
            }
        }
    }
}

@Composable fun StatBox(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.background(KledingColors.KoperLicht, RoundedCornerShape(12.dp)).padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = Typography.headlineMedium.copy(fontSize = 20.sp))
        Text(label, style = Typography.bodySmall)
    }
}

@Composable fun SettingSectionHeader(title: String) {
    Text(title, style = Typography.headlineSmall.copy(fontSize = 20.sp), modifier = Modifier.padding(top = 24.dp, bottom = 12.dp))
}

@Composable fun SettingRow(title: String, trailing: @Composable (() -> Unit)? = null, onClick: (() -> Unit)? = null) {
    Row(modifier = Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable { onClick() } else Modifier).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = Typography.bodyMedium)
        when {
            trailing != null -> trailing()
            onClick != null -> Icon(Icons.Default.ChevronRight, null, tint = KledingColors.KoperAccent)
        }
    }
}

@Composable fun StepperButton(symbol: String, onClick: () -> Unit) {
    Surface(color = KledingColors.WarmDonker, shape = RoundedCornerShape(4.dp),
        modifier = Modifier.size(28.dp).clickable { onClick() }) {
        Box(contentAlignment = Alignment.Center) { Text(symbol, color = Color.White, style = Typography.labelMedium) }
    }
}
