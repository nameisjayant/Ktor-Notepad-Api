package com.codingwithjks.routes


import com.codingwithjks.auth.MySession
import com.codingwithjks.data.model.Notes
import com.codingwithjks.repository.NotesRepository
import com.codingwithjks.repository.UserRepository
import com.codingwithjks.util.NoteResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Route.noteRoutes(
    userDb: UserRepository,
    noteDb: NotesRepository
) {
    authenticate("jwt") {
        post("/v1/notes") {
            val parameter = call.receive<Parameters>()
            val user = call.sessions.get<MySession>()?.let {
                userDb.findUser(it.userId)
            }
            if (user == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    NoteResponse(false, "problem getting user", null)
                )
            }
            val title = parameter["title"] ?: return@post call.respondText(
                "missing field",
                status = HttpStatusCode.Unauthorized
            )
            val note = parameter["note"] ?: return@post call.respondText(
                "missing field",
                status = HttpStatusCode.Unauthorized
            )
            val date = parameter["date"] ?: return@post call.respondText(
                "missing field",
                status = HttpStatusCode.Unauthorized
            )

            try {
                val currentNote = user?.userId?.let { it1 -> noteDb.createNote(it1, title, note, date.toLong()) }
                currentNote?.let {
                    call.respond(
                        status = HttpStatusCode.OK,
                        NoteResponse(
                            true, "Note Added",
                            arrayListOf(Notes(null, null, it.title, it.note, it.date))
                        )
                    )
                }
            } catch (e: Throwable) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    NoteResponse(
                        false, "Note not Added", null
                    )
                )
            }
        }
        get("/v1/notes") {
            val user = call.sessions.get<MySession>()?.let {
                userDb.findUser(it.userId)
            }
            if (user == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    NoteResponse(false, "problem getting user", null)
                )
            }
            try {
                val notes = user?.userId?.let { it1 -> noteDb.getAllNotes(it1) }
                if (notes?.isNotEmpty() == true) {
                    call.respond(
                        status = HttpStatusCode.OK,
                        NoteResponse(true, "done", notes)
                    )
                }else{
                    call.respond(
                        status = HttpStatusCode.OK,
                        NoteResponse(true, "No data found", notes)
                    )
                }
            } catch (e: Throwable) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    NoteResponse(false, "something went wrong", null)
                )
            }

        }
    }

    delete("/v1/notes/{id}") {
        val id = call.parameters["id"]
        val user = call.sessions.get<MySession>()?.let {
            userDb.findUser(it.userId)
        }
        if (user == null) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                NoteResponse(false, "problem getting user", null)
            )
        }

        try {
            val allNotes = user?.userId?.let { it1 -> noteDb.getAllNotes(it1) }
            allNotes?.forEach {
                if (it.id == id?.toInt()) {
                    val isDeleted = id?.toInt()?.let { it1 -> noteDb.deleteNote(it1) }
                    if (isDeleted != null) {
                        if (isDeleted > 0) {
                            call.respond(
                                status = HttpStatusCode.OK,
                                NoteResponse(true, "note deleted", null)
                            )
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                NoteResponse(false, "problem delete note", null)
            )
        }
    }

    delete("/v1/notes") {
        val user = call.sessions.get<MySession>()?.let {
            userDb.findUser(it.userId)
        }
        if (user == null) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                NoteResponse(false, "problem getting user", null)
            )
        }

        try {
            val isDeleted = user?.userId?.let { it1 -> noteDb.deleteAllNotes(it1) }
            if (isDeleted != null) {
                if (isDeleted > 0)
                    call.respond(
                        status = HttpStatusCode.OK,
                        NoteResponse(true, "All notes deleted", null)
                    )
            }
        } catch (e: Throwable) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                NoteResponse(false, "problem delete note", null)
            )
        }
    }

    put("/v1/notes/{id}") {
        val id = call.parameters["id"]
        val parameter = call.receive<Parameters>()
        val user = call.sessions.get<MySession>()?.let {
            userDb.findUser(it.userId)
        }
        if (user == null) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                NoteResponse(false, "problem getting user", null)
            )
        }
        val title = parameter["title"] ?: return@put call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )
        val note = parameter["note"] ?: return@put call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )
        val date = parameter["date"] ?: return@put call.respondText(
            "missing field",
            status = HttpStatusCode.Unauthorized
        )


        try {
            val allNotes = user?.userId?.let { it1 -> noteDb.getAllNotes(it1) }
            allNotes?.forEach {
                if (it.id == id?.toInt()) {
                    val isUpdated = id?.toInt()?.let { it1 -> noteDb.updateNote(it1, title, note, date.toLong()) }
                    if (isUpdated != null) {
                        if (isUpdated > 0) {
                            call.respond(
                                status = HttpStatusCode.OK,
                                NoteResponse(true, "note updated", null)
                            )
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                NoteResponse(false, "problem updating note", null)
            )
        }
    }


}