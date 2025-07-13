package repository

import database.DatabaseManager
import model.Director
import java.sql.SQLException

object DirectorRepository {
    fun getAll(): RepositoryResult<List<Director>> {
        val list = mutableListOf<Director>()
        val sql = "SELECT * FROM directores"
        return try {
            DatabaseManager.getConnection().use { conn ->
                val stmt = conn.prepareStatement(sql)
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    list.add(
                        Director(
                            id = rs.getInt("id"),
                            nombre = rs.getString("nombre"),
                            fotoUrl = rs.getString("foto_url")
                        )
                    )
                }
                RepositoryResult.Success(list)
            }
        } catch (e: SQLException) {
            RepositoryResult.Error(e, "Error al obtener directores: ${e.message}")
        }
    }

    fun insert(director: Director): RepositoryResult<Boolean> {
        val sql = "INSERT INTO directores (nombre, foto_url) VALUES (?, ?)"
        return try {
            DatabaseManager.getConnection().use { conn ->
                val stmt = conn.prepareStatement(sql)
                stmt.setString(1, director.nombre)
                stmt.setString(2, director.fotoUrl)
                val rowsAffected = stmt.executeUpdate()
                RepositoryResult.Success(rowsAffected > 0)
            }
        } catch (e: SQLException) {
            RepositoryResult.Error(e, "Error al insertar el director: ${e.message}")
        }
    }

    // NUEVA FUNCIÓN: Eliminar un director por ID
    fun delete(id: Int): RepositoryResult<Boolean> {
        val sql = "DELETE FROM directores WHERE id = ?"
        return try {
            DatabaseManager.getConnection().use { conn ->
                val stmt = conn.prepareStatement(sql)
                stmt.setInt(1, id)
                val rowsAffected = stmt.executeUpdate()
                RepositoryResult.Success(rowsAffected > 0)
            }
        } catch (e: SQLException) {
            RepositoryResult.Error(e, "Error al eliminar el director: ${e.message}")
        }
    }
}