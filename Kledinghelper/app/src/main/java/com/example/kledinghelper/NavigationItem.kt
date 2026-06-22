package com.example.kledinghelper

sealed class Screen(val route: String) {
    object Home            : Screen("home")
    object Kledingkast     : Screen("kledingkast")
    object NieuwItem       : Screen("nieuw_item")
    object Instellingen    : Screen("instellingen")
    object ProfielBewerken : Screen("profiel_bewerken")
    object Categorieen     : Screen("categorieen")
    object Zichtbaarheid   : Screen("zichtbaarheid")
    object Favorites       : Screen("favorites")
    object Add             : Screen("nieuw_item")
    object Closet          : Screen("kledingkast")
    object Settings        : Screen("instellingen")

    object ItemDetail : Screen("item_detail/{itemId}") {
        fun createRoute(itemId: String) = "item_detail/$itemId"
    }
    object EditItem : Screen("edit_item/{itemId}") {
        fun createRoute(itemId: String) = "edit_item/$itemId"
    }
    object ImageDetail : Screen("item_detail/{itemId}") {
        fun createRoute(itemId: String) = "item_detail/$itemId"
    }
    object OutfitDetail : Screen("outfit_detail/{outfitId}") {
        fun createRoute(outfitId: String) = "outfit_detail/$outfitId"
    }
    object AddDetails : Screen("add_details/{imageUri}") {
        fun createRoute(imageUri: String) = "add_details/$imageUri"
    }
}

object NavigationItem {
    val Home = Screen.Home; val Kledingkast = Screen.Kledingkast
    val NieuwItem = Screen.NieuwItem; val Instellingen = Screen.Instellingen
    val ProfielBewerken = Screen.ProfielBewerken; val Categorieen = Screen.Categorieen
    val Zichtbaarheid = Screen.Zichtbaarheid; val Favorites = Screen.Favorites
    val Add = Screen.Add; val Closet = Screen.Closet; val Settings = Screen.Settings
    val ItemDetail = Screen.ItemDetail; val ImageDetail = Screen.ImageDetail
    val OutfitDetail = Screen.OutfitDetail; val AddDetails = Screen.AddDetails
    val EditItem = Screen.EditItem
}
