package de.theodm

class LobbyDoesNotExistException(lobbyID: LobbyID)
    : Exception("Eine Lobby mit der ID $lobbyID existiert nicht.")
