package com.example.kledinghelper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kledinghelper.ui.theme.KledingColors
import com.example.kledinghelper.ui.theme.Typography
import com.example.kledinghelper.viewmodel.ClosetViewModel

@Composable
fun FavoritesScreen(closetViewModel: ClosetViewModel, navController: NavController) {
    val favoriteItems by closetViewModel.favorites.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(KledingColors.WarmCreme).padding(bottom = 80.dp)) {
        Text("Favorieten", style = Typography.headlineLarge,
            modifier = Modifier.padding(top = 24.dp, start = 20.dp, end = 20.dp))
        Spacer(Modifier.height(16.dp))

        if (favoriteItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nog geen favorieten.\nTik op het ♡ bij een item.", style = Typography.bodyMedium, color = KledingColors.MutedGrijs)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(favoriteItems) { item ->
                    KledingItemKaart(item.imagePath) {
                        navController.navigate(Screen.ItemDetail.createRoute(item.id))
                    }
                }
            }
        }
    }
}
