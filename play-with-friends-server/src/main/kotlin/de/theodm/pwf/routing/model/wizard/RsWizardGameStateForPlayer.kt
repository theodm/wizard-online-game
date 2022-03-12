package de.theodm.pwf.routing.model.wizard

import de.theodm.wizard.game.WizardGameState
import de.theodm.wizard.game.players.WizardPlayer

/**
 * Ausschnitt des Spielgeschehens, den ein einzelner Spieler
 * sehen darf.
 */
data class RsWizardGameStateForPlayer(
    /**
     * Vergangene Runden.
     */
    val oldRounds: List<RsFinishedWizardRound>,

    /**
     * Aktuelle Runde oder gerade beendete Runde.
     */
    val openWizardRound: RsOpenWizardRound?,
)

fun WizardGameState.getRsWizardGameStateForPlayer(forPlayer: WizardPlayer): RsWizardGameStateForPlayer {
    return RsWizardGameStateForPlayer(
        this.previousRounds.map { it.toRsFinishedWizardRound(this.players, this.previousRounds) },
        this.currentRound.toRsOpenWizardRound(this.gameSettings, this.players, forPlayer)
    )
}
