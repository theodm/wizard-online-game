package de.theodm.wizard

import kotlin.math.abs

data class FinishedWizardRound(
    val numberOfCards: Int,
    val bets: Map<WizardPlayer, Int>,
    val sticheOfPlayer: Map<WizardPlayer, Int>
) {
    private fun pointsOfRoundForPlayer(player: WizardPlayer): Int {
        val stiche = sticheOfPlayer[player]!!
        val bet = bets[player]!!

        if (stiche == bet) {
            return stiche * 10 + 20
        }

        return -1 * abs(stiche - bet) * 10
    }

    fun sumPointsOfRoundForPlayer(
        player: WizardPlayer,
        oldRounds: List<FinishedWizardRound>
    ): Int {
        if (this.numberOfCards == 1) {
            return pointsOfRoundForPlayer(player)
        }

        return oldRounds
            .single { it.numberOfCards == this.numberOfCards - 1 }
            .sumPointsOfRoundForPlayer(player, oldRounds) + pointsOfRoundForPlayer(player)
    }

    fun sumPointsOfRound(players: Players, oldRound: List<FinishedWizardRound>): Map<WizardPlayer, Int> {
        val result = mutableMapOf<WizardPlayer, Int>()
        for (player in players) {
            result[player] = sumPointsOfRoundForPlayer(player, oldRound)
        }

        return result
    }


}