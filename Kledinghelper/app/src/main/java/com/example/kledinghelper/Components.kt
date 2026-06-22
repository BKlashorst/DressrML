package com.example.kledinghelper

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.example.kledinghelper.ui.theme.KledingColors
import com.example.kledinghelper.ui.theme.PlusJakartaSans
import com.example.kledinghelper.ui.theme.Typography

@Composable
fun KledingBottomNav(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = KledingColors.Wit,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(Icons.Outlined.Home, Icons.Outlined.Add, Icons.Outlined.FavoriteBorder, Icons.Outlined.Settings)
                    .forEachIndexed { index, icon ->
                        IconButton(onClick = { onItemSelected(index) }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (index == selectedItem) KledingColors.KoperAccent else KledingColors.WarmDonker,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
            }
        }
    }
}

@Composable
fun KledingChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: Painter? = null,
    modifier: Modifier = Modifier   // caller kan weight(1f) doorgeven
) {
    val bg    = if (selected) KledingColors.KoperAccent else KledingColors.Wit
    val textC = if (selected) KledingColors.Wit         else KledingColors.WarmDonker
    val border = if (selected) null else BorderStroke(1.dp, KledingColors.WarmDonker.copy(alpha = 0.3f))

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bg,
        border = border,
        modifier = modifier
            .clickable(onClick = onClick)
            .height(if (icon != null) 80.dp else 40.dp)
            .then(if (icon == null) Modifier.wrapContentWidth() else Modifier)
    ) {
        if (icon != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(painter = icon, contentDescription = label, tint = textC, modifier = Modifier.size(28.dp))
                Spacer(Modifier.height(4.dp))
                Text(label, style = Typography.labelMedium, color = textC, fontFamily = PlusJakartaSans)
            }
        } else {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(label, style = Typography.labelMedium, color = textC, fontFamily = PlusJakartaSans)
            }
        }
    }
}

@Composable
fun FilterChipCustom(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg    = if (selected) KledingColors.WarmDonker else KledingColors.Wit
    val textC = if (selected) KledingColors.Wit        else KledingColors.WarmDonker
    val border = if (selected) null else BorderStroke(1.dp, KledingColors.KoperAccent)

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bg,
        border = border,
        modifier = Modifier.clickable(onClick = onClick).height(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(label, style = Typography.labelMedium, color = textC, fontFamily = PlusJakartaSans)
        }
    }
}
