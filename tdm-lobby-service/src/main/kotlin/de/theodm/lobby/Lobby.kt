package de.theodm.lobby

import de.theodm.*
import java.time.Instant

data class Lobby private constructor(
    val creationTime: Instant,
    val id: LobbyID,
    val host: Participant,
    val participants: List<Participant>,
    val participantsConnectivityInfo: Map<Participant, Connectivity>,
    val isInGame: Boolean = false,
    val settings: LobbySettings
) {
    companion object {
        fun create(
            creationTime: Instant = Instant.now(),
            lobbyID: LobbyID,
            host: Participant
        ): Lobby = Lobby(
            creationTime = creationTime,
            id = lobbyID,
            host = host,
            participants = listOf(host),
            isInGame = false,
            participantsConnectivityInfo = mapOf(host to Connectivity.Disconnected),
            settings = LobbySettings(true, GameAndSettings.WizardGameAndSettings)
        )
    }

    fun toShortLobbyInfo() = ShortLobbyInfo(
        creationTime = creationTime,
        lobbyID = id,
        host = host,
        players = participants,
        participantsConnectivityInfo = participantsConnectivityInfo,
        isInGame = isInGame
    )

    fun requireIsParticipant(participant: Participant) {
        if (!participants.contains(participant)) {
            log.trace { "[Lobby#${id}] $participant ist kein Teilnehmer der Lobby." }

            throw ParticipantNotInLobbyException(id, participant)
        }
    }

    fun removeParticipant(participant: Participant): Lobby? {
        requireIsParticipant(participant)

        val newLobby = copy(
                participants = participants - participant,
                participantsConnectivityInfo = participantsConnectivityInfo.minus(participant)
        )

        if (host == participant) {
            log.trace { "[Lobby#${id}] Host hat die Lobby verlassen." }

            val participantsWithoutBots = newLobby
                .participants
                .filter { !it.isBot() }

            if (participantsWithoutBots
                    .isEmpty()) {
                log.trace { "[Lobby#${id}] Es gibt keinen menschlichen Teilnehmer mehr in der Lobby." }

                return null
            }

            val newHost = participantsWithoutBots[0]

            log.trace { "[Lobby#${id}] $newHost ist nun neuer Host." }

            // Nächster Spieler ist nun Host
            return newLobby
                .copy(host = newHost)
        }

        return newLobby
    }

    fun addParticipant(participant: Participant): Lobby {
        if (participants.contains(participant)) {
            log.debug { "[Lobby#${id}] Benutzer $participant ist der Lobby bereits beigetreten." }

            return this
        }

        return copy(
                participants = participants + participant,
                participantsConnectivityInfo = participantsConnectivityInfo + (participant to Connectivity.Disconnected)
        )
    }

    fun updateConnectivity(participant: Participant, newConnectivity: Connectivity): Lobby {
        requireIsParticipant(participant)

        return copy(
            participantsConnectivityInfo = participantsConnectivityInfo + (participant to newConnectivity)
        )
    }

    /**
     * @param newOrder Liste alle öffentlichen IDs in der Spielerliste mit neuer Sortierung.
     */
    fun updatePlayerOrder(
        newOrder: List<String>
    ): Lobby {
        log.trace { "[Lobby#${id}] Alte Reihenfolge der Benutzer: $participants" }

        val newParticipantsList = participants
            .sortedBy { newOrder.indexOf(it.userPublicID()) }

        log.trace { "[Lobby#${id}] Neue Reihenfolge der Benutzer: $newParticipantsList" }

        return copy(
            participants = newParticipantsList
        )
    }

    fun updateLobbySettings(
        newLobbySettings: LobbySettings
    ): Lobby {
        return copy(
            settings = newLobbySettings
        )
    }

}