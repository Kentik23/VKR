@file:OptIn(ExperimentalMaterial3Api::class)

package ru.vetochkaadmin.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** ------------ модель ------------ **/
data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val description: String
)

@Composable
fun ManageProductsScreen() {
    /* Категории */
    val categories = listOf("Букеты", "Цветы", "Сопутствующие товары")
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var catExpanded by remember { mutableStateOf(false) }

    /* Cписок товаров */
    val products = remember {
        mutableStateListOf(
            Product(1, "Букет \"Дельфиниумы\"", "Букеты", 250.0, "3 дельфиниума + розовая упаковка"),
            Product(2, "Розы красные (5)", "Цветы", 120.0, "5 красных роз сорта Freedom"),
            Product(3, "Комбинированный букет", "Букеты", 500.0, "5 красных роз, 5 белых роз, белая упаковка, красная лента"),
            Product(4, "Конфеты ассорти", "Сопутствующие товары", 200.0, "Подарочная коробка 200 г")
        )
    }

    /* состояния диалога */
    var showDialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Product?>(null) }
    var dlgName by remember { mutableStateOf("") }
    var dlgCategory by remember { mutableStateOf(categories.first()) }
    var dlgPrice by remember { mutableStateOf("") }
    var dlgDesc by remember { mutableStateOf("") }
    var dlgCatExpanded by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        /** Выбор категории **/
        ExposedDropdownMenuBox(
            expanded = catExpanded,
            onExpandedChange = { catExpanded = !catExpanded }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Категория") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(catExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()   // теперь кликабельно по стандарту
            )
            ExposedDropdownMenu(
                expanded = catExpanded,
                onDismissRequest = { catExpanded = false }
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            selectedCategory = cat
                            catExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        /** Список товаров **/
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(products.filter { it.category == selectedCategory }) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(product.name)
                            Row {
                                IconButton(onClick = {
                                    editing = product
                                    dlgName = product.name
                                    dlgCategory = product.category
                                    dlgPrice = product.price.toString()
                                    dlgDesc = product.description
                                    showDialog = true
                                }) { Icon(Icons.Default.Edit, contentDescription = null) }
                                IconButton(onClick = { products.remove(product) }) {
                                    Icon(Icons.Default.Delete, contentDescription = null)
                                }
                            }
                        }
                        if (product.description.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(product.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        /** Кнопка добавить */
        Button(
            onClick = {
                editing = null
                dlgName = ""
                dlgCategory = selectedCategory
                dlgPrice = ""
                dlgDesc = ""
                showDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Добавить товар")
        }
    }

    /** Диалог **/
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (editing == null) "Новый товар" else "Редактировать товар") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dlgName,
                        onValueChange = { dlgName = it },
                        label = { Text("Название") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // category dropdown in dialog
                    ExposedDropdownMenuBox(
                        expanded = dlgCatExpanded,
                        onExpandedChange = { dlgCatExpanded = !dlgCatExpanded }
                    ) {
                        OutlinedTextField(
                            value = dlgCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Категория") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dlgCatExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = dlgCatExpanded,
                            onDismissRequest = { dlgCatExpanded = false }
                        ) {
                            categories.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c) },
                                    onClick = { dlgCategory = c; dlgCatExpanded = false }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = dlgPrice,
                        onValueChange = { dlgPrice = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Цена") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dlgDesc,
                        onValueChange = { dlgDesc = it },
                        label = { Text("Описание") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val priceVal = dlgPrice.toDoubleOrNull() ?: 0.0
                    if (editing == null) {
                        val newId = (products.maxOfOrNull { it.id } ?: 0) + 1
                        products.add(Product(newId, dlgName, dlgCategory, priceVal, dlgDesc))
                    } else {
                        val idx = products.indexOf(editing)
                        products[idx] = editing!!.copy(
                            name = dlgName,
                            category = dlgCategory,
                            price = priceVal,
                            description = dlgDesc
                        )
                    }
                    showDialog = false
                }) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Отмена") }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ManageProductsPreview() {
    ManageProductsScreen()
}
