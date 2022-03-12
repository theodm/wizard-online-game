package de.theodm.pwf.bot

import de.theodm.wizard.game.round.ImmutableRoundState
import de.theodm.wizard.game.round.RoundStateForPlayerFirstRound

data class R1Observation(
    val trumpCardColor: Int,
    val trumpCardNumber: Int,
    val trumpColor: Int,
    val numberOfPlayers: Int,
    val ownPosition: Int,
    val p1CardColor: Int,
    val p1CardNumber: Int,
    val p2CardColor: Int,
    val p2CardNumber: Int,
    val p3CardColor: Int,
    val p3CardNumber: Int,
    val p4CardColor: Int,
    val p4CardNumber: Int,
    val p5CardColor: Int,
    val p5CardNumber: Int,
    val p6CardColor: Int,
    val p6CardNumber: Int,
    val firstPlayerBet: Int,
    val secondPlayerBet: Int,
    val thirdPlayerBet: Int,
    val fourthPlayerBet: Int,
    val fifthPlayerBet: Int,
)

fun r1ObservationOf(
    viewForPlayer: RoundStateForPlayerFirstRound,
    immutableRoundState: ImmutableRoundState
): R1Observation {
    val currentPlayer = viewForPlayer.currentPlayer

    val firstPlayer = immutableRoundState.players.getOrNull(0)
    val secondPlayer = immutableRoundState.players.getOrNull(1)
    val thirdPlayer = immutableRoundState.players.getOrNull(2)
    val fourthPlayer = immutableRoundState.players.getOrNull(3)
    val fifthPlayer = immutableRoundState.players.getOrNull(4)
    val sixthPlayer = immutableRoundState.players.getOrNull(5)

    val firstPlayerCard = viewForPlayer.cardsOfOtherPlayers[firstPlayer]?.let { it[0] }
    val secondPlayerCard = viewForPlayer.cardsOfOtherPlayers[secondPlayer]?.let { it[0] }
    val thirdPlayerCard = viewForPlayer.cardsOfOtherPlayers[thirdPlayer]?.let { it[0] }
    val fourthPlayerCard = viewForPlayer.cardsOfOtherPlayers[fourthPlayer]?.let { it[0] }
    val fifthPlayerCard = viewForPlayer.cardsOfOtherPlayers[fifthPlayer]?.let { it[0] }
    val sixthPlayerCard = viewForPlayer.cardsOfOtherPlayers[sixthPlayer]?.let { it[0] }

    val currentPlayerIndex = immutableRoundState
        .players
        .indexOf(currentPlayer)

    return R1Observation(
        mapTrumpCardColor(immutableRoundState.trumpCard!!),
        mapTrumpCardNumber(immutableRoundState.trumpCard),
        mapTrumpColor(viewForPlayer.trumpColor),
        mapNumberOfPlayers(immutableRoundState.players.size),
        currentPlayerIndex,
        mapCardColor(firstPlayerCard),
        mapCardNumber(firstPlayerCard),
        mapCardColor(secondPlayerCard),
        mapCardNumber(secondPlayerCard),
        mapCardColor(thirdPlayerCard),
        mapCardNumber(thirdPlayerCard),
        mapCardColor(fourthPlayerCard),
        mapCardNumber(fourthPlayerCard),
        mapCardColor(fifthPlayerCard),
        mapCardNumber(fifthPlayerCard),
        mapCardColor(sixthPlayerCard),
        mapCardNumber(sixthPlayerCard),
        mapBet(viewForPlayer.bets[firstPlayer]),
        mapBet(viewForPlayer.bets[secondPlayer]),
        mapBet(viewForPlayer.bets[thirdPlayer]),
        mapBet(viewForPlayer.bets[fourthPlayer]),
        mapBet(viewForPlayer.bets[fifthPlayer]),
    )
}

