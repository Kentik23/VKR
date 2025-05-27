@file:OptIn(ExperimentalMaterial3Api::class)

package ru.vetochkaadmin.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Данные о разделе
private data class Section(val title: String, val icon: ImageVector)

private val sections = listOf(
    Section("Товары", Icons.Default.LocalFlorist),
    Section("Категории", Icons.Default.List),
    Section("Склад", Icons.Default.Inventory),
    Section("Скидки и акции", Icons.Default.LocalOffer),
    Section("Настройки магазина", Icons.Default.Settings),
    Section("Пользователи и права", Icons.Default.People)
)

@Composable
fun ContentManagementScreen() {
    var selectedSection by remember { mutableStateOf<Section?>(null) }

    if (selectedSection == null) {
        SectionsList(onSectionClick = { selectedSection = it })
    } else {
        SectionDetailScreen(
            section = selectedSection!!,
            onBack = { selectedSection = null }
        )
    }
}

@Composable
private fun SectionsList(onSectionClick: (Section) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(sections) { section ->
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSectionClick(section) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = section.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionDetailScreen(section: Section, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text(section.title, fontSize = 20.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                }
            }
        )

        when (section.title) {
            "Товары" -> {
                // сразу показываем без лишнего отступа
                ManageProductsScreen()
            }
            else -> {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Здесь будет экран «${section.title}»",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContentManagementPreview() {
    ContentManagementScreen()
}
