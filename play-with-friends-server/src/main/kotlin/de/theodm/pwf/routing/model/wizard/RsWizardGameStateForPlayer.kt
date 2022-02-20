package de.theodm.pwf.routing.model.wizard

import de.theodm.wizard.WizardGameState
import de.theodm.wizard.WizardPlayer

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
        this.oldRounds.map { it.toRsFinishedWizardRound(this.players, this.oldRounds) },
        this.currentRound.toRsOpenWizardRound(forPlayer)
    )
}
