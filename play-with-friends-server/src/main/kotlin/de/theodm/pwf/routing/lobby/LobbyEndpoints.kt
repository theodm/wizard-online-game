package de.theodm.pwf.routing.lobby

import de.theodm.LobbyService
import de.theodm.ParticipantAlreadyInLobbyException
import de.theodm.pwf.routing.model.*
import de.theodm.pwf.user.User
import javax.inject.Inject

class LobbyEndpoints @Inject constructor(
    private val lobbyService: LobbyService
) {
    fun createLobby(authUser: User): CreateLobbyResponse {
        val lobbyCode = lobbyService
            .createLobby(LobbyParticipant.of(authUser))

        return CreateLobbyResponse(lobbyCode)
    }

    fun joinLobby(authUser: User, lobbyID: String) {
        try {
            lobbyService
                .participantJoin(
                    lobby = lobbyID,
                    joiningParticipant = LobbyParticipant.of(authUser)
                )
        } catch (ex: ParticipantAlreadyInLobbyException) {
            // ToDo: Vielleicht sollte dieser Fall niemals zu einem Fehler führen?
        }
    }

    fun startGame(authUser: User, lobbyID: String) {
        lobbyService.gameStart(
            caller = LobbyParticipant.of(authUser),
            lobby = lobbyID
        )
    }

    fun updateLobbySettings(
        authUser: User,
        lobbyID: String,
        newSettings: RsLobbySettings
    ) {
        lobbyService.updateSettings(
            caller = LobbyParticipant.of(authUser),
            lobbyID = lobbyID,
            lobbySettings = newSettings.toLobbySettings()
        )
    }

    fun updatePlayerOrder(authUser: User, lobbyID: String, newOrder: RqUpdatePlayerOrder) {
        lobbyService.updatePlayerOrder(
            caller = LobbyParticipant.of(authUser),
            lobbyID = lobbyID,
            newPlayerOrder = newOrder.newPlayerOrder
        )
    }

    fun updateConnectivity(authUser: User, lobbyID: String, newConnectivity: RqUpdateConnectivity) {
        lobbyService.updateConnectivity(
            caller = LobbyParticipant.of(authUser),
            lobbyID = lobbyID,
            newConnectivity = newConnectivity.newConnectivity.toConnectivity()
        )
    }

    fun leaveLobby(authUser: User, lobbyID: String) {
        lobbyService
            .participantLeave(
                lobbyCode = lobbyID,
                leavingParticipant = LobbyParticipant.of(authUser)
            )
    }

    fun addBot(authUser: User, lobbyID: String, botType: String) {
        lobbyService
            .botJoin(
                LobbyParticipant.of(authUser),
                lobbyID,
                botType
            )

    }
//
//    fun getLobbies(): RsLobbiesResponse {
//        return RsLobbiesResponse(lobbyService
//            .getPublicLobbies()
//            .map { it.toRsShortLobbyInfo() }
//        )
//     }
//
//    fun getLobby(authUser: User, lobbyID: String): RsLobby {
//        val lobby = lobbyService.getLobbyIfParticipant(
//            LobbyParticipant.of(authUser),
//            lobbyID
//        )
//
//        return lobby.toRsLobby()
//    }
}