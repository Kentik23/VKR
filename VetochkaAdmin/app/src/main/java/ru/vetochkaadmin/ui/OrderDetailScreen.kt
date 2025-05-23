@file:OptIn(ExperimentalMaterial3Api::class)

package ru.vetochkaadmin.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.format.DateTimeFormatter

@Composable
fun OrderDetailScreen(
    order: Order,
    onStatusChange: (String) -> Unit,
    onBack: () -> Unit
) {
    val fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    var expanded by remember { mutableStateOf(false) }
    var currentStatus by remember { mutableStateOf(order.status) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TopAppBar(
            title = { Text("Заказ ${order.number}", fontSize = 20.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
            }
        )

        Text("Состав заказа:", style = MaterialTheme.typography.titleMedium)
        order.items.forEach { item ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${item.name} x${item.quantity}")
                Text("${item.price} ₽")
            }
        }
        Text("Итоговая цена: ${order.totalPrice} ₽", style = MaterialTheme.typography.titleMedium)
        Text("Адрес доставки: ${order.address}")
        Text("Дата и время доставки: ${order.deliveryDateTime.format(fmt)}")
        if (order.comment.isNotBlank()) Text("Комментарий: ${order.comment}")

        Box(Modifier.fillMaxWidth()) {
            TextField(
                value = currentStatus,
                onValueChange = {},
                readOnly = true,
                label = { Text("Статус") },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Box(Modifier.matchParentSize().clickable { expanded = true })
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                statusOptions.forEach { status ->
                    DropdownMenuItem(text = { Text(status) }, onClick = {
                        currentStatus = status; expanded = false
                    })
                }
            }
        }

        Spacer(Modifier.weight(1f))
        Button(onClick = { onStatusChange(currentStatus) }, Modifier.fillMaxWidth()) {
            Text("Сохранить")
        }
    }
}