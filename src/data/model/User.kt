package com.codingwithjks.data.model

import io.ktor.auth.*

data class User(
    val userId:Int?,
    val name:String?,
    val email:String?,
    val password:String?
) : Principal
