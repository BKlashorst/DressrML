package com.example.kledinghelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kledinghelper.ui.theme.KledingColors
import com.example.kledinghelper.ui.theme.KledingHelperTheme
import com.example.kledinghelper.viewmodel.CategoriesViewModel
import com.example.kledinghelper.viewmodel.ClosetViewModel
import com.example.kledinghelper.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { KledingHelperTheme { AppNavigation() } }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val weatherViewModel: WeatherViewModel = viewModel()
    val closetViewModel: ClosetViewModel = viewModel()
    val categoriesViewModel: CategoriesViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Bottom nav zichtbaar op alle schermen waar Figma het toont
    val showBottomNav = currentRoute in setOf(
        Screen.Home.route, Screen.Kledingkast.route, Screen.NieuwItem.route,
        Screen.Favorites.route, Screen.Instellingen.route,
        Screen.ItemDetail.route   // Frame_286 toont ook bottom nav
    )

    val selectedIndex = when (currentRoute) {
        Screen.Home.route, Screen.Kledingkast.route -> 0
        Screen.NieuwItem.route -> 1
        Screen.Favorites.route -> 2
        Screen.Instellingen.route -> 3
        else -> -1
    }

    Box(modifier = Modifier.fillMaxSize().background(KledingColors.WarmCreme)) {
        Scaffold(containerColor = KledingColors.WarmCreme, bottomBar = {}) { innerPadding ->
            NavHost(navController, startDestination = Screen.Home.route, Modifier.padding(innerPadding)) {
                composable(Screen.Home.route) { HomeScreen(navController, weatherViewModel, closetViewModel) }
                composable(Screen.Kledingkast.route) { KledingkastScreen(closetViewModel, categoriesViewModel, navController) }
                composable(Screen.NieuwItem.route) { NieuwItemScreen(closetViewModel, categoriesViewModel, navController) }
                composable(Screen.Favorites.route) { FavoritesScreen(closetViewModel, navController) }
                composable(Screen.ItemDetail.route) { back ->
                    ItemDetailScreen(back.arguments?.getString("itemId"), closetViewModel, navController)
                }
                composable(Screen.EditItem.route) { back ->
                    ItemEditScreen(back.arguments?.getString("itemId"), closetViewModel, categoriesViewModel, navController)
                }
                composable(Screen.OutfitDetail.route) { back ->
                    OutfitDetailScreen(back.arguments?.getString("outfitId"), closetViewModel, navController)
                }
                composable(Screen.AddDetails.route) { back ->
                    val enc = back.arguments?.getString("imageUri") ?: ""
                    val uri = android.net.Uri.parse(java.net.URLDecoder.decode(enc, "UTF-8"))
                    AddDetailsScreen(uri, closetViewModel, navController)
                }
                composable(Screen.Instellingen.route) { InstellingenScreen(navController, weatherViewModel, closetViewModel) }
                composable(Screen.ProfielBewerken.route) { ProfielBewerkenScreen(navController) }
                composable(Screen.Categorieen.route) { CategorieenScreen(navController, categoriesViewModel) }
                composable(Screen.Zichtbaarheid.route) { ZichtbaarheidScreen(navController) }
            }
        }

        if (showBottomNav) {
            Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding()) {
                KledingBottomNav(
                    selectedItem = if (selectedIndex >= 0) selectedIndex else 0,
                    onItemSelected = { index ->
                        val target = when (index) {
                            0 -> Screen.Home.route; 1 -> Screen.NieuwItem.route
                            2 -> Screen.Favorites.route; 3 -> Screen.Instellingen.route
                            else -> Screen.Home.route
                        }
                        navController.navigate(target) {
                            // Standaard Google bottom-nav patroon
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}
