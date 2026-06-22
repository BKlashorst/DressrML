package com.example.kledinghelper

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kledinghelper.ui.theme.KledingColors
import com.example.kledinghelper.ui.theme.Typography
import java.io.File

@Composable
fun ProfielBewerkenScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE) }

    var name by remember { mutableStateOf(prefs.getString("user_name", "Bram") ?: "Bram") }
    // Laad opgeslagen bestandspad (niet content-URI)
    var photoPath by remember { mutableStateOf(prefs.getString("user_photo_path", null)) }

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            // Kopieer naar interne opslag zodat het permanent is
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val destFile = File(context.filesDir, "profile_photo.jpg")
                inputStream?.use { input -> destFile.outputStream().use { input.copyTo(it) } }
                photoPath = destFile.absolutePath
            } catch (_: Exception) {}
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(KledingColors.WarmCreme).padding(bottom = 80.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Outlined.ArrowBack, null, tint = KledingColors.KoperAccent)
            }
            Text("Profiel bewerken", style = Typography.headlineMedium, modifier = Modifier.padding(start = 4.dp))
        }

        Column(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(96.dp).clickable { photoLauncher.launch("image/*") }) {
                if (photoPath != null) {
                    AsyncImage(model = File(photoPath!!), contentDescription = null, contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape))
                } else {
                    Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color.LightGray))
                }
                Box(modifier = Modifier.size(28.dp).align(Alignment.BottomEnd)
                    .background(KledingColors.KoperAccent, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Edit, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Klik om aan te passen", style = Typography.bodySmall)
        }

        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = name, onValueChange = { name = it },
            placeholder = { Text("Naam", color = KledingColors.MutedGrijs) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            shape = RoundedCornerShape(8.dp), singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = KledingColors.WarmDonker),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = KledingColors.Wit, focusedContainerColor = KledingColors.Wit,
                unfocusedBorderColor = Color.Transparent, focusedBorderColor = KledingColors.KoperAccent)
        )

        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                prefs.edit().putString("user_name", name).putString("user_photo_path", photoPath).apply()
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = KledingColors.WarmDonker),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Opslaan", style = Typography.labelMedium, color = KledingColors.Wit) }
        Spacer(Modifier.height(24.dp))
    }
}
