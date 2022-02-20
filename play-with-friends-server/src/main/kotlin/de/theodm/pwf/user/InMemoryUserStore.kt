package de.theodm.pwf.user

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class InMemoryUserStore() {
    private val users: ConcurrentMap<String, User> = ConcurrentHashMap()

    fun addUser(user: User) {
        users[user.userKey] = user
    }

    fun updateUser(user: User) {
        users[user.userKey] = user
    }

    fun getUserBySessionKey(userKey: String): User? {
        return users[userKey]
    }
}
