package de.theodm.pwf.routing

import de.theodm.pwf.user.User
import de.theodm.pwf.user.UserManager
import io.javalin.http.Context
import io.javalin.websocket.WsConnectContext
import java.lang.Exception
import javax.inject.Inject

class SessionDoesNotExistException(sessionKey: String): Exception("Einen Benutzer zur Session $sessionKey existiert nicht.")

class AuthorizationNeededException(
    method: String,
    fullUrl: String
): Exception("Der Zugriff auf $method $fullUrl erfordert Authentifizierung.")

class WsAuthorizationNeededException(
    url: String
): Exception("Der Zugriff auf $url erfordert Authentifizierung.")

class AuthHandler @Inject constructor(
    private val userManager: UserManager
) {

    private fun getAuthUser(sessionKey: String): User {
        return userManager.getUserBySessionKey(sessionKey) ?: throw SessionDoesNotExistException(sessionKey)
    }

    fun handleWebsocket(ctx: WsConnectContext): User {
        val authHeader = ctx.queryParam("Authorization") ?: throw WsAuthorizationNeededException(ctx.matchedPath())

        if (!authHeader.startsWith("Custom ")) {
            throw IllegalStateException("Der Header Authorization muss das Format 'Custom [xxx]' haben. (War: $authHeader)")
        }

        return getAuthUser(authHeader.removePrefix("Custom "))
    }

    fun handlePrivateKey(privateKey: String): User {
        return getAuthUser(privateKey)
    }

    fun handleRoute(ctx: Context): User {
        val authHeader = ctx.header("Authorization") ?: throw AuthorizationNeededException(ctx.method(), ctx.fullUrl())

        if (!authHeader.startsWith("Custom ")) {
            throw IllegalStateException("Der Header Authorization muss das Format 'Custom [xxx]' haben. (War: $authHeader)")
        }

        return getAuthUser(authHeader.removePrefix("Custom "))
    }
}