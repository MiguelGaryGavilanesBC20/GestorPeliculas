package model

data class Movie(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val portadaUrl: String?,
    val ranking: Int,
    val directorId: Int? = null // Nuevo campo para relacionar con director
)

