package com.codingwithjks.repository


import com.codingwithjks.data.dao.UserDao

import com.codingwithjks.data.model.User

import com.codingwithjks.data.table.UserTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement

class UserRepository : UserDao {

    override suspend fun createUser(name: String, email: String, password: String): User? {
        var statement: InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            statement = UserTable.insert { user ->
                user[UserTable.name] = name
                user[UserTable.email] = email
                user[UserTable.password] = password
            }
        }
        return rowToUser(statement?.resultedValues?.get(0))
    }

    override suspend fun findUser(userId: Int): User? = DatabaseFactory.dbQuery {
        UserTable.select { UserTable.userId.eq(userId) }
            .map {
                rowToUser(it)
            }.singleOrNull()
    }

    override suspend fun findUserByEmail(email: String): User? = DatabaseFactory.dbQuery {
        UserTable.select { UserTable.email.eq(email) }
            .map {
                rowToUser(it)
            }.singleOrNull()
    }

    override suspend fun deleteUser(userId: Int): Int = DatabaseFactory.dbQuery {
        UserTable.deleteWhere { UserTable.userId.eq(userId) }
    }

    override suspend fun updateUser(userId: Int, name: String, email: String, password: String): Int = DatabaseFactory
        .dbQuery {
            UserTable.update({ UserTable.userId.eq(userId) }) { user ->
                user[UserTable.name] = name
                user[UserTable.email] = email
                user[UserTable.password] = password
            }
        }

    override suspend fun getAllUser(): List<User> = DatabaseFactory.dbQuery {
        UserTable.selectAll()
            .mapNotNull {
                rowToUser(it)
            }
    }

    private fun rowToUser(row: ResultRow?): User? {
        if (row == null)
            return null
        return User(
            userId = row[UserTable.userId],
            name = row[UserTable.name],
            email = row[UserTable.email],
            password = row[UserTable.password]
        )
    }
}