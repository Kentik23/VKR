@file:OptIn(ExperimentalMaterial3Api::class)

package ru.vetochkaadmin.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    var expanded by remember { mutableStateOf(false) }
    var currentStatus by remember { mutableStateOf(order.status) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AppBar
        TopAppBar(
            title = { Text(text = "Заказ ${order.number}", fontSize = 20.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                }
            }
        )

        // Состав заказа
        Text(text = "Состав заказа:", style = MaterialTheme.typography.titleMedium)
        order.items.forEach { item ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${item.name} x${item.quantity}")
                Text(text = "${item.price} ₽")
            }
        }

        // Итоговая сумма
        Text(
            text = "Итоговая цена: ${order.totalPrice} ₽",
            style = MaterialTheme.typography.titleMedium
        )

        // Адрес и время доставки
        Text(text = "Адрес доставки: ${order.address}")
        Text(text = "Дата и время доставки: ${order.deliveryDateTime.format(dateTimeFormatter)}")

        // Комментарий клиента
        if (order.comment.isNotBlank()) {
            Text(text = "Комментарий: ${order.comment}")
        }

        // Выбор статуса: кликается весь TextField
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = currentStatus,
                onValueChange = {},
                readOnly = true,
                label = { Text("Статус") },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Открыть список статусов")
                },
                modifier = Modifier.fillMaxWidth()
            )
            // Оверлей для обработки клика
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                statusOptions.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status) },
                        onClick = {
                            currentStatus = status
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Кнопка сохранить
        Button(
            onClick = { onStatusChange(currentStatus) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Сохранить")
        }
    }
}
