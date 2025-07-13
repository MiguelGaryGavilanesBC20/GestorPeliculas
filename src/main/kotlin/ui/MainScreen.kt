package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.*
import repository.*
import utils.tryLoadImage
import androidx.compose.ui.window.Dialog

@Composable
fun MainScreen(
    user: User,
    onAddMovie: () -> Unit,
    onAddDirector: () -> Unit
) {
    var directors by remember { mutableStateOf(listOf<Director>()) }
    var movies by remember { mutableStateOf(listOf<Movie>()) }
    var showAddMovieDialog by remember { mutableStateOf(false) }
    var showAddDirectorDialog by remember { mutableStateOf(false) }
    var selectedMovieForInfo by remember { mutableStateOf<Movie?>(null) }
    var selectedMovieForEdit by remember { mutableStateOf<Movie?>(null) }
    var selectedDirectorForContextMenu by remember { mutableStateOf<Director?>(null) }
    var showMovieInfoDialog by remember { mutableStateOf(false) }
    var contextMenuOffset by remember { mutableStateOf(Offset.Zero) }
    var showContextMenu by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    fun loadData() {
        coroutineScope.launch(Dispatchers.IO) {
            errorMessage = ""

            when (val directorsResult = DirectorRepository.getAll()) {
                is RepositoryResult.Success -> {
                    directors = directorsResult.data
                }
                is RepositoryResult.Error -> {
                    launch(Dispatchers.Main) { errorMessage = "Error al cargar directores: ${directorsResult.message}" }
                }
            }
            when (val moviesResult = MovieRepository.getAll()) {
                is RepositoryResult.Success -> {
                    movies = moviesResult.data
                }
                is RepositoryResult.Error -> {
                    launch(Dispatchers.Main) { errorMessage = "Error al cargar películas: ${moviesResult.message}" }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .width(220.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF121212))
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Directores", color = Color.White, fontSize = 18.sp)
                    IconButton(onClick = { showAddDirectorDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Director", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = MaterialTheme.colors.error)
                }

                LazyColumn {
                    items(directors) { director ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .pointerInput(director.id) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            val event = awaitPointerEvent()
                                            if (event.type == PointerEventType.Press &&
                                                event.buttons.isSecondaryPressed &&
                                                !event.buttons.isPrimaryPressed
                                            ) {
                                                event.changes.forEach { it.consume() }
                                                contextMenuOffset = event.changes.first().position
                                                selectedDirectorForContextMenu = director
                                                selectedMovieForInfo = null
                                                selectedMovieForEdit = null
                                                showContextMenu = true
                                            }
                                        }
                                    }
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val img = remember(director.fotoUrl) { mutableStateOf<ImageBitmap?>(null) }
                            LaunchedEffect(director.fotoUrl) {
                                launch(Dispatchers.IO) {
                                    img.value = tryLoadImage(director.fotoUrl)
                                }
                            }

                            img.value?.let {
                                Image(
                                    bitmap = it,
                                    contentDescription = "Foto",
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                )
                            } ?: run {
                                Spacer(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray)
                                )
                            }
                            Text(director.nombre, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Películas", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Button(onClick = { showAddMovieDialog = true }) {
                        Text("Agregar Película")
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = MaterialTheme.colors.error)
                }

                movies.chunked(2).forEach { rowMovies ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowMovies.forEach { movie ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(280.dp)
                                    .pointerInput(movie.id) {
                                        awaitPointerEventScope {
                                            while (true) {
                                                val event = awaitPointerEvent()
                                                if (event.type == PointerEventType.Press &&
                                                    event.buttons.isSecondaryPressed &&
                                                    !event.buttons.isPrimaryPressed
                                                ) {
                                                    event.changes.forEach { it.consume() }
                                                    contextMenuOffset = event.changes.first().position
                                                    selectedMovieForInfo = movie
                                                    selectedMovieForEdit = null
                                                    selectedDirectorForContextMenu = null
                                                    showContextMenu = true
                                                }
                                            }
                                        }
                                    },
                                elevation = 6.dp
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    val img = remember(movie.portadaUrl) { mutableStateOf<ImageBitmap?>(null) }
                                    LaunchedEffect(movie.portadaUrl) {
                                        launch(Dispatchers.IO) {
                                            img.value = tryLoadImage(movie.portadaUrl)
                                        }
                                    }

                                    img.value?.let {
                                        Image(
                                            bitmap = it,
                                            contentDescription = "Portada",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(150.dp)
                                        )
                                    } ?: run {
                                        Spacer(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(150.dp)
                                                .background(Color.LightGray)
                                        )
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text(movie.titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Ranking: ${movie.ranking}%", fontSize = 14.sp)
                                }
                            }
                        }

                        if (rowMovies.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }

        if (showContextMenu) {
            if (selectedDirectorForContextMenu != null) {
                DropdownMenu(
                    expanded = true,
                    onDismissRequest = {
                        showContextMenu = false
                        selectedDirectorForContextMenu = null
                    },
                    offset = DpOffset(contextMenuOffset.x.dp, contextMenuOffset.y.dp)
                ) {
                    DropdownMenuItem(onClick = {
                        selectedDirectorForContextMenu?.let { directorToDelete ->
                            coroutineScope.launch(Dispatchers.IO) {
                                when (val result = DirectorRepository.delete(directorToDelete.id)) {
                                    is RepositoryResult.Success -> {
                                        if (result.data) {
                                            launch(Dispatchers.Main) {
                                                loadData()
                                                errorMessage = ""
                                            }
                                        } else {
                                            launch(Dispatchers.Main) { errorMessage = "No se pudo eliminar el director." }
                                        }
                                    }
                                    is RepositoryResult.Error -> {
                                        launch(Dispatchers.Main) { errorMessage = "Error al eliminar: ${result.message}" }
                                    }
                                }
                            }
                        }
                        showContextMenu = false
                        selectedDirectorForContextMenu = null
                    }) {
                        Text("Eliminar Director")
                    }
                }
            } else if (selectedMovieForInfo != null) {
                DropdownMenu(
                    expanded = true,
                    onDismissRequest = {
                        showContextMenu = false
                        selectedMovieForInfo = null
                        selectedMovieForEdit = null
                    },
                    offset = DpOffset(contextMenuOffset.x.dp, contextMenuOffset.y.dp)
                ) {
                    DropdownMenuItem(onClick = {
                        showMovieInfoDialog = true
                        showContextMenu = false
                    }) {
                        Text("Mostrar descripción")
                    }
                    DropdownMenuItem(onClick = {
                        selectedMovieForInfo?.let { movieToDelete ->
                            coroutineScope.launch(Dispatchers.IO) {
                                when (val result = MovieRepository.delete(movieToDelete.id)) {
                                    is RepositoryResult.Success -> {
                                        if (result.data) {
                                            launch(Dispatchers.Main) {
                                                loadData()
                                                errorMessage = ""
                                            }
                                        } else {
                                            launch(Dispatchers.Main) { errorMessage = "No se pudo eliminar la película." }
                                        }
                                    }
                                    is RepositoryResult.Error -> {
                                        launch(Dispatchers.Main) { errorMessage = "Error al eliminar: ${result.message}" }
                                    }
                                }
                            }
                        }
                        showContextMenu = false
                        selectedMovieForInfo = null
                        selectedMovieForEdit = null
                    }) {
                        Text("Eliminar película")
                    }
                    DropdownMenuItem(onClick = {
                        selectedMovieForEdit = selectedMovieForInfo
                        selectedMovieForInfo = null
                        showContextMenu = false
                    }) {
                        Text("Modificar película")
                    }
                }
            }
        }

        if (showMovieInfoDialog && selectedMovieForInfo != null) {
            AlertDialog(
                onDismissRequest = {
                    showMovieInfoDialog = false
                    selectedMovieForInfo = null
                },
                title = { Text(selectedMovieForInfo!!.titulo) },
                text = { Text(selectedMovieForInfo!!.descripcion) },
                confirmButton = {
                    Button(onClick = {
                        showMovieInfoDialog = false
                        selectedMovieForInfo = null
                    }) {
                        Text("Cerrar")
                    }
                }
            )
        }

        if (showAddMovieDialog) {
            Dialog(onCloseRequest = { showAddMovieDialog = false }) {
                Surface(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    AddMovieScreen(
                        onMovieAdded = {
                            loadData()
                            showAddMovieDialog = false
                            errorMessage = ""
                        },
                        onCancel = { showAddMovieDialog = false }
                    )
                }
            }
        }

        if (showAddDirectorDialog) {
            Dialog(onCloseRequest = { showAddDirectorDialog = false }) {
                Surface(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    AddDirectorScreen(
                        onDirectorAdded = {
                            loadData()
                            showAddDirectorDialog = false
                            errorMessage = ""
                        },
                        onCancel = { showAddDirectorDialog = false }
                    )
                }
            }
        }

        selectedMovieForEdit?.let { movieToEdit ->
            Dialog(onCloseRequest = { selectedMovieForEdit = null }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .heightIn(min = 400.dp, max = 700.dp) // Mejor presentación
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()) // Scroll
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
                        EditMovieScreen(
                            movie = movieToEdit,
                            onUpdate = {
                                loadData()
                                selectedMovieForEdit = null
                                errorMessage = ""
                            },
                            onCancel = { selectedMovieForEdit = null }
                        )
                    }
                }
            }
        }
    }
}