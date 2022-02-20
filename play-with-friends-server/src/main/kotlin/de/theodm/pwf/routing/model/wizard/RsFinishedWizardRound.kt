package de.theodm.pwf.routing.model.wizard

import de.theodm.wizard.FinishedWizardRound
import de.theodm.wizard.Players
import de.theodm.wizard.WizardPlayer

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
    bets = this.bets.mapKeys { it.key.userPublicID() },
    sticheOfPlayer = this.sticheOfPlayer.mapKeys { it.key.userPublicID() },
    cmpSumPointsOfPlayers = this.sumPointsOfRound(players, finishedWizardRounds).mapKeys { it.key.userPublicID() }
)