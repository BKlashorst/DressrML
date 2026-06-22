package com.example.kledinghelper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kledinghelper.ui.theme.KledingColors
import com.example.kledinghelper.ui.theme.Typography
import com.example.kledinghelper.viewmodel.ClosetViewModel

@Composable
fun OutfitDetailScreen(
    outfitId: String?,
    closetViewModel: ClosetViewModel,
    navController: NavController
) {
    val outfit = outfitId?.let { closetViewModel.getOutfitById(it) }
    // In a real app, we would resolve itemIds to ClothingItem objects
    // For now, let's assume we can fetch them or just show the outfit photo
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KledingColors.WarmCreme)
            .padding(bottom = 80.dp)
    ) {
        // 1. TopBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = KledingColors.WarmDonker)
            }
            Text(
                text = outfit?.title ?: "Outfit",
                style = Typography.headlineLarge
            )
            IconButton(onClick = { /* Toggle Fav */ }) {
                Icon(Icons.Outlined.FavoriteBorder, contentDescription = null, tint = KledingColors.WarmDonker)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 2. Outfit-foto
            AsyncImage(
                model = outfit?.imagePath,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            // 3. "Gebruikte kleding"
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Gebruikte kleding", style = Typography.headlineSmall)
                Spacer(Modifier.height(12.dp))
                
                // Using a Box with fixed height or similar because LazyVerticalGrid inside Column is tricky
                // For simplicity in this implementation, we use a fixed number of items if possible
                // or just a Row/Column combination.
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Dummy items for preview as per spec
                    repeat(2) {
                        KledingItemKaart(
                            imagePath = outfit?.imagePath ?: "", // Placeholder
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}
