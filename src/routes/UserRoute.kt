package com.codingwithjks.routes


import com.codingwithjks.auth.JwtService
import com.codingwithjks.auth.MySession
import com.codingwithjks.repository.NotesRepository
import com.codingwithjks.repository.UserRepository
import com.codingwithjks.util.UserData
import com.codingwithjks.util.UserResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*


fun Route.userRoutes(
    userDb: UserRepository,
    notesDb: NotesRepository,
    jwt: JwtService,
    hash: (String) -> String
) {

    post("/v1/signup") {
        val parameter = call.receive<Parameters>()
        val name = parameter["name"] ?: return@post call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )
        val email = parameter["email"] ?: return@post call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )
        val password = parameter["password"] ?: return@post call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )

        val hashPassword = hash(password)
        try {
            val user = userDb.createUser(name, email, hashPassword)
            user?.userId?.let { userId ->
                call.sessions.set(MySession(userId))
                userDb.getAllUser()
                    .forEach {
                        if (it.email == email) {
                            call.respond(
                                status = HttpStatusCode.BadRequest,
                                UserResponse(
                                    false, "email already exits..",
                                    null
                                )
                            )
                        } else {
                            call.respond(
                                status = HttpStatusCode.Created,
                                UserResponse(
                                    true, "user $name created successfully",
                                    arrayListOf(UserData(userId, name, email, jwt.generateToken(user))),
                                )
                            )
                        }
                    }
            }
        } catch (e: Throwable) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                UserResponse(
                    false, "cannot create user",
                    null
                )
            )
        }
    }

    post("/v1/login") {
        val parameter = call.receive<Parameters>()

        val email = parameter["email"] ?: return@post call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )
        val password = parameter["password"] ?: return@post call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )

        val hashPassword = hash(password)
        try {
            val user = userDb.findUserByEmail(email)
            user?.userId?.let {
                if (user.password == hashPassword) {
                    call.sessions.set(MySession(it))
                    call.respond(
                        status = HttpStatusCode.Created,
                        UserResponse(
                            true, "user ${user.name} logged in successfully",
                            arrayListOf(UserData(it, null, email, jwt.generateToken(user))),
                        )
                    )
                } else {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        UserResponse(
                            false, "password does not matched",
                            null
                        )
                    )
                }
            }
        } catch (e: Throwable) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                UserResponse(
                    false, "login failed",
                    null
                )
            )
        }
    }

    delete("/v1/user") {
        val user = call.sessions.get<MySession>()?.let {
            userDb.findUser(it.userId)
        }

        if (user == null) {
            call.respondText("problem getting user", status = HttpStatusCode.BadRequest)
        }

        try {
            user?.userId?.let { it1 -> notesDb.deleteAllNotes(it1) }
            val isDelete = user?.userId?.let { it1 -> userDb.deleteUser(it1) }
            if (isDelete != null) {
                if (isDelete > 0) {
                    call.respond(
                        UserResponse(
                            status = true,
                            "User ${user.name} deleted successfully",
                            null
                        )
                    )
                } else {
                    call.respond(
                        UserResponse(
                            status = false,
                            "User ${user.name} not deleted",
                            null
                        )
                    )
                }
            }
        } catch (e: Throwable) {

            call.respond(
                UserResponse(
                    status = false,
                    "User ${user?.name} not deleted",
                    null
                )
            )
        }

    }

    put("/v1/user") {
        val user = call.sessions.get<MySession>()?.let {
            userDb.findUser(it.userId)
        }
        val parameter = call.receive<Parameters>()
        val name = parameter["name"] ?: return@put call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )
        val email = parameter["email"] ?: return@put call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )
        val password = parameter["password"] ?: return@put call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )

        val hashPassword = hash(password)

        if (user == null) {
            call.respondText("problem getting user", status = HttpStatusCode.BadRequest)
        }
        try {
            val isUpdated = user?.userId?.let { it1 -> userDb.updateUser(it1, name, email, hashPassword) }
            if (isUpdated != null) {
                if (isUpdated > 0) {
                    call.respond(
                        UserResponse(
                            status = true,
                            "User updated",
                            null
                        )
                    )
                } else {
                    call.respond(
                        UserResponse(
                            status = false,
                            "user not updated",
                            null
                        )
                    )
                }
            }
        } catch (e: Throwable) {

            call.respond(
                UserResponse(
                    status = false,
                    "user not updated",
                    null
                )
            )
        }
    }

}