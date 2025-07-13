package repository

import database.DatabaseManager
import model.Movie
import java.sql.SQLException

object MovieRepository {
    fun getAll(): RepositoryResult<List<Movie>> {
        val list = mutableListOf<Movie>()
        val sql = "SELECT * FROM peliculas"
        return try {
            DatabaseManager.getConnection().use { conn ->
                val stmt = conn.prepareStatement(sql)
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    list.add(
                        Movie(
                            id = rs.getInt("id"),
                            titulo = rs.getString("titulo"),
                            descripcion = rs.getString("descripcion") ?: "",
                            portadaUrl = rs.getString("portada_url"),
                            ranking = rs.getInt("ranking"),
                            directorId = rs.getInt("director_id").takeIf { !rs.wasNull() } // Handle null in DB
                        )
                    )
                }
                RepositoryResult.Success(list)
            }
        } catch (e: SQLException) {
            RepositoryResult.Error(e, "Error al obtener películas: ${e.message}")
        }
    }

    fun insert(movie: Movie): RepositoryResult<Boolean> {
        val sql = """
        INSERT INTO peliculas (titulo, descripcion, portada_url, ranking, director_id)
        VALUES (?, ?, ?, ?, ?)
    """.trimIndent()

        return try {
            DatabaseManager.getConnection().use { conn ->
                val stmt = conn.prepareStatement(sql)
                stmt.setString(1, movie.titulo)
                stmt.setString(2, movie.descripcion)
                stmt.setString(3, movie.portadaUrl)
                stmt.setInt(4, movie.ranking)
                if (movie.directorId != null) {
                    stmt.setInt(5, movie.directorId)
                } else {
                    stmt.setNull(5, java.sql.Types.INTEGER)
                }
                val rowsAffected = stmt.executeUpdate()
                RepositoryResult.Success(rowsAffected > 0)
            }
        } catch (e: SQLException) {
            RepositoryResult.Error(e, "Error al insertar la película: ${e.message}")
        }
    }

    fun delete(movieId: Int): RepositoryResult<Boolean> {
        val sql = "DELETE FROM peliculas WHERE id = ?"
        return try {
            DatabaseManager.getConnection().use { conn ->
                val stmt = conn.prepareStatement(sql)
                stmt.setInt(1, movieId)
                RepositoryResult.Success(stmt.executeUpdate() > 0)
            }
        } catch (e: SQLException) {
            RepositoryResult.Error(e, "Error al eliminar la película: ${e.message}")
        }
    }

    fun update(movie: Movie): RepositoryResult<Boolean> {
        val sql = """
        UPDATE peliculas
        SET titulo = ?, descripcion = ?, portada_url = ?, ranking = ?, director_id = ?
        WHERE id = ?
    """.trimIndent()

        return try {
            DatabaseManager.getConnection().use { conn ->
                val stmt = conn.prepareStatement(sql)
                stmt.setString(1, movie.titulo)
                stmt.setString(2, movie.descripcion)
                stmt.setString(3, movie.portadaUrl)
                stmt.setInt(4, movie.ranking)
                if (movie.directorId != null) {
                    stmt.setInt(5, movie.directorId)
                } else {
                    stmt.setNull(5, java.sql.Types.INTEGER)
                }
                stmt.setInt(6, movie.id)

                RepositoryResult.Success(stmt.executeUpdate() > 0)
            }
        } catch (e: SQLException) {
            RepositoryResult.Error(e, "Error al actualizar la película: ${e.message}")
        }
    }
}