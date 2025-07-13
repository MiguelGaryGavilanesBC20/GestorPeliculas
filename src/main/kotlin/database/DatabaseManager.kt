package database

import java.sql.Connection
import java.sql.DriverManager

object DatabaseManager {
    private const val URL = "jdbc:mysql://localhost:3306/gestor_peliculas"
    private const val USER = "root"
    private const val PASSWORD = "12345"

    init {
        Class.forName("com.mysql.cj.jdbc.Driver")
    }

    fun getConnection(): Connection {
        return DriverManager.getConnection(URL, USER, PASSWORD)
    }
}
