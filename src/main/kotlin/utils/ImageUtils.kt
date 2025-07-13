package utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

fun tryLoadImage(path: String?): ImageBitmap? {
    return try {
        if (path.isNullOrBlank()) return null
        FileInputStream(path).use {
            loadImageBitmap(it)
        }
    } catch (e: FileNotFoundException) {
        System.err.println("Imagen no encontrada en la ruta: $path. Error: ${e.message}")
        null
    } catch (e: IOException) {
        System.err.println("Error de I/O al cargar la imagen: $path. Error: ${e.message}")
        null
    } catch (e: Exception) {
        System.err.println("Error desconocido al cargar la imagen: $path. Error: ${e.message}")
        e.printStackTrace()
        null
    }
}