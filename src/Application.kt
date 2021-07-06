package com.codingwithjks

import com.codingwithjks.auth.JwtService
import com.codingwithjks.auth.MySession
import com.codingwithjks.auth.hash
import com.codingwithjks.repository.DatabaseFactory
import com.codingwithjks.repository.NotesRepository
import com.codingwithjks.repository.UserRepository
import com.codingwithjks.routes.noteRoutes
import com.codingwithjks.routes.userRoutes
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.sessions.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.gson.*
import io.ktor.features.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    DatabaseFactory.init()
    val userDb = UserRepository()
    val notesDb = NotesRepository()
    val jwt = JwtService()
    val hashPassword = { s: String -> hash(s) }
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    install(Authentication) {
        jwt("jwt") {
            verifier(jwt.verifier)
            realm = "Todo Server"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asInt()
                val user = userDb.findUser(claimString)
                user
            }
        }
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        userRoutes(userDb, notesDb, jwt, hashPassword)
        noteRoutes(userDb,notesDb)
    }
}


