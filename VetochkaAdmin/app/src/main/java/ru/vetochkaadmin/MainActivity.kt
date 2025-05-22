package ru.vetochkaadmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ru.vetochkaadmin.ui.LoginScreen
import ru.vetochkaadmin.ui.theme.VetochkaAdminTheme

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.vetochkaadmin.ui.OrdersScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VetochkaAdminTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreen { email, password ->
                            // TODO: ваша валидация
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                    composable("main") {
                        OrdersScreen()
                    }
                }
            }
        }
    }
}