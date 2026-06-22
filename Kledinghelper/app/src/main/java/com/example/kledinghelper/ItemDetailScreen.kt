package com.example.kledinghelper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kledinghelper.ui.theme.KledingColors
import com.example.kledinghelper.ui.theme.Typography
import com.example.kledinghelper.viewmodel.ClosetViewModel

@Composable
fun ItemDetailScreen(itemId: String?, closetViewModel: ClosetViewModel, navController: NavController) {
    val items by closetViewModel.clothingItems.collectAsState()
    val item = remember(items, itemId) { itemId?.let { closetViewModel.getItemById(it) } }

    Column(modifier = Modifier.fillMaxSize().background(KledingColors.WarmCreme)
        .verticalScroll(rememberScrollState()).padding(bottom = 80.dp)) {

        // TopBar
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Outlined.ArrowBack, null, tint = KledingColors.WarmDonker)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { item?.let { navController.navigate(Screen.EditItem.createRoute(it.id)) } }) {
                    Icon(Icons.Outlined.Edit, null, tint = KledingColors.WarmDonker)
                }
                IconButton(onClick = { item?.let { closetViewModel.toggleFavorite(it.id) } }) {
                    Icon(
                        imageVector = if (item?.isFavorite == true) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = if (item?.isFavorite == true) KledingColors.KoperAccent else KledingColors.WarmDonker
                    )
                }
            }
        }

        // Hero image
        Surface(modifier = Modifier.fillMaxWidth().height(280.dp).padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp), color = KledingColors.Wit) {
            AsyncImage(model = item?.imagePath, contentDescription = null, contentScale = ContentScale.Fit,
                modifier = Modifier.padding(8.dp))
        }

        // Info
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(item?.title ?: "Onbekend item", style = Typography.headlineLarge)
            Text("${item?.type ?: "T-shirt"}", style = Typography.bodyMedium.copy(color = KledingColors.MutedGrijs))
            Spacer(Modifier.height(12.dp))

            // Sterren (rating placeholder = 4)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(5) { i ->
                    Icon(Icons.Default.Star, null,
                        tint = if (i < 4) KledingColors.Goud else KledingColors.Goud.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.height(12.dp))

            // Seizoen chips (echt uit item data)
            val seasons = item?.season?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: listOf("Alle")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                seasons.forEach { label ->
                    Surface(shape = RoundedCornerShape(20.dp), color = KledingColors.KoperLicht) {
                        Text(label, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = Typography.labelMedium.copy(color = KledingColors.KoperAccent))
                    }
                }
            }
        }
    }
}
