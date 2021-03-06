package de.theodm.pwf.routing.lobby

import de.theodm.*
import de.theodm.lobby.Lobby
import de.theodm.pwf.routing.model.*

fun Participant.toRsParticipant(): RsParticipant {
    val userName: String = when (this) {
        is LobbyParticipant -> this.userName
        is BotParticipant -> this.name
        else -> "<error>"
    }

    return RsParticipant(
        userPublicID = this.userPublicID(),
        userName = userName
    )
}

fun ShortLobbyInfo.toRsShortLobbyInfo() =
    RsShortLobbyInfo(this.lobbyID, this.players.map(Participant::toRsParticipant), this.isInGame)


fun Lobby.toRsLobby(): RsLobby {
    return RsLobby(
        creationTime = this.creationTime,
        lobbyID = this.id,
        host = this.host.toRsParticipant(),
        participants = this.participants.map(Participant::toRsParticipant),
        inGame = this.isInGame,
        participantsConnectivityInfo = this.participantsConnectivityInfo.map { it.key.userPublicID() to it.value.toRsConnectivity() }.toMap(),
        settings = settings.toRsLobbySettings()
    )
}

private fun LobbySettings.toRsLobbySettings(): RsLobbySettings {
    return RsLobbySettings(
        this.isPublicLobby,
        RsWizardGameAndSettings()
    )
}

private fun Connectivity.toRsConnectivity(): RsConnectivity {
    return RsConnectivity.valueOf(this.name.toUpperCase())
}