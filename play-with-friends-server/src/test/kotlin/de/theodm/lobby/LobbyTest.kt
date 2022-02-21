package de.theodm.lobby

import com.google.common.truth.Truth
import de.theodm.BotParticipant
import de.theodm.Connectivity
import de.theodm.Participant
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Instant

internal class LobbyTest {
    data class LobbyParticipant(
        private val id: String
    ) : Participant {
        override fun userPublicID() = id
        override fun isBot() = false
    }

    @DisplayName("Wenn der letzte Spieler die Lobby verlässt, wird sie aufgelöst.")
    @Test
    fun removeParticipant_LastPlayerLeaves() {
        val theo = LobbyParticipant("Theo")

        val lobbyToTest: Lobby = Lobby.create(
            creationTime = Instant.MIN,
            lobbyID = "1234",
            host = theo
        )

        val result = lobbyToTest
            .removeParticipant(theo)

        Truth.assertThat(result).isNull()
    }

    @DisplayName("Wenn der letzte Host die Lobby verlässt, wird der nächste Spieler Host.")
    @Test
    fun removeParticipant_HostLeaves_HumanPlayerRemaining() {
        val theo = LobbyParticipant("Theo")
        val steiner = LobbyParticipant("Steiner")

        val lobbyToTest: Lobby = Lobby.create(
            creationTime = Instant.MIN,
            lobbyID = "1234",
            host = theo
        )
            .addParticipant(steiner)

        val result = lobbyToTest
            .removeParticipant(theo)

        Truth.assertThat(result!!.participants).isEqualTo(listOf(steiner))
        Truth.assertThat(result.participantsConnectivityInfo).doesNotContainKey(theo)
        Truth.assertThat(result.host).isEqualTo(steiner)
    }

    @DisplayName("Wenn ein Nicht-Host die Lobby verlässt, ist er weg.")
    @Test
    fun removeParticipant_PlayerLeaves() {
        val theo = LobbyParticipant("Theo")
        val steiner = LobbyParticipant("Steiner")

        val lobbyToTest: Lobby = Lobby.create(
            creationTime = Instant.MIN,
            lobbyID = "1234",
            host = theo
        )
            .addParticipant(steiner)

        val result = lobbyToTest
            .removeParticipant(steiner)

        Truth.assertThat(result!!.participants).isEqualTo(listOf(theo))
        Truth.assertThat(result.participantsConnectivityInfo).doesNotContainKey(steiner)
    }

    @DisplayName("Wenn der letzte menschliche Spieler die Lobby verlässt, wird sie aufgelöst (auch wenn es Bots gibt).")
    @Test
    fun removeParticipant_HostLeaves_OnlyBotsRemaining() {
        val theo = LobbyParticipant("Theo")
        val steinerBot = BotParticipant("SteinerBot", "SteinerBot")

        val lobbyToTest: Lobby = Lobby.create(
            creationTime = Instant.MIN,
            lobbyID = "1234",
            host = theo
        )
            .addParticipant(steinerBot)

        val result = lobbyToTest
            .removeParticipant(theo)

        Truth.assertThat(result).isNull()
    }

    @DisplayName("Wenn der Host die Lobby verlässt, kann ein Bot nicht versehentlich Host werden.")
    @Test
    fun removeParticipant_HostLeaves_BotWillNotBeHost() {
        val theo = LobbyParticipant("Theo")
        val steinerBot = BotParticipant("SteinerBot", "SteinerBot")
        val tabea = LobbyParticipant("Tabea")

        val lobbyToTest: Lobby = Lobby.create(
            creationTime = Instant.MIN,
            lobbyID = "1234",
            host = theo
        )
            .addParticipant(steinerBot)
            .addParticipant(tabea)

        val result = lobbyToTest
            .removeParticipant(theo)

        Truth.assertThat(result!!.host).isEqualTo(tabea)
        Truth.assertThat(result.participantsConnectivityInfo).doesNotContainKey(theo)
    }

    // ToDo: Fleißarbeit
//    @DisplayName("Benutzer wird einer Lobby hinzugefügt.")
//    @DisplayName("Einstellungen der Lobby werden geändert.")
//    @DisplayName("Konnektivität eines Spielers wird geändert.")
//    @DisplayName("Lobby-Einstellungen werden geändert.")
//    @DisplayName("Spieler-Reihenfolge wird geändert.")






}