package de.theodm.pwf.routing.model.wizard

import de.theodm.wizard.OpenWizardRound
import de.theodm.wizard.WizardPlayer

/**
 * Aktuelle Runde oder gerade beendete Runde.
 */
data class RsOpenWizardRound(
    /**
     * Diese Daten bleiben die ganze Runde über gleich. Beispielsweise
     * Spieler, oberste Stapelkarte und Anzahl der verteilten Karten.
     */
    val immutableRoundState: RsImmutableRoundState,

    /**
     * Rundenstatus, alle Eigenschaften können sich innerhalb einer Runde ändern.
     */
    val currentRoundStateForPlayer: RsRoundStateForPlayer
)


fun OpenWizardRound?.toRsOpenWizardRound(forPlayer: WizardPlayer): RsOpenWizardRound? {
    if (this == null)
        return null

    return RsOpenWizardRound(
        this.immutableRoundState().toRsImmutableRoundState(),
        this.viewForPlayer(forPlayer).toRsRoundStateForPlayer(forPlayer)
    )
}