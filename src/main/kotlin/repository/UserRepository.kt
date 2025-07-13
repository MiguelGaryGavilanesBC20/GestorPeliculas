package repository

import database.DatabaseManager
import model.User
import java.sql.SQLException

object UserRepository {

    // !!! WARNING: In a real application, use a SECURE HASHING LIBRARY (e.g., BCrypt) !!!
    // This is just to simulate the hashing concept.
    private fun hashPassword(plainPassword: String): String {
        // In a real project: return BCrypt.hashpw(plainPassword, BCrypt.gensalt())
        return plainPassword // SIMULATION: returns plain text password
    }

    private fun checkPassword(plainPassword: String, hashedPasswordFromDb: String): Boolean {
        // In a real project: return BCrypt.checkpw(plainPassword, hashedPasswordFromDb)
        return plainPassword == hashedPasswordFromDb // SIMULATION: compares plain text
    }
    // !!! END WARNING !!!


    fun login(username: String, password: String): RepositoryResult<User?> {
        // Assuming your 'usuarios' table has a 'password' column storing the (hashed) password
        val query = "SELECT id, username, password FROM usuarios WHERE username = ?"
        return try {
            DatabaseManager.getConnection().use { conn ->
                val stmt = conn.prepareStatement(query)
                stmt.setString(1, username)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    val hashedPasswordFromDb = rs.getString("password")
                    if (checkPassword(password, hashedPasswordFromDb)) {
                        RepositoryResult.Success(User(rs.getInt("id"), rs.getString("username")))
                    } else {
                        RepositoryResult.Success(null) // Incorrect password
                    }
                } else {
                    RepositoryResult.Success(null) // User not found
                }
            }
        } catch (e: SQLException) {
            RepositoryResult.Error(e, "Error de base de datos durante el login: ${e.message}")
        }
    }

    fun register(username: String, password: String): RepositoryResult<User?> {
        val checkQuery = "SELECT id FROM usuarios WHERE username = ?"
        val insertQuery = "INSERT INTO usuarios (username, password) VALUES (?, ?)" // Store the hash
        val getIdQuery = "SELECT * FROM usuarios WHERE username = ?" // To retrieve the newly created user

        return try {
            DatabaseManager.getConnection().use { conn ->
                // 1. Check if user already exists
                val checkStmt = conn.prepareStatement(checkQuery)
                checkStmt.setString(1, username)
                val checkRs = checkStmt.executeQuery()
                if (checkRs.next()) {
                    return RepositoryResult.Success(null) // User already exists
                }

                // 2. Hash the password
                val hashedPassword = hashPassword(password)

                // 3. Insert new user
                val insertStmt = conn.prepareStatement(insertQuery)
                insertStmt.setString(1, username)
                insertStmt.setString(2, hashedPassword) // Store the hashed password
                val rowsAffected = insertStmt.executeUpdate()

                if (rowsAffected > 0) {
                    // 4. Retrieve the newly registered user (to get their ID)
                    val getStmt = conn.prepareStatement(getIdQuery)
                    getStmt.setString(1, username)
                    val rs = getStmt.executeQuery()
                    return if (rs.next()) {
                        RepositoryResult.Success(User(rs.getInt("id"), rs.getString("username")))
                    } else {
                        // This case should ideally not happen if insertion was successful
                        RepositoryResult.Error(
                            IllegalStateException("Registered user not found"),
                            "Error al recuperar el usuario registrado."
                        )
                    }
                } else {
                    RepositoryResult.Error(
                        SQLException("No se pudo insertar el usuario"),
                        "Error desconocido al registrar el usuario."
                    )
                }
            }
        } catch (e: SQLException) {
            RepositoryResult.Error(e, "Error de base de datos durante el registro: ${e.message}")
        }
    }
}