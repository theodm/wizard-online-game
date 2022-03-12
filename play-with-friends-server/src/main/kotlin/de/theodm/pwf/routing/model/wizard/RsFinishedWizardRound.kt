package de.theodm.pwf.routing.model.wizard

import de.theodm.wizard.game.round.FinishedWizardRound
import de.theodm.wizard.game.players.Players
import de.theodm.wizard.game.stich.toPlayerSticheMap

data class RsFinishedWizardRound(
    val numberOfCards: Int,
    val bets: Map<String, Int>,
    val sticheOfPlayer: Map<String, Int>,

    val cmpSumPointsOfPlayers: Map<String, Int>
)

fun FinishedWizardRound.toRsFinishedWizardRound(
    players: Players,
    finishedWizardRounds: List<FinishedWizardRound>
) = RsFinishedWizardRound(
    numberOfCards = this.numberOfCards,
    bets = this.bets.mapKeys { it.key.userPublicID() }.mapValues { it.value ?: throw IllegalStateException("An dieser Stelle muss die Wette gesetzt sein") },
    sticheOfPlayer = this.sticheOfPlayer.toPlayerSticheMap(players, this.trumpColor).mapKeys { it.key.userPublicID() },
    cmpSumPointsOfPlayers = this.sumPointsOfRound(players, finishedWizardRounds).mapKeys { it.key.userPublicID() }
)