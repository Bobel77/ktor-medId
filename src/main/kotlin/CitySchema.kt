package com.example

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.Statement

@Serializable
data class MedId(val persoId: String, )
class MedIdService(private val connection: Connection) {
    companion object {
        private const val CREATE_TABLE_MEDID =
            "CREATE TABLE IF NOT EXISTS medID (ID SERIAL PRIMARY KEY, persoID VARCHAR(255));"
        private const val SELECT_MEDID_BY_ID = "SELECT persoID FROM medID WHERE id = ?"
     /*   private const val INSERT_CITY = "INSERT INTO cities (name, population) VALUES (?, ?)"
        private const val UPDATE_CITY = "UPDATE cities SET name = ?, population = ? WHERE id = ?"
        private const val DELETE_CITY = "DELETE FROM cities WHERE id = ?"*/

    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_MEDID)
    }

    private var newCityId = 0


    // Read a city
    suspend fun read(id: Int): MedId = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_MEDID_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val name = resultSet.getString("persoID")
            return@withContext MedId(name)
        } else {
            throw Exception("Record not found")
        }
    }
}

