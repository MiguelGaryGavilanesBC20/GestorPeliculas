package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import model.Director
import model.Movie
import repository.DirectorRepository
import repository.MovieRepository
import repository.RepositoryResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import utils.tryLoadImage
import java.awt.FileDialog
import java.awt.Frame

@Composable
fun EditMovieScreen(
    movie: Movie,
    onUpdate: () -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(movie.titulo) }
    var description by remember { mutableStateOf(movie.descripcion) }
    var rankingText by remember { mutableStateOf(movie.ranking.toString()) }
    var selectedDirector by remember { mutableStateOf<Director?>(null) }
    var directors by remember { mutableStateOf<List<Director>>(emptyList()) }
    var imagePath by remember { mutableStateOf(movie.portadaUrl) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Cargar directores y precargar director seleccionado y imagen
    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            when (val result = DirectorRepository.getAll()) {
                is RepositoryResult.Success -> {
                    directors = result.data
                    selectedDirector = directors.find { it.id == movie.directorId }
                }
                is RepositoryResult.Error -> {
                    errorMessage = "Error al cargar directores: ${result.message}"
                }
            }
            val loadedBitmap = tryLoadImage(imagePath)
            launch(Dispatchers.Main) {
                imageBitmap = loadedBitmap
            }
        }
    }

    fun openFileDialog(): String? {
        val dialog = FileDialog(Frame(), "Selecciona una imagen", FileDialog.LOAD)
        dialog.isVisible = true
        val files = dialog.files
        return if (files.isNotEmpty()) files[0].absolutePath else null
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
        Text("Editar Película", style = MaterialTheme.typography.h5)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Spacer(Modifier.height(8.dp))

        // Dropdown para seleccionar director
        Box {
            OutlinedTextField(
                value = selectedDirector?.nombre ?: "",
                onValueChange = {},
                label = { Text("Director") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                readOnly = true
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                directors.forEach { director ->
                    DropdownMenuItem(onClick = {
                        selectedDirector = director
                        expanded = false
                    }) {
                        Text(director.nombre)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = rankingText,
            onValueChange = { input ->
                if (input.all { it.isDigit() } && input.length <= 3) {
                    rankingText = input
                }
            },
            label = { Text("Ranking (0-100)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            val path = openFileDialog()
            if (path != null) {
                imagePath = path
                coroutineScope.launch(Dispatchers.IO) {
                    val loadedBitmap = tryLoadImage(path)
                    if (loadedBitmap != null) {
                        imageBitmap = loadedBitmap
                        errorMessage = ""
                    } else {
                        errorMessage = "Error al cargar la imagen. Asegúrate de que es un formato válido."
                    }
                }
            }
        }) {
            Text("Cambiar imagen de portada")
        }

        Spacer(Modifier.height(8.dp))

        imageBitmap?.let {
            androidx.compose.foundation.Image(
                bitmap = it,
                contentDescription = "Imagen portada",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = MaterialTheme.colors.error)
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {
                errorMessage = ""
                val ranking = rankingText.toIntOrNull()
                when {
                    title.isBlank() -> errorMessage = "El título es obligatorio"
                    selectedDirector == null -> errorMessage = "Debe seleccionar un director"
                    ranking == null || ranking !in 0..100 -> errorMessage = "Ranking debe estar entre 0 y 100"
                    imagePath.isNullOrBlank() -> errorMessage = "Debe cargar una imagen"
                    else -> {
                        val updatedMovie = movie.copy(
                            titulo = title,
                            descripcion = description,
                            ranking = ranking,
                            directorId = selectedDirector!!.id,
                            portadaUrl = imagePath!!
                        )
                        coroutineScope.launch(Dispatchers.IO) {
                            when (val result = MovieRepository.update(updatedMovie)) {
                                is RepositoryResult.Success -> {
                                    if (result.data) {
                                        launch(Dispatchers.Main) { onUpdate() }
                                    } else {
                                        launch(Dispatchers.Main) { errorMessage = "No se pudo actualizar la película." }
                                    }
                                }
                                is RepositoryResult.Error -> {
                                    launch(Dispatchers.Main) { errorMessage = "Error al actualizar: ${result.message}" }
                                }
                            }
                        }
                    }
                }
            }) {
                Text("Actualizar")
            }

            OutlinedButton(onClick = onCancel) {
                Text("Cancelar")
            }
        }
    }
}
