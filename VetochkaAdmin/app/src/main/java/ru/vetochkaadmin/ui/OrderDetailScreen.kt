@file:OptIn(ExperimentalMaterial3Api::class)

package ru.vetochkaadmin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.vetochkaadmin.R
import java.time.format.DateTimeFormatter

@Composable
fun OrderDetailScreen(
    order: Order,
    onStatusChange: (String) -> Unit,
    onBack: () -> Unit
) {
    // Выбранный элемент для просмотра детали
    var selectedItem by remember { mutableStateOf<OrderItem?>(null) }
    val fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    var expanded by remember { mutableStateOf(false) }
    var currentStatus by remember { mutableStateOf(order.status) }

    if (selectedItem != null) {
        // Показ экрана детали товара
        ProductDetailScreen(item = selectedItem!!, onBack = { selectedItem = null })
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopAppBar(
            title = { Text("Заказ ${order.number}", fontSize = 20.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                }
            }
        )

        Text("Состав заказа:", style = MaterialTheme.typography.titleMedium)
        // Список позиций: кликабельные
        order.items.forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedItem = item }
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(item.name)
                    Text(" x${item.quantity} ")
                    Text("${item.price} ₽")
                }
            }
        }

        Text("Итоговая цена: ${order.totalPrice} ₽", style = MaterialTheme.typography.titleMedium)
        Text("Адрес доставки: ${order.address}")
        Text("Дата и время доставки: ${order.deliveryDateTime.format(fmt)}")
        if (order.comment.isNotBlank()) Text("Комментарий: ${order.comment}")

        // Статус
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

@Composable
fun ProductDetailScreen(
    item: OrderItem,
    onBack: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopAppBar(
            title = { Text(item.name, fontSize = 20.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                }
            }
        )
        // Фото товара (заглушка)
        Image(
            painter = painterResource(id = R.drawable.placeholder),
            contentDescription = item.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        // Состав букета
        Text("Состав:", style = MaterialTheme.typography.titleMedium)
        item.details.forEach { detail ->
            Text(detail, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// В OrderItem добавьте поле деталей
// data class OrderItem(val name: String, val price: Double, val quantity: Int, val details: List<String>)

@Preview(showBackground = true)
@Composable
fun ProductDetailScreenPreview() {
    val sample = OrderItem(
        name = "Дельфиниумы",
        price = 250.0,
        quantity = 3,
        details = listOf("3x Дельфиниум", "Розовая упаковка")
    )
    ProductDetailScreen(item = sample, onBack = {})
}
