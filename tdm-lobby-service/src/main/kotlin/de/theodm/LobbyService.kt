package de.theodm

import de.theodm.storage.LobbyStorage
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import mu.KotlinLogging
import java.time.Instant
import java.util.*
import kotlin.random.Random

/**
 * Erstellt eine zufällige Zeichenfolge
 * aus den verfügbaren Zeichensatz [availableChars]
 * mit der Länge [length];
 */
fun randomString(
        availableChars: String,
        length: Int
): String {
    require(availableChars.length >= 2) { "availableChars.length >= 2" }
    require(length > 0) { "length > 0" }

    var resultStr = ""
    while (resultStr.length < length) {
        val charIndex = Random
                .Default
                .nextInt(0, availableChars.length - 1)

        resultStr += availableChars[charIndex]
    }

    return resultStr
}

interface Participant {
    fun userPublicID(): String
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


data class OutgoingLobby(
        val creationTime: Instant,
        val id: LobbyID,
        val host: Participant,
        val participants: List<Participant>,
        val isInGame: Boolean
) {

}

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

data class Lobby(
        val creationTime: Instant,
        val id: LobbyID,
        val host: Participant,
        val participants: List<Participant>,
        val participantsConnectivityInfo: Map<Participant, Connectivity>,
        val isInGame: Boolean = false,
        val settings: LobbySettings
) {
    fun toShortLobbyInfo(): ShortLobbyInfo {
        return ShortLobbyInfo(
            creationTime,
            id,
            host,
            participants,
            participantsConnectivityInfo,
            isInGame
        )
    }

    fun participantLeave(participant: Participant): Lobby? {
        if (!participants.contains(participant)) {
            log.trace { "[Lobby#${id}] $participant ist kein Teilnehmer der Lobby." }
            throw ParticipantNotInLobbyException(id, participant)
        }

        val newLobby = copy(
                participants = participants - participant
        )

        if (host == participant) {
            log.trace { "[Lobby#${id}] Host hat die Lobby verlassen." }

            if (newLobby.participants.isEmpty()) {
                log.trace { "[Lobby#${id}] Es gibt keinen Teilnehmer mehr in der Lobby." }

                return null
            }

            log.trace { "[Lobby#${id}] ${newLobby.participants[0]} ist nun neuer Host." }

            // Nächster Spieler ist nun Host
            return newLobby.copy(host = newLobby.participants[0])
        }

        return newLobby
    }

    fun participantJoin(participant: Participant): Lobby {
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
        if (!this.participants.contains(participant)) {
            log.debug { "[Lobby#${id}] Benutzer $participant befindet sich nicht in der Lobby." }
            throw ParticipantNotInLobbyException(this.id, participant)
        }

        return copy(
            participantsConnectivityInfo = participantsConnectivityInfo + (participant to newConnectivity)
        )
    }

    /**
     * @param newOrder Liste alle öffentlichen IDs in der Spielerliste mit neuer Sortierung.
     */
    fun updatePlayerOrder(
        caller: Participant,
        newOrder: List<String>
    ): Lobby {
        if (caller != host) {
            log.debug { "[Lobby#${id}] Benutzer $caller ist nicht der Host der Lobby und darf die Reihenfolge der Benutzer nicht ändern." }
            throw OnlyHostIsAllowedToChangeParticipantOrderException(this.id)
        }

        log.trace { "[Lobby#${id}] Alte Reihenfolge der Benutzer: $participants" }

        val newParticipantsList = participants
            .sortedBy { newOrder.indexOf(it.userPublicID()) }

        log.trace { "[Lobby#${id}] Neue Reihenfolge der Benutzer: $newParticipantsList" }

        return copy(
            participants = newParticipantsList
        )
    }

    fun updateLobbySettings(caller: Participant, newLobbySettings: LobbySettings): Lobby {
        if (caller != host) {
            log.debug { "[Lobby#${id}] Benutzer $caller ist nicht der Host der Lobby und darf die Einstellungen nicht ändern." }
            throw OnlyHostIsAllowedToChangeSettingsException(this.id)
        }

        return copy(
            settings = newLobbySettings
        )
    }

}

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

// ToDo: Ein Benutzer darf nicht in mehreren Lobbys gleichzeitig sein.
class LobbyService(
    private val maxLobbies: Int,
    private val lobbyStorage: LobbyStorage,

    private val game: Game
) {
    fun lobbyStream(lobbyCode: String): Observable<Optional<Lobby>> = lobbyStorage
        .lobbiesStream()
        .map { lobbies -> Optional.ofNullable(lobbies.singleOrNull { it.id == lobbyCode }) }
        .distinctUntilChanged()

    fun lobbiesStream(): Observable<List<Lobby>> = lobbyStorage
        .lobbiesStream()
        .distinctUntilChanged()

    fun lobbiesOverviewStream(): Observable<List<ShortLobbyInfo>> = lobbyStorage
        .lobbiesStream()
        .map { lobbies -> lobbies.map { it.toShortLobbyInfo() } }
        .distinctUntilChanged()

//    /**
//     * Gibt alle öffentlichen Lobbies, welche gerade bestehen zurück.
//     */
//    fun getPublicLobbies(): List<ShortLobbyInfo> {
//        log.trace { "[Lobby#all] Es sollen alle Lobbies zurückgegeben werden." }
//
//        return lobbyStorage
//            .getAllLobbies()
//            .map { it.toShortLobbyInfo() }
//    }

    fun createLobby(
            host: Participant
    ): LobbyID {
        log.trace { "[Lobby#new] Es soll eine Lobby erstellt werden. (Host ist: $host)" }

        requireParticipantIsNotInAnotherLobby(host, null)

        if (lobbyStorage.numberOfLobbies() >= maxLobbies) {
            log.debug { "[Lobby#new] Es sind bereits zuviele Lobbys aktiv (${lobbyStorage.numberOfLobbies()}/$maxLobbies)." }

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

        val newLobby = Lobby(
                creationTime = Instant.now(),
                id = newLobbyId,
                host = host,
                participants = listOf(host),
                isInGame = false,
                participantsConnectivityInfo = mapOf(host to Connectivity.Disconnected),
                settings = LobbySettings(true, GameAndSettings.WizardGameAndSettings)
        )

        lobbyStorage.createLobby(newLobby)

        log.info { "[Lobby#${newLobbyId}] Lobby erstellt." }

        return newLobbyId
    }

//    fun getLobbyIfParticipant(
//        caller: Participant,
//        lobbyID: LobbyID
//    ): Lobby {
//        log.trace { "[Lobby#${lobbyID}] Teilnehmer $caller versucht die Daten der Lobby $lobbyID zu lesen." }
//
//        val lobby = lobbyStorage
//            .getLobbyByID(lobbyID)
//
//        if (!lobby.participants.contains(caller)) {
//            throw IllegalStateException("Teilnehmer $caller ist kein Teilnehmer der Lobby ${lobbyID}.")
//        }
//
//        return lobby
//    }

    private fun updateLobby(lobbyCode: LobbyID, updateLobbyBlock: (oldLobby: Lobby) -> Lobby) {
        return lobbyStorage
            .getLobbyByID(lobbyCode)
            .let {
                lobbyStorage.updateLobby(updateLobbyBlock(it))
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
                .updateLobbySettings(caller, lobbySettings)
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
                .updatePlayerOrder(caller, newPlayerOrder)
        }

        log.debug { "[Lobby#${lobbyID}] Der Teilnehmer $caller hat die Reihenfolge der Benutzer geändert." }
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

    private fun requireParticipantIsNotInAnotherLobby(participant: Participant, lobbyOfAction: LobbyID?) {
        val otherLobbyOfUser = lobbyStorage
            .getLobbyByUser(participant)

        if (otherLobbyOfUser != null && lobbyOfAction != otherLobbyOfUser.id) {
            log.debug { "[Lobby#new] Benutzer $participant ist bereits in der Lobby ${otherLobbyOfUser.id}" }

            throw UserIsInAnotherLobbyException(otherLobbyOfUser.id)
        }
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

            if (oldLobby.isInGame) {
                log.debug { "[Lobby#${lobby}] Die Lobby wurde bereits gestartet. Teilnehmer kann nicht mehr beitreten." }

                throw CannotJoinStartedGameException(lobby)
            }

            return@updateLobby oldLobby
                .participantJoin(joiningParticipant)
        }

        log.info { "[Lobby#${lobby}] Teilnehmer ist beigetreten: $joiningParticipant" }
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
            .participantLeave(leavingParticipant)

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