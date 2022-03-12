package de.theodm.pwf.routing.lobby

import de.theodm.Participant
import de.theodm.pwf.user.User
import de.theodm.wizard.game.players.WizardPlayer

data class LobbyParticipant(
    val userPublicID: String,
    val userName: String
) : Participant, WizardPlayer {
    override fun userPublicID(): String {
        return userPublicID
    }

    override fun isBot(): Boolean {
        return false
    }

    override fun botType(): String? = null

    companion object {
        fun of(user: User) = LobbyParticipant(
            user.userPublicID,
            user.userName.toString()
        )
    }
}