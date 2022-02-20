package de.theodm.pwf.user

import java.util.*
import javax.inject.Inject

class UserManager @Inject constructor(
    private val userStore: InMemoryUserStore
) {
    fun createUser(
        initialUserName: UserName
    ): User {
        val user = User(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            initialUserName
        )

        userStore.addUser(user)

        return user
    }

    fun getUserBySessionKey(sessionKey: String): User? {
        return userStore.getUserBySessionKey(sessionKey)
    }

    fun updateUserNameBySessionKey(
        sessionKey: String,
        newUserName: UserName
    ) {
        val oldUser = userStore.getUserBySessionKey(sessionKey)

        require(oldUser != null)

        val user = oldUser
            .copy(userName = newUserName)

        userStore.updateUser(user)
    }
}
