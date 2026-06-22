package com.example.kledinghelper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kledinghelper.ui.theme.KledingColors
import com.example.kledinghelper.ui.theme.Typography
import com.example.kledinghelper.viewmodel.CategoriesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorieenScreen(navController: NavController, categoriesViewModel: CategoriesViewModel) {
    val types by categoriesViewModel.types.collectAsState()
    val dresscodes by categoriesViewModel.dresscodes.collectAsState()

    var newType by remember { mutableStateOf("") }
    var newDresscode by remember { mutableStateOf("") }
    var editingType by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var editingDresscode by remember { mutableStateOf<Pair<Int, String>?>(null) }

    // Edit dialogs
    editingType?.let { (index, value) ->
        var editVal by remember(index) { mutableStateOf(value) }
        AlertDialog(onDismissRequest = { editingType = null },
            title = { Text("Type bewerken", style = Typography.headlineSmall) },
            text = { OutlinedTextField(value = editVal, onValueChange = { editVal = it }, singleLine = true,
                colors = fieldColors()) },
            confirmButton = { TextButton(onClick = { categoriesViewModel.updateType(index, editVal); editingType = null }) { Text("Opslaan", color = KledingColors.KoperAccent) } },
            dismissButton = { TextButton(onClick = { editingType = null }) { Text("Annuleren") } },
            containerColor = KledingColors.WarmCreme)
    }
    editingDresscode?.let { (index, value) ->
        var editVal by remember(index) { mutableStateOf(value) }
        AlertDialog(onDismissRequest = { editingDresscode = null },
            title = { Text("Dresscode bewerken", style = Typography.headlineSmall) },
            text = { OutlinedTextField(value = editVal, onValueChange = { editVal = it }, singleLine = true,
                colors = fieldColors()) },
            confirmButton = { TextButton(onClick = { categoriesViewModel.updateDresscode(index, editVal); editingDresscode = null }) { Text("Opslaan", color = KledingColors.KoperAccent) } },
            dismissButton = { TextButton(onClick = { editingDresscode = null }) { Text("Annuleren") } },
            containerColor = KledingColors.WarmCreme)
    }

    Column(modifier = Modifier.fillMaxSize().background(KledingColors.WarmCreme)
        .verticalScroll(rememberScrollState()).padding(bottom = 80.dp)) {

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Outlined.ArrowBack, null, tint = KledingColors.KoperAccent)
            }
            Text("Categorieën", style = Typography.headlineMedium, modifier = Modifier.padding(start = 4.dp))
        }

        // Types sectie
        CategorySection(
            sectionTitle = "Types",
            items = types,
            newValue = newType,
            onNewValueChange = { newType = it },
            onAdd = { categoriesViewModel.addType(newType); newType = "" },
            onEdit = { i, v -> editingType = i to v },
            onDelete = { categoriesViewModel.removeType(it) }
        )

        Spacer(Modifier.height(24.dp))

        // Dresscodes sectie
        CategorySection(
            sectionTitle = "Dresscodes",
            items = dresscodes,
            newValue = newDresscode,
            onNewValueChange = { newDresscode = it },
            onAdd = { categoriesViewModel.addDresscode(newDresscode); newDresscode = "" },
            onEdit = { i, v -> editingDresscode = i to v },
            onDelete = { categoriesViewModel.removeDresscode(it) }
        )
    }
}

@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = KledingColors.WarmDonker,
    unfocusedTextColor = KledingColors.WarmDonker,
    focusedBorderColor = KledingColors.KoperAccent,
    unfocusedBorderColor = Color.Transparent,
    unfocusedContainerColor = KledingColors.Wit,
    focusedContainerColor = KledingColors.Wit
)

@Composable
fun CategorySection(
    sectionTitle: String,
    items: List<String>,
    newValue: String,
    onNewValueChange: (String) -> Unit,
    onAdd: () -> Unit,
    onEdit: (Int, String) -> Unit,
    onDelete: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(sectionTitle, style = Typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        // Invoerveld met + icoon erin (frame 301)
        OutlinedTextField(
            value = newValue, onValueChange = onNewValueChange,
            placeholder = { Text("Nieuwe $sectionTitle", color = KledingColors.MutedGrijs) },
            leadingIcon = {
                Icon(Icons.Outlined.Add, null, tint = KledingColors.MutedGrijs,
                    modifier = Modifier.clickable { if (newValue.isNotBlank()) onAdd() })
            },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = fieldColors()
        )

        Spacer(Modifier.height(12.dp))

        Surface(color = KledingColors.Wit, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column {
                items.forEachIndexed { index, name ->
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(name, style = Typography.bodyMedium, modifier = Modifier.weight(1f))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Edit — donker vlak
                            Box(modifier = Modifier.size(36.dp)
                                .background(KledingColors.WarmDonker, RoundedCornerShape(8.dp))
                                .clickable { onEdit(index, name) },
                                contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Edit, null, tint = KledingColors.KoperAccent, modifier = Modifier.size(18.dp))
                            }
                            // Delete — licht vlak met donker icoon (als in screenshot)
                            Box(modifier = Modifier.size(36.dp)
                                .background(KledingColors.KoperLicht, RoundedCornerShape(8.dp))
                                .clickable { onDelete(index) },
                                contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Delete, null, tint = KledingColors.WarmDonker, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                    if (index < items.size - 1) HorizontalDivider(color = Color(0xFFF5F5F5))
                }
            }
        }
    }
}
