package de.theodm.wizard.game.round

import de.theodm.wizard.game.bets.Bets
import de.theodm.wizard.game.card.TrumpColor
import de.theodm.wizard.game.players.WizardPlayer
import de.theodm.wizard.card.WizardCard
import de.theodm.wizard.game.players.Players
import de.theodm.wizard.game.stich.Stich
import de.theodm.wizard.game.stich.toPlayerSticheMap
import kotlin.math.abs

data class FinishedWizardRound(
    val numberOfCards: Int,
    val trumpCard: WizardCard?,
    val trumpColor: TrumpColor?,
    val bets: Bets,
    val sticheOfPlayer: List<Stich>
) {
    fun pointsOfRoundForPlayer(
        players: Players,
        player: WizardPlayer
    ): Int {
        val sticheMap = sticheOfPlayer
            .toPlayerSticheMap(players, trumpColor)

        val stiche = sticheMap[player] ?: throw IllegalStateException("Für den Spieler $player sind keine Stiche in $sticheMap hinterlegt.")
        val bet = bets[player] ?: throw IllegalStateException("Für den Spieler $player sind keine Tipps in $bets hinterlegt.")

        if (stiche == bet) {
            return stiche * 10 + 20
        }

        return -1 * abs(stiche - bet) * 10
    }

    fun sumPointsOfRoundForPlayer(
        players: Players,

        player: WizardPlayer,
        previousRounds: List<FinishedWizardRound>
    ): Int {
        if (this.numberOfCards == 1) {
            return pointsOfRoundForPlayer(players, player)
        }

        return previousRounds
            .single { it.numberOfCards == this.numberOfCards - 1 }
            .sumPointsOfRoundForPlayer(players, player, previousRounds) + pointsOfRoundForPlayer(players, player)
    }

    fun sumPointsOfRound(players: Players, oldRound: List<FinishedWizardRound>): Map<WizardPlayer, Int> {
        val result = mutableMapOf<WizardPlayer, Int>()
        for (player in players) {
            result[player] = sumPointsOfRoundForPlayer(players, player, oldRound)
        }

        return result
    }


}