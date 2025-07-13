package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import java.awt.FileDialog
import java.awt.Frame
import repository.DirectorRepository
import model.Director
import repository.RepositoryResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import utils.tryLoadImage

@Composable
fun AddDirectorScreen(
    onDirectorAdded: () -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf<String?>(null) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var error by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope() // Get a CoroutineScope

    fun openFileDialog(): String? {
        val dialog = FileDialog(Frame(), "Selecciona una foto", FileDialog.LOAD)
        dialog.isVisible = true
        return if (dialog.file != null) "${dialog.directory}${dialog.file}" else null
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
        Text("Agregar Nuevo Director", style = MaterialTheme.typography.h5)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del Director") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            val path = openFileDialog()
            if (path != null) {
                imagePath = path
                coroutineScope.launch(Dispatchers.IO) { // Load image on IO thread
                    val loadedBitmap = tryLoadImage(path)
                    if (loadedBitmap != null) {
                        imageBitmap = loadedBitmap
                        error = "" // Clear previous errors if successful
                    } else {
                        error = "Error al cargar la imagen. Asegúrate de que es un formato válido."
                    }
                }
            }
        }) {
            Text("Seleccionar Foto")
        }

        Spacer(Modifier.height(8.dp))

        imageBitmap?.let {
            Image(bitmap = it, contentDescription = "Foto", modifier = Modifier.size(150.dp))
        }

        Spacer(Modifier.height(16.dp))

        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colors.error)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {
                error = "" // Clear errors before attempting to save
                if (name.isBlank()) {
                    error = "El nombre no puede estar vacío"
                    return@Button
                }
                val director = Director(0, name, imagePath)
                coroutineScope.launch(Dispatchers.IO) { // Execute insert on IO thread
                    when (val result = DirectorRepository.insert(director)) {
                        is RepositoryResult.Success -> {
                            if (result.data) {
                                launch(Dispatchers.Main) { onDirectorAdded() } // Navigate on Main thread
                            } else {
                                launch(Dispatchers.Main) { error = "No se pudo insertar el director." }
                            }
                        }
                        is RepositoryResult.Error -> {
                            launch(Dispatchers.Main) { error = "Error al guardar: ${result.message}" }
                        }
                    }
                }
            }) {
                Text("Guardar")
            }

            OutlinedButton(onClick = onCancel) {
                Text("Cancelar")
            }
        }
    }
}