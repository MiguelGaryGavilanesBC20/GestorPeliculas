package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import model.User
import repository.UserRepository
import androidx.compose.ui.Alignment
import repository.RepositoryResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    goToRegister: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Iniciar sesión", style = MaterialTheme.typography.h4)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        if (error.isNotEmpty()) {
            Text(
                error,
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                error = "" // Clear errors
                coroutineScope.launch(Dispatchers.IO) { // Execute login on IO thread
                    when (val result = UserRepository.login(username, password)) {
                        is RepositoryResult.Success -> {
                            if (result.data != null) {
                                launch(Dispatchers.Main) { onLoginSuccess(result.data) }
                            } else {
                                launch(Dispatchers.Main) { error = "Credenciales incorrectas" }
                            }
                        }
                        is RepositoryResult.Error -> {
                            launch(Dispatchers.Main) { error = "Error de conexión: ${result.message}" }
                        }
                    }
                }
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            Text("Entrar")
        }

        TextButton(
            onClick = goToRegister,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}