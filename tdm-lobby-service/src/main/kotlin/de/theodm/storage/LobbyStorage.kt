package de.theodm.storage

import de.theodm.Lobby
import de.theodm.LobbyID
import de.theodm.Participant
import io.reactivex.rxjava3.core.Observable

interface LobbyStorage {
    /**
     *
     */
    fun doesLobbyExist(lobbyID: LobbyID): Boolean

    /**
     *
     */
    fun createLobby(lobby: Lobby)

    fun updateLobby(lobby: Lobby)

    fun getLobbyByID(lobbyID: LobbyID): Lobby

    fun getLobbyByUser(participant: Participant): Lobby?

    fun removeLobbyByID(lobbyID: LobbyID)
    fun numberOfLobbies(): Int

    fun getAllLobbies(): List<Lobby>
    fun lobbiesStream(): Observable<List<Lobby>>
}