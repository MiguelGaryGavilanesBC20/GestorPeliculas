import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.User
import ui.*

enum class Screen {
    LOGIN, REGISTER, MAIN
    // ADD_MOVIE and ADD_DIRECTOR are now handled as dialogs within MainScreen,
    // so they are no longer top-level navigation screens in this enum.
}

fun main() = application {
    var currentUser by remember { mutableStateOf<User?>(null) }
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }

    Window(onCloseRequest = ::exitApplication, title = "Gestor de Películas") {
        MaterialTheme {
            when (currentScreen) {
                Screen.LOGIN -> LoginScreen(
                    onLoginSuccess = {
                        currentUser = it
                        currentScreen = Screen.MAIN
                    },
                    goToRegister = { currentScreen = Screen.REGISTER }
                )

                Screen.REGISTER -> RegisterScreen(
                    onRegisterSuccess = {
                        currentUser = it
                        currentScreen = Screen.MAIN
                    },
                    goToLogin = { currentScreen = Screen.LOGIN }
                )

                Screen.MAIN -> MainScreen(
                    user = currentUser!!,
                    onAddMovie = { /* Lógica de añadir película manejada por el diálogo en MainScreen */ },
                    onAddDirector = { /* Lógica de añadir director manejada por el diálogo en MainScreen */ }
                )
            }
        }
    }
}