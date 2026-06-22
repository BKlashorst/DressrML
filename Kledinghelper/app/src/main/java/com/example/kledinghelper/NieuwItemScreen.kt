package com.example.kledinghelper

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kledinghelper.ui.theme.KledingColors
import com.example.kledinghelper.ui.theme.Typography
import com.example.kledinghelper.viewmodel.CategoriesViewModel
import com.example.kledinghelper.viewmodel.ClosetViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NieuwItemScreen(closetViewModel: ClosetViewModel, categoriesViewModel: CategoriesViewModel, navController: NavController) {
    var name by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }
    var selectedSeasons by remember { mutableStateOf(setOf<String>()) }
    var selectedType by remember { mutableStateOf("T-shirt") }
    var selectedSize by remember { mutableStateOf("L") }
    var selectedDresscodes by remember { mutableStateOf(setOf<String>()) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val types by categoriesViewModel.types.collectAsState()
    val dresscodes by categoriesViewModel.dresscodes.collectAsState()

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) photoUri = uri
    }
    val sizes = listOf("XS", "S", "M", "L", "XL")

    Column(modifier = Modifier.fillMaxSize().background(KledingColors.WarmCreme)
        .verticalScroll(rememberScrollState()).padding(bottom = 80.dp)) {
        Text("Nieuw item", style = Typography.headlineLarge,
            modifier = Modifier.padding(top = 24.dp, start = 20.dp, end = 20.dp))

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))

            // Foto + Naam + Rating
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp))
                    .background(KledingColors.KoperLicht).clickable { photoLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center) {
                    if (photoUri != null) AsyncImage(model = photoUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    else Icon(Icons.Outlined.PhotoCamera, null, tint = KledingColors.KoperAccent, modifier = Modifier.size(32.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(value = name, onValueChange = { name = it },
                        placeholder = { Text("Naam item", color = KledingColors.MutedGrijs) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        shape = RoundedCornerShape(8.dp), colors = fieldColors())
                    Spacer(Modifier.height(6.dp))
                    Text("Rating", style = Typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        repeat(5) { i ->
                            Icon(Icons.Default.Star, null,
                                tint = if (i < rating) KledingColors.Goud else KledingColors.Goud.copy(alpha = 0.3f),
                                modifier = Modifier.size(24.dp).clickable { rating = i + 1 })
                        }
                    }
                }
            }

            ChipSection("Seizoen", KledingColors.KoperAccent) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Lente" to R.drawable.ic_lente, "Zomer" to R.drawable.ic_zomer,
                        "Herfst" to R.drawable.ic_herfst, "Winter" to R.drawable.ic_winter)
                        .forEach { (label, icon) ->
                            KledingChip(label, selectedSeasons.contains(label),
                                onClick = { selectedSeasons = selectedSeasons.toggle(label) },
                                icon = painterResource(icon), modifier = Modifier.weight(1f))
                        }
                }
            }

            ChipSection("Type", KledingColors.KoperAccent) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(types) { type ->
                        KledingChip(type, selectedType == type, onClick = { selectedType = type })
                    }
                }
            }

            ChipSection("Maat", KledingColors.WarmDonker) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    sizes.forEach { size ->
                        KledingChip(size, selectedSize == size, onClick = { selectedSize = size }, modifier = Modifier.weight(1f))
                    }
                }
            }

            ChipSection("Dresscode", KledingColors.WarmDonker) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    dresscodes.forEach { code ->
                        KledingChip(code, selectedDresscodes.contains(code), onClick = { selectedDresscodes = selectedDresscodes.toggle(code) })
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            Button(onClick = {
                if (name.isNotBlank() && photoUri != null) {
                    closetViewModel.saveItem(photoUri!!, name, "", selectedType, "Onbekend",
                        selectedSeasons.joinToString(",").ifBlank { "Alle" })
                    navController.popBackStack()
                }
            }, enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KledingColors.WarmDonker),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Opslaan", style = Typography.labelMedium, color = KledingColors.Wit) }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ChipSection(label: String, labelColor: androidx.compose.ui.graphics.Color, content: @Composable () -> Unit) {
    Spacer(Modifier.height(24.dp))
    Text(label, style = Typography.titleLarge.copy(color = labelColor))
    Spacer(Modifier.height(8.dp))
    content()
}

private fun <T> Set<T>.toggle(item: T): Set<T> = if (contains(item)) this - item else this + item
