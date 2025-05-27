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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Пункты бокового меню
private val drawerItems = listOf(
    DrawerItem("Управление контентом", Icons.Default.Settings),
    DrawerItem("Активные заказы", Icons.Default.ListAlt),
    DrawerItem("Аналитика и отчёты", Icons.Default.Assessment),
    DrawerItem("Профиль", Icons.Default.Person)
)

data class DrawerItem(val title: String, val icon: ImageVector)

data class OrderItem(val name: String, val price: Double, val quantity: Int, val details: List<String>)

data class Order(
    val number: String,
    val items: List<OrderItem>,
    val deliveryDateTime: LocalDateTime,
    var status: String,
    val address: String,
    val comment: String
) {
    val totalPrice: Double get() = items.sumOf { it.price * it.quantity }
}

val statusOptions = listOf(
    "В обработке",
    "Принят в работу",
    "Собирается",
    "Передан в доставку",
    "Завершён"
)

@Composable
fun OrdersScreen() {
    var selectedItem by remember { mutableStateOf(drawerItems[1]) }
    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Генерация тестовых заказов
    val orders = remember {
//        List(10) { idx ->
//            val daysOffset = (idx / 3).toLong()
//            val hour = 9 + (idx % 3) * 3
//            val items = listOf(
//                OrderItem("Роза", 100.0, (idx % 3) + 1),
//                OrderItem("Ландыш", 120.0, (idx % 2) + 1),
//                OrderItem("Тюльпан", 80.0, (idx % 4) + 1)
//            ).take((idx % 3) + 1)
//            Order(
//                number = "#${1000 + idx}",
//                items = items,
//                deliveryDateTime = LocalDateTime.now()
//                    .plusDays(daysOffset)
//                    .withHour(hour)
//                    .withMinute(0)
//                    .withSecond(0),
//                status = statusOptions[idx % statusOptions.size],
//                address = "ул. Ленина, д.${idx + 1}",
//                comment = "Комментарий к заказу ${idx + 1}"
//            )
//        }
        List(1) { idx ->
            val items = listOf(
                OrderItem("Букет \"Дельфиниумы\"", 2500.0, 1, listOf("3x Дельфиниум", "Розовая упаковка")),
                OrderItem("Настраиваемый букет №1", 1350.0, 1, listOf("5x Красная роза", "4х Белая роза", "Белая бумага", "Красная лента"))
            )
            Order(
                number = "#${1000 + idx}",
                items = items,
                deliveryDateTime = LocalDateTime.of(2025, 4, 25, 14, 30),
                status = statusOptions[idx % statusOptions.size],
                address = "г. Ярославль, ул. Гагарина, д.11",
                comment = "Комментарий к заказу"
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
                    "Меню",
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
                            selectedOrder = null
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
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Меню")
                        }
                    },
                    title = { Text(selectedItem.title, fontSize = 20.sp) }
                )
            }
        ) { innerPadding ->
            Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when (selectedItem.title) {
                    "Активные заказы" -> {
                        if (selectedOrder == null) {
                            OrdersList(groupedOrders) { order ->
                                selectedOrder = order
                            }
                        } else {
                            OrderDetailScreen(
                                order = selectedOrder!!,
                                onStatusChange = { selectedOrder!!.status = it },
                                onBack = { selectedOrder = null }
                            )
                        }
                    }
                    "Управление контентом" -> {
                        ContentManagementScreen()
                    }
                    "Аналитика и отчёты" -> {
                        AnalyticsScreen()
                    }
                    "Профиль" -> {
                        Text("Экран профиля", modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }
}

@Composable
fun OrdersList(
    groupedOrders: Map<LocalDate, List<Order>>,
    onOrderClick: (Order) -> Unit
) {
    val dateFmt = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val timeFmt = DateTimeFormatter.ofPattern("HH:mm")

    LazyColumn(
        state = rememberLazyListState(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        groupedOrders.forEach { (date, ordersOnDate) ->
            stickyHeader {
                Text(
                    date.format(dateFmt),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = 4.dp)
                )
            }
            items(ordersOnDate) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOrderClick(order) }
                ) {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(order.number, style = MaterialTheme.typography.titleMedium)
                        Text("Состав: ${order.items.sumOf { it.quantity }} позиций")
                        Text("Время доставки: ${order.deliveryDateTime.format(timeFmt)}")
                        Text("Статус: ${order.status}")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrdersScreenPreview() {
    OrdersScreen()
}