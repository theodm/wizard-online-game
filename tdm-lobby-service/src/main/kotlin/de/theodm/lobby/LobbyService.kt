package de.theodm

import de.theodm.lobby.Lobby
import de.theodm.lobby.storage.LobbyStorage
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import mu.KotlinLogging
import java.time.Instant
import java.util.*

interface Participant {
    fun userPublicID(): String
    fun isBot(): Boolean
    fun botType(): String?
}

typealias LobbyID = String

class ParticipantAlreadyInLobbyException(
        lobby: LobbyID,
        participant: Participant
) : Exception("Der Teilnehmer $participant ist bereits der Lobby $lobby beigetreten.")

class CannotJoinStartedGameException(
        lobby: LobbyID
) : Exception("Die Lobby mit der Lobby-ID $lobby befindet sich bereits im Spiel.")

class ParticipantNotInLobbyException(
        lobby: LobbyID,
        participant: Participant
) : Exception("Der Teilnehmer $participant ist kein Teilnehmer der Lobby ${lobby}.")

class OnlyHostIsAllowedToChangeSettingsException(
    lobby: LobbyID,
) : Exception("Nur der Host darf die Einstellungen der Lobby $lobby verändern")

class OnlyHostIsAllowedToChangeParticipantOrderException(
    lobby: LobbyID,
) : Exception("Nur der Host darf die Reihenfolge der Teilnehmer der Lobby $lobby verändern")

sealed class GameAndSettings {
    object WizardGameAndSettings : GameAndSettings() {
        override val maxPlayers: Int
            get() = 6
        override val minPlayers: Int
            get() = 3
    }

    abstract val maxPlayers: Int
    abstract val minPlayers: Int
}

data class LobbySettings(
    val isPublicLobby: Boolean,

    val currentSelectedGameAndSettings: GameAndSettings
)

enum class Connectivity {
    /**
     * Websocket ist nicht verbunden.
     */
    Disconnected,

    /**
     * Websocket ist verbunden, aber Benutzer befindet sich nicht aktiv auf der Anwendung.
     */
    Inactive,

    /**
     * Websocket ist verbunden, Benutzer ist aktiv auf der Anwendung.
     */
    Active
}

data class ShortLobbyInfo(
    val creationTime: Instant,
    val lobbyID: LobbyID,
    val host: Participant,
    val players: List<Participant>,
    val participantsConnectivityInfo: Map<Participant, Connectivity>,
    val isInGame: Boolean
)

val log = KotlinLogging.logger { }

class MaxNumberOfLobbiesException(
        maxLobbies: Int
) : Exception("Es sind bereits zuviele Lobbys offen (max: $maxLobbies).")

class GameStartNotAllowedException() : Exception("Mit den aktuellen Einstellungen ist das Starten des Spiels nicht möglich.")
class OnlyHostCanStartGameException() : Exception("Nur der Host einer Lobby kann das Spiel starten.")

class GameAlreadyStartedException() : Exception("Das Spiel wurde bereits gestartet. Es kann nicht erneut gestartet werden.")
class NotInGameException(): Exception("Das Spiel wurde noch nicht gestartet. Daher kann nicht zur Lobby zurückgekehrt werden.")

class UserIsInAnotherLobbyException(lobbyCode: String): Exception("Sie befinden sich bereits in der Lobby mit dem Code $lobbyCode.")

//
//data class LobbyShortInfo(
//    val lobbyCode: String,
//    val lobbyGame:
//)



interface Game {
    fun isGameStartAllowed(lobby: Lobby): Boolean
    fun startGame(lobby: Lobby)

    fun gameFinishedStream(lobby: Lobby): Single<Unit>
}

class LobbyService(
    private val maxLobbies: Int,
    private val lobbyStorage: LobbyStorage,

    private val game: Game
) {
    fun lobbyStream(lobbyCode: String): Observable<Lobby> = lobbyStorage
        .lobbiesStream()
        .map { lobbies -> Optional.ofNullable(lobbies.singleOrNull { it.id == lobbyCode }) }
        .takeUntil { lobby -> lobby.isEmpty }
        .map { lobby -> lobby.get() }
        .distinctUntilChanged()

    fun lobbiesStream(): Observable<List<Lobby>> = lobbyStorage
        .lobbiesStream()
        .distinctUntilChanged()

    fun lobbiesOverviewStream(): Observable<List<ShortLobbyInfo>> = lobbyStorage
        .lobbiesStream()
        .map { lobbies -> lobbies.map { it.toShortLobbyInfo() } }
        .distinctUntilChanged()

    fun createLobby(
            host: Participant
    ): LobbyID {
        log.trace { "[Lobby#new] Es soll eine Lobby erstellt werden. (Host ist: $host)" }

        requireParticipantIsNotInAnotherLobby(host, null)

        if (lobbyStorage.numberOfLobbies() >= maxLobbies) {
            log.debug { "[Lobby#new] Es sind bereits zu viele Lobbys aktiv (${lobbyStorage.numberOfLobbies()}/$maxLobbies)." }

            throw MaxNumberOfLobbiesException(maxLobbies)
        }

        var newLobbyId: String

        // Wir generieren eine eindeutige ID für
        // die Lobby. Diese sollte möglichst benutzerfreundlich
        // sein.
        while (true) {
            log.trace { "[Lobby#new] Generiere eine eindeutige Lobby-ID." }

            newLobbyId = randomString(
                    availableChars = "012345679",
                    length = 4
            )

            log.trace { "[Lobby#${newLobbyId}] Generiert: $newLobbyId" }

            if (!lobbyStorage.doesLobbyExist(newLobbyId)) {
                break
            }

            log.trace { "[Lobby#${newLobbyId}] Generierte Lobby-ID ist nicht eindeutig. Versuche es erneut..." }
        }

        log.debug { "[Lobby#${newLobbyId}] Lobby-ID: $newLobbyId" }

        val newLobby = Lobby.create(
                creationTime = Instant.now(),
                lobbyID = newLobbyId,
                host = host,
        )

        lobbyStorage.createLobby(newLobby)

        log.info { "[Lobby#${newLobbyId}] Lobby erstellt." }

        return newLobbyId
    }

    private fun requireParticipantIsNotInAnotherLobby(participant: Participant, lobbyOfAction: LobbyID?) {
        val otherLobbyOfUser = lobbyStorage
            .getLobbyByUser(participant)

        if (otherLobbyOfUser != null && lobbyOfAction != otherLobbyOfUser.id) {
            log.debug { "[Lobby#new] Benutzer $participant ist bereits in der Lobby ${otherLobbyOfUser.id}" }

            throw UserIsInAnotherLobbyException(otherLobbyOfUser.id)
        }
    }

    private fun requireIsHost(participant: Participant, lobbyOfAction: Lobby) {
        if (lobbyOfAction.host != participant) {
            throw OnlyHostIsAllowedToChangeSettingsException(lobbyOfAction.id)
        }
    }

    private fun requireIsParticipant(participant: Participant, lobbyOfAction: Lobby) {
        if (!lobbyOfAction.participants.contains(participant)) {
            throw ParticipantNotInLobbyException(lobbyOfAction.id, participant)
        }
    }

    private fun requireGameNotStarted(lobbyOfAction: Lobby) {
        if (lobbyOfAction.isInGame) {
            throw GameAlreadyStartedException()
        }
    }

    private fun updateLobby(lobbyCode: LobbyID, updateLobbyBlock: (oldLobby: Lobby) -> Lobby?) {
        return lobbyStorage
            .getLobbyByID(lobbyCode)
            .let {
                val lobby = updateLobbyBlock(it)

                if (lobby == null) {
                    lobbyStorage.removeLobbyByID(it.id)

                    log.info { "[Lobby#${lobbyCode}] Lobby aufgelöst. (Keine Teilnehmer mehr)" }
                    return
                }

                lobbyStorage.updateLobby(lobby)
            }
    }

    // ToDo: Testen!
    fun updateSettings(
        caller: Participant,
        lobbyID: LobbyID,
        lobbySettings: LobbySettings
    ) {
        log.trace { "[Lobby#${lobbyID}] Der Teilnehmer $caller versucht die Lobby-Einstellungen auf $lobbySettings zu ändern." }

        updateLobby(lobbyID) { oldLobby ->
            requireIsHost(caller, oldLobby)

            return@updateLobby oldLobby
                .updateLobbySettings(lobbySettings)
        }

        log.debug { "[Lobby#${lobbyID}] Der Teilnehmer $caller hat die Lobby-Einstellungen auf $lobbySettings geändert." }
    }

    fun updateConnectivity(
        caller: Participant,
        lobbyID: LobbyID,
        newConnectivity: Connectivity
    ) {
        log.trace { "[Lobby#${lobbyID}] Der Teilnehmer $caller versucht, seinen Status auf $newConnectivity zu ändern." }

        updateLobby(lobbyID) {
            oldLobby ->
            requireIsParticipant(caller, oldLobby)

            return@updateLobby oldLobby
                .updateConnectivity(caller, newConnectivity)
        }

        log.debug { "[Lobby#${lobbyID}] Der Teilnehmer $caller hat seinen Status auf $newConnectivity geändert." }
    }

    fun updatePlayerOrder(
        caller: Participant,
        lobbyID: LobbyID,
        newPlayerOrder: List<String>
    ) {
        log.trace { "[Lobby#${lobbyID}] Der Teilnehmer $caller versucht, seinen die Reihenfolge der Benutzer zu ändern." }

        updateLobby(lobbyID) {
                oldLobby ->
            requireIsHost(caller, oldLobby)

            return@updateLobby oldLobby
                .updatePlayerOrder(newPlayerOrder)
        }

        log.debug { "[Lobby#${lobbyID}] Der Teilnehmer $caller hat die Reihenfolge der Benutzer geändert." }
    }

    fun participantJoin(
            lobby: LobbyID,
            joiningParticipant: Participant
    ) {
        log.trace { "[Lobby#${lobby}] Der Teilnehmer $joiningParticipant versucht, der Lobby mit der Lobby-ID $lobby beizutreten." }

        requireParticipantIsNotInAnotherLobby(joiningParticipant, lobby)

        updateLobby(lobby) {
            oldLobby ->

            if (oldLobby.participants.contains(joiningParticipant)) {
                log.debug { "[Lobby#${lobby}] Der Spieler $joiningParticipant ist bereits Teilnehmner der Lobby." }

                return@updateLobby oldLobby
            }

            requireGameNotStarted(oldLobby)

            return@updateLobby oldLobby
                .addParticipant(joiningParticipant)
        }

        log.info { "[Lobby#${lobby}] Teilnehmer ist beigetreten: $joiningParticipant" }
    }

    fun botJoin(
        caller: Participant,
        lobbyID: LobbyID,
        botDef: String
    ) {
        log.trace { "[Lobby#${lobbyID}] Der Teilnehmer $caller versucht, der Lobby den Bot $botDef hinzuzufügen." }

        val botPublicID = UUID
            .randomUUID()
            .toString()

        val botParticipant = BotParticipant(
            uniqueID = botPublicID,
            name = botDef + randomString("123456790", 2),
            botType = botDef
        )

        updateLobby(lobbyID) {
            oldLobby ->
            requireIsHost(caller, oldLobby)
            requireGameNotStarted(oldLobby)


            val newLobby = oldLobby.addParticipant(
                botParticipant
            )

            return@updateLobby newLobby
        }


        log.info { "[Lobby#${lobbyID}] Bot ist beigetreten: $botDef" }
    }

    fun kickPlayer(
        caller: Participant,
        lobbyID: LobbyID,
        playerToBeKickedPublicID: String
    ) {
        log.trace { "[Lobby#${lobbyID}] Der Teilnehmer $caller versucht, der Lobby den Spieler mit der ID $playerToBeKickedPublicID zu löschen." }

        updateLobby(lobbyID) {
            oldLobby ->
            requireIsHost(caller, oldLobby)
            requireGameNotStarted(oldLobby)

            val oldParticipant = oldLobby
                .participants
                .single { it.userPublicID() == playerToBeKickedPublicID }

            val newLobby = oldLobby
                .removeParticipant(oldParticipant)

            return@updateLobby newLobby
        }
    }

    fun participantLeave(
        lobbyCode: LobbyID,
        leavingParticipant: Participant
    ) {
        log.trace { "[Lobby#${lobbyCode}] Der Teilnehmer $leavingParticipant versucht, die Lobby mit der Lobby-ID $lobbyCode zu verlassen." }

        val oldLobby = lobbyStorage
            .getLobbyByID(lobbyCode)

        requireIsParticipant(leavingParticipant, oldLobby)

        val newLobby = oldLobby
            .removeParticipant(leavingParticipant)

        if (newLobby == null) {
            lobbyStorage.removeLobbyByID(lobbyCode)
            log.info { "[Lobby#${lobbyCode}] Lobby aufgelöst. (Keine Teilnehmer mehr)" }
            return
        }

        lobbyStorage.updateLobby(newLobby)
        log.info { "[Lobby#${lobbyCode}] Teilnehmer hat die Lobby verlassen: $leavingParticipant" }
    }

    fun gameStart(
            caller: Participant,
            lobby: LobbyID
    ) {
        log.trace { "[Lobby#${lobby}] Teilnehmer $caller versucht in der Lobby $lobby das Spiel zu starten." }

        updateLobby(lobby) {
            oldLobby ->

            requireIsHost(caller, oldLobby)

            if (oldLobby.isInGame) {
                log.debug { "[Lobby#${lobby}] Die Lobby wurde bereits gestartet." }

                throw GameAlreadyStartedException()
            }

            if (!game.isGameStartAllowed(oldLobby)) {
                log.trace { "[Lobby#${lobby}] Spielstart ist mit den aktuellen Einstellungen nicht erlaubt." }
                throw GameStartNotAllowedException()
            }

            val newLobby = oldLobby
                .copy(isInGame = true)

            game.startGame(newLobby)
            game
                .gameFinishedStream(newLobby)
                .subscribe { _ -> returnToLobby(newLobby.id) }

            return@updateLobby newLobby
        }


        log.info { "[Lobby#${lobby}] Spiel wurde gestartet." }
    }

    fun returnToLobby(
            lobby: LobbyID
    ) {
        log.trace { "[Lobby#${lobby}] Die Teilnehmer kehren zur Lobby zurück." }

        val oldLobby = lobbyStorage
                .getLobbyByID(lobby)

        if (!oldLobby.isInGame) {
            log.debug { "[Lobby#${lobby}] Lobby war nicht im Spiel, kann daher nicht zurückkehren." }

            // ToDo: Fehlerbehandlung und Test
            throw NotInGameException();
        }

        val newLobby = oldLobby
                .copy(isInGame = false)

        lobbyStorage.updateLobby(newLobby)
        log.info { "[Lobby#${lobby}] Spiel wurde beendet. Zur Lobby zurückgekehrt." }
    }

}