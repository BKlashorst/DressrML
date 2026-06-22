package com.example.kledinghelper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kledinghelper.ui.theme.KledingColors
import com.example.kledinghelper.ui.theme.Typography
import com.example.kledinghelper.viewmodel.CategoriesViewModel
import com.example.kledinghelper.viewmodel.ClosetViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun KledingkastScreen(
    closetViewModel: ClosetViewModel,
    categoriesViewModel: CategoriesViewModel,   // nieuw
    navController: NavController
) {
    val items by closetViewModel.filteredItems.collectAsState()
    var showFilters by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val context = LocalContext.current
    val columns = remember {
        context.getSharedPreferences("AppSettings", android.content.Context.MODE_PRIVATE)
            .getInt("kledingkast_cols", 2)
    }

    Scaffold(containerColor = KledingColors.WarmCreme, topBar = {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Outlined.ArrowBack, null, tint = KledingColors.WarmDonker)
            }
            Text("Kledingkast", style = Typography.headlineLarge)
            Row {
                IconButton(onClick = { navController.navigate(Screen.NieuwItem.route) }) {
                    Icon(Icons.Outlined.Add, null, tint = KledingColors.WarmDonker)
                }
                IconButton(onClick = { showFilters = true }) {
                    Icon(Icons.Outlined.Tune, null, tint = KledingColors.WarmDonker)
                }
            }
        }
    }) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(items) { item ->
                KledingItemKaart(item.imagePath) { navController.navigate(Screen.ItemDetail.createRoute(item.id)) }
            }
        }

        if (showFilters) {
            ModalBottomSheet(
                onDismissRequest = { showFilters = false }, sheetState = sheetState,
                containerColor = KledingColors.WarmCreme,
                dragHandle = {
                    Box(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp).width(40.dp).height(4.dp)
                        .background(KledingColors.MutedGrijs.copy(alpha = 0.4f), RoundedCornerShape(2.dp)))
                },
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                FilterBottomSheetContent(closetViewModel, categoriesViewModel) { showFilters = false }
            }
        }
    }
}

@Composable
fun KledingItemKaart(imagePath: String, onClick: () -> Unit) {
    Surface(modifier = Modifier.aspectRatio(1f), shape = RoundedCornerShape(12.dp),
        color = KledingColors.Wit, shadowElevation = 2.dp, onClick = onClick) {
        AsyncImage(model = imagePath, contentDescription = null, contentScale = ContentScale.Fit,
            modifier = Modifier.padding(16.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheetContent(
    closetViewModel: ClosetViewModel,
    categoriesViewModel: CategoriesViewModel,   // nieuw — dynamische types
    onClose: () -> Unit
) {
    val selectedSeasons by closetViewModel.selectedSeasons.collectAsState()
    val selectedType by closetViewModel.selectedType.collectAsState()
    val seasons = listOf("Lente", "Zomer", "Herfst", "Winter")

    // Types komen nu uit CategoriesViewModel — zelfde lijst als bij toevoegen
    val types by categoriesViewModel.types.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
        Text("Filter", style = Typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        Text("Seizoen", style = Typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            seasons.forEach { season ->
                FilterChipCustom(season, selectedSeasons.contains(season)) {
                    closetViewModel.toggleSeasonFilter(season)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Type", style = Typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            types.forEach { type ->
                FilterChipCustom(type, selectedType == type) {
                    closetViewModel.setTypeFilter(if (selectedType == type) "Alle" else type)
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        Button(onClick = onClose, modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = KledingColors.WarmDonker),
            shape = RoundedCornerShape(12.dp)) {
            Text("Resultaten", style = Typography.labelMedium, color = KledingColors.Wit)
        }
    }
}
