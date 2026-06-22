package com.example.kledinghelper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kledinghelper.ui.theme.KledingColors
import com.example.kledinghelper.ui.theme.Typography

@Composable
fun ZichtbaarheidScreen(navController: NavController) {
    // mutableStateMapOf triggert recompose bij in-place mutatie
    val visibility = remember {
        mutableStateMapOf(
            "T-shirts" to true, "Trui" to true, "Colbert" to true,
            "Overhemd" to true, "Overshirt" to true, "Broek" to true,
            "Winter" to false, "Jas" to false
        )
    }

    val visible = visibility.entries.filter { it.value }.map { it.key }.sorted()
    val hidden  = visibility.entries.filter { !it.value }.map { it.key }.sorted()

    Column(modifier = Modifier.fillMaxSize().background(KledingColors.WarmCreme)
        .verticalScroll(rememberScrollState()).padding(bottom = 80.dp)) {

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Outlined.ArrowBack, null, tint = KledingColors.KoperAccent)
            }
            Text("Zichtbaarheid", style = Typography.headlineMedium, modifier = Modifier.padding(start = 4.dp))
        }

        Surface(color = KledingColors.KoperLicht, shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
            Text("Verborgen categorieën verschijnen niet meer in je kledingkast of in outfit-suggesties. Handig voor seizoenskleding.",
                modifier = Modifier.padding(12.dp),
                style = Typography.bodyMedium.copy(color = KledingColors.KoperAccent, fontSize = 13.sp))
        }

        Spacer(Modifier.height(24.dp))
        Text("Zichtbaar (${visible.size})", style = Typography.headlineSmall.copy(fontSize = 18.sp),
            modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(Modifier.height(12.dp))

        Surface(color = KledingColors.Wit, shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
            Column {
                visible.forEachIndexed { index, name ->
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(name, style = Typography.bodyMedium)
                        Switch(
                            checked = true,
                            onCheckedChange = { visibility[name] = false },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = KledingColors.WarmDonker,
                                checkedThumbColor = Color.White
                            )
                        )
                    }
                    if (index < visible.size - 1) HorizontalDivider(color = Color(0xFFF5F5F5))
                }
            }
        }

        if (hidden.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            Text("Verborgen (${hidden.size})",
                style = Typography.headlineSmall.copy(fontSize = 18.sp, color = KledingColors.MutedGrijs),
                modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(12.dp))

            Surface(color = KledingColors.Wit.copy(alpha = 0.7f), shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
                Column {
                    hidden.forEachIndexed { index, name ->
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(name, style = Typography.bodyMedium.copy(color = KledingColors.MutedGrijs))
                            Switch(
                                checked = false,
                                onCheckedChange = { visibility[name] = true },
                                colors = SwitchDefaults.colors(
                                    uncheckedTrackColor = Color.LightGray,
                                    uncheckedThumbColor = KledingColors.WarmDonker
                                )
                            )
                        }
                        if (index < hidden.size - 1) HorizontalDivider(color = Color(0xFFF5F5F5))
                    }
                }
            }
        }
    }
}
