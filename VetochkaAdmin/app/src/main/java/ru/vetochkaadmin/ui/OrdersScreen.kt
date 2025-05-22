@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package ru.vetochkaadmin.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Пункты бокового меню
private val drawerItems = listOf(
    DrawerItem("Управление контентом", Icons.Default.Settings),
    DrawerItem("Активные заказы", Icons.Default.ListAlt),
    DrawerItem("Аналитика и отчёты", Icons.Default.Assessment),
    DrawerItem("Профиль", Icons.Default.Person)
)

// Статусы заказа (стали публичными для использования в OrderDetailScreen)
val statusOptions = listOf(
    "Принят в работу",
    "Собирается",
    "Передан в доставку",
    "Завершён"
)

data class DrawerItem(val title: String, val icon: ImageVector)

data class Order(
    val number: String,
    val items: List<OrderItem>,
    val deliveryDateTime: LocalDateTime,
    var status: String,
    val address: String,
    val comment: String
) {
    val totalPrice: Double
        get() = items.sumOf { it.price * it.quantity }
}

data class OrderItem(
    val name: String,
    val price: Double,
    val quantity: Int
)

@Composable
fun OrdersScreen(onItemSelected: (DrawerItem) -> Unit = {}) {
    var selectedItem by remember { mutableStateOf(drawerItems[1]) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedOrder by remember { mutableStateOf<Order?>(null) }

    // Генерация тестовых заказов
    val orders = remember {
        List(10) { index ->
            val daysOffset = (index / 3).toLong()
            val hour = 9 + (index % 3) * 3
            val items = listOf(
                OrderItem("Роза", 100.0, (index % 3) + 1),
                OrderItem("Ландыш", 120.0, (index % 2) + 1),
                OrderItem("Тюльпан", 80.0, (index % 4) + 1)
            ).take((index % 3) + 1)
            Order(
                number = "#${1000 + index}",
                items = items,
                deliveryDateTime = LocalDateTime.now()
                    .plusDays(daysOffset)
                    .withHour(hour)
                    .withMinute(0)
                    .withSecond(0),
                status = statusOptions[index % statusOptions.size],
                address = "ул. Ленина, д.${index + 1}",
                comment = "Комментарий к заказу ${index + 1}"
            )
        }
    }

    // Группировка по дате доставки
    val groupedOrders = remember(orders) {
        orders.groupBy { it.deliveryDateTime.toLocalDate() }
            .toSortedMap()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Меню",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(Modifier.height(8.dp))
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = item == selectedItem,
                        onClick = {
                            selectedItem = item
                            onItemSelected(item)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (selectedOrder == null) {
                    CenterAlignedTopAppBar(
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Открыть меню")
                            }
                        },
                        title = { Text("Активные заказы", fontSize = 20.sp) }
                    )
                } else {
                    CenterAlignedTopAppBar(
                        navigationIcon = {
                            IconButton(onClick = { selectedOrder = null }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                            }
                        },
                        title = { Text("Заказ ${selectedOrder!!.number}", fontSize = 20.sp) }
                    )
                }
            }
        ) { innerPadding ->
            if (selectedOrder == null) {
                // Список заказов
                LazyColumn(
                    state = rememberLazyListState(),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

                    groupedOrders.forEach { (date, ordersOnDate) ->
                        stickyHeader {
                            Text(
                                text = date.format(dateFormatter),
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(vertical = 4.dp)
                            )
                        }
                        items(ordersOnDate) { order ->
                            Card(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedOrder = order }
                            ) {
                                Column(
                                    Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(order.number, style = MaterialTheme.typography.titleMedium)
                                    Text("Состав: ${order.items.sumOf { it.quantity }} позиций")
                                    Text("Время доставки: ${order.deliveryDateTime.format(timeFormatter)}")
                                    Text("Статус: ${order.status}")
                                }
                            }
                        }
                    }
                }
            } else {
                // Экран деталей заказа
                OrderDetailScreen(
                    order = selectedOrder!!,
                    onStatusChange = { newStatus -> selectedOrder!!.status = newStatus },
                    onBack = { selectedOrder = null }
                )
            }
        }
    }
}
