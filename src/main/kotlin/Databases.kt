package com.example

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.Connection
import java.sql.DriverManager
import kotlinx.css.*
import kotlinx.html.*

fun Application.configureDatabases() {
    val dbConnection: Connection = connectToPostgres(embedded = false)
    val medIdService = MedIdService(dbConnection)
    
    routing {

        get("id/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val medId = medIdService.read(id)
                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        title { +"Anders3 - Medikamentenausweis" }
                    }
                    body {
                        style = "text-align: center; margin: 0; padding: 0;"

                        p { +"Die Ausweis-ID: $id" }
                        p { +"Personalausweis-Chiffre: ${medId.persoId}" }
                        p { +"Die Chiffre setzt sich zusammen aus:" }

                        val contextPath = "/medausweis"
                        img(src = "$contextPath/static/images/AnnPerso2.png", alt = "back") {
                            style = "width: 50vw; height: auto; display: block; margin: 0 auto;"
                        }
                        img(src = "$contextPath/static/images/AnnPerso1.png", alt = "front") {
                            style = "width: 50vw; height: auto; display: block; margin: 0 auto;"
                        }

                    }
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

fun Application.connectToPostgres(embedded: Boolean): Connection {
    Class.forName("org.postgresql.Driver")
    if (embedded) {
        log.info("Using embedded H2 database for testing; replace this flag to use postgres")
        return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "root", "")
    } else {
        val url = environment.config.property("postgres.url").getString()
        log.info("Connecting to postgres database at $url")
        val user = environment.config.property("postgres.user").getString()
        val password = environment.config.property("postgres.password").getString()

        return DriverManager.getConnection(url, user, password)
    }
}

