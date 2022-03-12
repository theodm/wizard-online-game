package de.theodm.pwf.routing.model.wizard

import de.theodm.wizard.game.WizardGameSettings
import de.theodm.wizard.game.players.Players
import de.theodm.wizard.game.round.OpenWizardRound
import de.theodm.wizard.game.players.WizardPlayer

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


fun OpenWizardRound?.toRsOpenWizardRound(
    gameSettings: WizardGameSettings,
    players: Players,
    forPlayer: WizardPlayer
): RsOpenWizardRound? {
    if (this == null)
        return null

    return RsOpenWizardRound(
        this.immutableRoundState(gameSettings, players).toRsImmutableRoundState(),
        this.viewForPlayer(forPlayer).toRsRoundStateForPlayer(players, forPlayer)
    )
}