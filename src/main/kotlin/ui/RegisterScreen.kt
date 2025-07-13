package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import repository.UserRepository
import model.User
import repository.RepositoryResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(onRegisterSuccess: (User) -> Unit, goToLogin: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(32.dp)) {
        Text("Registro de Usuario", style = MaterialTheme.typography.h4)

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colors.error)
        }

        Button(
            onClick = {
                error = "" // Clear errors
                when {
                    username.isBlank() -> error = "El nombre de usuario no puede estar vacío."
                    password.isBlank() -> error = "La contraseña no puede estar vacía."
                    password != confirmPassword -> error = "Las contraseñas no coinciden"
                    else -> {
                        coroutineScope.launch(Dispatchers.IO) { // Execute registration on IO thread
                            when (val result = UserRepository.register(username, password)) {
                                is RepositoryResult.Success -> {
                                    if (result.data != null) {
                                        launch(Dispatchers.Main) { onRegisterSuccess(result.data) }
                                    } else {
                                        launch(Dispatchers.Main) { error = "El usuario ya existe o hubo un error." }
                                    }
                                }
                                is RepositoryResult.Error -> {
                                    launch(Dispatchers.Main) { error = "Error al registrar: ${result.message}" }
                                }
                            }
                        }
                    }
                }
            },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Registrarse")
        }

        TextButton(onClick = goToLogin) {
            Text("¿Ya tienes cuenta? Iniciar sesión")
        }
    }
}