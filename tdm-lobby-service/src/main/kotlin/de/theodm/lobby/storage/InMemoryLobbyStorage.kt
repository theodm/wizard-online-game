package de.theodm.lobby.storage

import de.theodm.lobby.Lobby
import de.theodm.LobbyDoesNotExistException
import de.theodm.LobbyID
import de.theodm.Participant
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class InMemoryLobbyStorage : LobbyStorage {
    private val lobbies: MutableMap<LobbyID, Lobby> = mutableMapOf()

    private val subject: BehaviorSubject<List<Lobby>> = BehaviorSubject.create<List<Lobby>?>()
        .apply { onNext(listOf()) }

    override fun doesLobbyExist(lobbyID: LobbyID) = lobbies.containsKey(lobbyID)

    private fun lobbiesUpdated() {
        subject.onNext(lobbies.values.toList())
    }

    override fun createLobby(lobby: Lobby) {
        lobbies[lobby.id] = lobby
        lobbiesUpdated()
    }

    override fun updateLobby(lobby: Lobby) {
        if (!lobbies.containsKey(lobby.id))
            throw LobbyDoesNotExistException(lobby.id)

        lobbies[lobby.id] = lobby
        lobbiesUpdated()
    }

    override fun lobbiesStream(): Observable<List<Lobby>> = subject.hide()

    override fun getLobbyByID(lobbyID: LobbyID) = lobbies[lobbyID] ?: throw LobbyDoesNotExistException(lobbyID)

    override fun getLobbyByUser(participant: Participant): Lobby? = lobbies
        .values
        .firstOrNull { it.participants.contains(participant) }

    override fun removeLobbyByID(lobbyID: LobbyID) {
        if (!lobbies.containsKey(lobbyID))
            throw LobbyDoesNotExistException(lobbyID)

        lobbies.remove(lobbyID)
        lobbiesUpdated()
    }

    override fun numberOfLobbies() = lobbies.size
    override fun getAllLobbies(): List<Lobby> {
        return lobbies.values.toList()
    }


}