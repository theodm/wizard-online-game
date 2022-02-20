package de.theodm.pwf.routing.user

import de.theodm.pwf.user.User
import de.theodm.pwf.user.UserManager
import mu.KotlinLogging
import de.theodm.pwf.routing.model.RsUser
import de.theodm.pwf.routing.model.toRsUser
import de.theodm.pwf.user.UserName
import javax.inject.Inject

private val log = KotlinLogging.logger {  }

class UserEndpoints @Inject constructor(
    private val userManager: UserManager
) {
    fun changeName(
        authUser: User,
        name: String
    ) {
        log.debug { "[User#${authUser.userPublicID.substring(0, 10)}] ${authUser.userName} tries to change name to: $name" }

        userManager.updateUserNameBySessionKey(authUser.userKey, UserName(name))

        log.info { "[User#${authUser.userPublicID.substring(0, 10)}] ${authUser.userName} changed name to $name" }
    }

    fun createUser(userName: String): RsUser {
        log.debug { "[User#new] Trying to create user with name: $userName" }

        val user = userManager.createUser(
            UserName(userName)
        )

        log.trace { "[User#${user.userPublicID.substring(0, 10)}] Created User: $user" }
        log.info { "[User#${user.userPublicID.substring(0, 10)}] Created User: $userName (${user.userKey}, ${user.userPublicID})" }

        return user.toRsUser()
    }
}
