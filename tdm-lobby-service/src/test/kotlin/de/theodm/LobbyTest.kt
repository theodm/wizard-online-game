package de.theodm

import com.google.common.collect.Range
import com.google.common.truth.Truth
import de.theodm.lobby.Lobby
import de.theodm.lobby.storage.InMemoryLobbyStorage
import extensions.lastOrThrow
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Instant

data class WizardParticipant(
    private val sessionID: String
) : Participant {
    override fun userPublicID() = sessionID
    override fun isBot() = false
}

class WizardTest {

    data class Environment(
        val lobbyService: LobbyService,
        val lobbyUpdates: List<Lobby>,
        val lobbyActionUpdates: List<LobbyAction>,
        val lobbiesUpdates: List<List<ShortLobbyInfo>>
    )

    fun initEnvironment(
        maxLobbies: Int = 1000,
        isGameStartAllowed: (lobby: Lobby) -> Boolean = { lobby -> lobby.participants.size >= 3 }
    ): Environment {
        val lobbyUpdates = mutableListOf<Lobby>()
        val lobbyActionUpdates = mutableListOf<LobbyAction>()
        val lobbiesUpdate = mutableListOf<List<ShortLobbyInfo>>()

        val lobbyService = LobbyService(
            maxLobbies,
            InMemoryLobbyStorage(),
            isGameStartAllowed
        ) { println("Game started...") }

        lobbyService.registerLobbyUpdateListener { action, lobby -> lobbyActionUpdates.add(action); lobbyUpdates.add(lobby) }
        lobbyService.registerLobbiesUpdateListeners { lobbies -> lobbiesUpdate.add(lobbies) }

        return Environment(
            lobbyService, lobbyUpdates, lobbyActionUpdates, lobbiesUpdate
        )
    }


    @Test
    @DisplayName("Alle aktuellen Lobbies können abgerufen werden und bei Änderung wird darüber informiert.")
    fun allLobbiesAndUpdates() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates, lobbiesUpdates) = initEnvironment()

        Truth.assertThat(lobbyService.getPublicLobbies()).isEqualTo(listOf<ShortLobbyInfo>())

        val participant = WizardParticipant("Theo")
        val lobbyID1 = lobbyService.createLobby(participant)
        val lobbyID2 = lobbyService.createLobby(participant)
        val lobbyID3 = lobbyService.createLobby(participant)

        Truth.assertThat(lobbyService.getPublicLobbies()).hasSize(3)
        Truth.assertThat(lobbiesUpdates.lastOrThrow()).hasSize(3)

        Truth.assertThat(lobbiesUpdates.lastOrThrow()).isEqualTo(listOf(
            ShortLobbyInfo(lobbyID1, listOf(participant), false),
            ShortLobbyInfo(lobbyID2, listOf(participant), false),
            ShortLobbyInfo(lobbyID3, listOf(participant), false),
        ))

    }

    @Test
    @DisplayName("Eine Lobby wird erfolgreich erstellt.")
    fun createLobby() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates) = initEnvironment()

        val participant = WizardParticipant("Theo")

        val lobbyID = lobbyService
            .createLobby(participant)

        Truth.assertThat(lobbyID).hasLength(4)

        Truth.assertThat(lobbyUpdates.lastOrThrow().creationTime)
            .isIn(Range.closed(Instant.now().minusSeconds(60), Instant.now()))
        Truth.assertThat(lobbyUpdates.lastOrThrow().host).isEqualTo(participant)
        Truth.assertThat(lobbyUpdates.lastOrThrow().isInGame).isFalse()
        Truth.assertThat(lobbyUpdates.lastOrThrow().id).isEqualTo(lobbyID)
        Truth.assertThat(lobbyUpdates.lastOrThrow().participants).isEqualTo(listOf(participant))

        Truth.assertThat(lobbyActionUpdates.lastOrThrow()).isEqualTo(LobbyAction.CREATED)
    }

    @Test
    @DisplayName("Eine Lobby kann nicht erstellt werden, da es bereits zuviele Lobbies gibt.")
    fun createLobbyMaxLobbies() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates) = initEnvironment(1)

        val participant1 = WizardParticipant("Theo")
        val participant2 = WizardParticipant("Christian")

        lobbyService.createLobby(participant1)

        Assertions.assertThrows(MaxNumberOfLobbiesException::class.java) {
            lobbyService.createLobby(participant2)
        }
    }

    @Test
    @DisplayName("Ein Teilnehmer verändert seinen Konnektivitätsstatus.")
    fun changeConnectivity() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates) = initEnvironment(1)

        val participant1 = WizardParticipant("Theo")

        val lobby = lobbyService.createLobby(participant1)

        Truth.assertThat(
            lobbyService.getLobbyIfParticipant(
                participant1,
                lobby
            ).participantsConnectivityInfo[participant1]
        ).isEqualTo(Connectivity.Disconnected)

        // When
        lobbyService.updateConnectivity(participant1, lobby, Connectivity.Active)

        Truth.assertThat(lobbyUpdates.lastOrThrow().participantsConnectivityInfo[participant1])
            .isEqualTo(Connectivity.Active)
        Truth.assertThat(lobbyActionUpdates.lastOrThrow()).isEqualTo(LobbyAction.CONNECTIVITY_UPDATE)
    }

    @Test
    @DisplayName("Der Host ändert die Reihenfolge der Spieler.")
    fun hostChangesOrder() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates) = initEnvironment(1)

        val participant1 = WizardParticipant("Theo")
        val participant2 = WizardParticipant("Christian")

        val lobby = lobbyService.createLobby(participant1)
        lobbyService.participantJoin(lobby, participant2)

        Truth.assertThat(lobbyUpdates.lastOrThrow().participants).isEqualTo(listOf(participant1, participant2))

        lobbyService.updatePlayerOrder(
            participant1, lobby, newPlayerOrder = listOf(
                participant2.userPublicID(),
                participant1.userPublicID()
            )
        )

        Truth.assertThat(lobbyActionUpdates.lastOrThrow()).isEqualTo(LobbyAction.PLAYERORDER_UPDATE)
        Truth.assertThat(lobbyUpdates.lastOrThrow().participants).isEqualTo(listOf(participant2, participant1))
    }

    @Test
    @DisplayName("Ein Teilnehmer tritt einer bestehenden Lobby bei.")
    fun participantJoin() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates) = initEnvironment(1)

        val participant1 = WizardParticipant("Theo")
        val participant2 = WizardParticipant("Christian")

        val lobby = lobbyService.createLobby(participant1)

        lobbyService.participantJoin(lobby, participant2)

        Truth.assertThat(lobbyUpdates.lastOrThrow().host).isEqualTo(participant1)
        Truth.assertThat(lobbyUpdates.lastOrThrow().isInGame).isFalse()
        Truth.assertThat(lobbyUpdates.lastOrThrow().id).isEqualTo(lobby)
        Truth.assertThat(lobbyUpdates.lastOrThrow().participants).isEqualTo(listOf(participant1, participant2))

        Truth.assertThat(lobbyActionUpdates.lastOrThrow()).isEqualTo(LobbyAction.PARTICIPANT_JOIN)
    }

    @Test
    @DisplayName("Ein Teilnehmer kann einer Lobby nicht beitreten, bei der er bereits Teilnehmer ist.")
    fun participantJoinError() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates) = initEnvironment(1)

        val participant = WizardParticipant("Theo")

        val lobby = lobbyService.createLobby(participant)

        Assertions.assertThrows(ParticipantAlreadyInLobbyException::class.java) {
            lobbyService.participantJoin(lobby, participant)
        }
    }

    @Test
    @DisplayName("Ein Teilnehmer kann einer Lobby nicht beitreten, die bereits gestartet ist.")
    fun participantJoinErrorAlreadyStarted() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates) = initEnvironment(1) { true }

        val participant1 = WizardParticipant("Theo")
        val participant2 = WizardParticipant("Christian")

        val lobby = lobbyService.createLobby(participant1)

        lobbyService.gameStart(participant1, lobby);

        Assertions.assertThrows(CannotJoinStartedGameException::class.java) {
            lobbyService.participantJoin(lobby, participant2)
        }
    }

    @Test
    @DisplayName("Ein Teilnehmer verlässt die Lobby.")
    fun participantLeavesLobby() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates) = initEnvironment(1)

        val participant1 = WizardParticipant("Theo")
        val participant2 = WizardParticipant("Christian")

        val lobby = lobbyService.createLobby(participant1)

        lobbyService.participantJoin(lobby, participant2)
        lobbyService.participantLeave(lobby, participant2)

        Truth.assertThat(lobbyUpdates.lastOrThrow().host).isEqualTo(participant1)
        Truth.assertThat(lobbyUpdates.lastOrThrow().participants).isEqualTo(listOf(participant1))

        Truth.assertThat(lobbyActionUpdates.lastOrThrow()).isEqualTo(LobbyAction.PARTICIPANT_LEAVE)
    }

    @Test
    @DisplayName("Host verlässt die Lobby.")
    fun hostLeavesLobby() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates) = initEnvironment(1)

        val participant1 = WizardParticipant("Theo")
        val participant2 = WizardParticipant("Christian")

        val lobby = lobbyService.createLobby(participant1)
        lobbyService.participantJoin(lobby, participant2)

        lobbyService.participantLeave(lobby, participant1)

        Truth.assertThat(lobbyUpdates.lastOrThrow().host).isEqualTo(participant2)
        Truth.assertThat(lobbyUpdates.lastOrThrow().participants).isEqualTo(listOf(participant2))

        Truth.assertThat(lobbyActionUpdates.lastOrThrow()).isEqualTo(LobbyAction.PARTICIPANT_LEAVE)
    }

    @Test
    @DisplayName("Letzter Teilnehmer verlässt die Lobby.")
    fun lastParticipantLeavesLobby() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates) = initEnvironment(1)

        val participant1 = WizardParticipant("Theo")

        val lobby = lobbyService.createLobby(participant1)

        lobbyService.participantLeave(lobby, participant1)

        Assertions.assertThrows(LobbyDoesNotExistException::class.java) {
            lobbyService.participantJoin(lobby, participant1)
        }
    }

    @Test
    @DisplayName("Teilnehmer befindet sich nicht in der Lobby.")
    fun wrongParticipantLeavesLobby() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates) = initEnvironment(1)

        val participant1 = WizardParticipant("Theo")
        val participant2 = WizardParticipant("Christian")

        val lobby = lobbyService.createLobby(participant1)

        Assertions.assertThrows(ParticipantNotInLobbyException::class.java) {
            lobbyService.participantLeave(lobby, participant2)
        }
    }

    @Test
    @DisplayName("Spiel wird gestartet.")
    fun gameStarted() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates) = initEnvironment(1) { true }

        val participant = WizardParticipant("Theo")

        val lobby = lobbyService.createLobby(participant)

        lobbyService.gameStart(participant, lobby);

        Truth.assertThat(lobbyUpdates.lastOrThrow().isInGame).isEqualTo(true)
        Truth.assertThat(lobbyActionUpdates.lastOrThrow()).isEqualTo(LobbyAction.GAME_START)
    }

    @Test
    @DisplayName("Ein Teilnehmer, der nicht der Host ist, versucht das Spiel zu starten.")
    fun gameStartFailNotHost() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates) = initEnvironment(1) { true }

        val participant1 = WizardParticipant("Theo")
        val participant2 = WizardParticipant("Christian")

        val lobby = lobbyService.createLobby(participant1)

        Assertions.assertThrows(OnlyHostCanStartGameException::class.java) {
            lobbyService.gameStart(participant2, lobby);
        }
    }

    @Test
    @DisplayName("Das Spiel kann nicht gestartet werden, wenn die Spieleinstellungen ungültig sind.")
    fun gameStartFailSettingsInvalid() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates) = initEnvironment(1) { false }

        val participant = WizardParticipant("Theo")

        val lobby = lobbyService.createLobby(participant)

        Assertions.assertThrows(GameStartNotAllowedException::class.java) {
            lobbyService.gameStart(participant, lobby);
        }
    }

    @Test
    @DisplayName("Das Spiel kann nicht gestartet werden, wenn das Spiel bereits gestartet ist.")
    fun gameStartFailIfAlreadyInGame() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates) = initEnvironment(1) { true }

        val participant = WizardParticipant("Theo")

        val lobby = lobbyService.createLobby(participant)

        lobbyService.gameStart(participant, lobby);

        Assertions.assertThrows(GameAlreadyStartedException::class.java) {
            lobbyService.gameStart(participant, lobby);
        }
    }

    @Test
    @DisplayName("Das Zurückkehren zur Lobby ist nicht möglich, wenn das Spiel gar nicht gestartet ist.")
    fun returnToLobbyFailedIfGameNotStarted() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates) = initEnvironment(1) { true }

        val participant = WizardParticipant("Theo")

        val lobby = lobbyService.createLobby(participant)

        Assertions.assertThrows(NotInGameException::class.java) {
            lobbyService.returnToLobby(lobby)
        }
    }

    @Test
    @DisplayName("Das Zurückkehren zur Lobby ist erfolgreich.")
    fun returnToLobbySuccessful() {
        val (lobbyService, lobbyUpdates, lobbyActionUpdates) = initEnvironment(1) { true }

        val participant = WizardParticipant("Theo")

        val lobby = lobbyService.createLobby(participant)

        lobbyService.gameStart(participant, lobby)
        lobbyService.returnToLobby(lobby)

        Truth.assertThat(lobbyUpdates.lastOrThrow().isInGame).isEqualTo(false)
        Truth.assertThat(lobbyActionUpdates.lastOrThrow()).isEqualTo(LobbyAction.GAME_ENDED)
    }


}