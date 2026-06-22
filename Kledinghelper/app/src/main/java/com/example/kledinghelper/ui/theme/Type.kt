package com.example.kledinghelper.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.kledinghelper.R

val CormorantGaramond = FontFamily(
    Font(R.font.cormorant_garamond_variable, FontWeight.Normal),
    Font(R.font.cormorant_garamond_variable, FontWeight.SemiBold),
    Font(R.font.cormorant_garamond_variable, FontWeight.Bold)
)

val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_variable, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_variable, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_variable, FontWeight.SemiBold)
)

val Typography = Typography(
    // Pagina-titels — SemiBold
    displayLarge   = TextStyle(fontFamily = CormorantGaramond, fontSize = 32.sp, fontWeight = FontWeight.SemiBold, color = KledingColors.WarmDonker),
    headlineLarge  = TextStyle(fontFamily = CormorantGaramond, fontSize = 28.sp, fontWeight = FontWeight.SemiBold, color = KledingColors.WarmDonker),
    headlineMedium = TextStyle(fontFamily = CormorantGaramond, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = KledingColors.WarmDonker),
    // Sectie-headers binnen pagina's — normaal gewicht
    headlineSmall  = TextStyle(fontFamily = CormorantGaramond, fontSize = 20.sp, fontWeight = FontWeight.Normal,   color = KledingColors.WarmDonker),
    titleLarge     = TextStyle(fontFamily = PlusJakartaSans,   fontSize = 16.sp, fontWeight = FontWeight.Medium,   color = KledingColors.WarmDonker),
    bodyMedium     = TextStyle(fontFamily = PlusJakartaSans,   fontSize = 14.sp, color = KledingColors.WarmDonker),
    bodySmall      = TextStyle(fontFamily = PlusJakartaSans,   fontSize = 12.sp, color = KledingColors.MutedGrijs),
    labelMedium    = TextStyle(fontFamily = PlusJakartaSans,   fontSize = 14.sp, fontWeight = FontWeight.Medium),
)
