package ru.vetochkaadmin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.vetochkaadmin.R

// Форма овальной (эллипс) обрезки
private val EllipseShape = GenericShape { size, _ ->
    addOval(Rect(Offset.Zero, size))
}

@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Логотип магазина с эллиптической обрезкой
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Логотип Веточка",
            modifier = Modifier
                .size(width = 150.dp, height = 100.dp)
                .clip(EllipseShape)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Название магазина
        Text(
            text = "Веточка",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Подзаголовок
        Text(
            text = "Панель управления магазином",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Поля ввода
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Электронная почта") },
            placeholder = { Text("пример@vetochka.ru") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            placeholder = { Text("Введите пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Кнопка входа — цвет берётся из темы (primary)
        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Войти")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // Для предпросмотра используем тестовую тему с серым primary
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    ) {
        LoginScreen { _, _ -> }
    }
}
