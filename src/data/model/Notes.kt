package com.codingwithjks.data.model

import io.ktor.auth.*

data class Notes(
    val id:Int?,
    val userId:Int?,
    val title:String?,
    val note:String?,
    val date:Long?
)

