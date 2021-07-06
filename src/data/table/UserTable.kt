package com.codingwithjks.data.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object UserTable : Table() {
    val userId:Column<Int> = integer("userId").autoIncrement()
    val name:Column<String> = varchar("name",512)
    val email:Column<String> = varchar("email",512).uniqueIndex()
    val password:Column<String> = varchar("password",512)

    override val primaryKey: PrimaryKey = PrimaryKey(userId)
}