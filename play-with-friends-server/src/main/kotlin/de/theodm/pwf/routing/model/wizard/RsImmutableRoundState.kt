package de.theodm.pwf.routing.model.wizard

import de.theodm.Participant
import de.theodm.pwf.routing.lobby.LobbyParticipant
import de.theodm.pwf.routing.lobby.toRsParticipant
import de.theodm.pwf.routing.model.RsParticipant
import de.theodm.wizard.ImmutableRoundState

/**
 * Diese Daten bleiben die ganze Runde über gleich. Beispielsweise
 * Spieler, oberste Stapelkarte und Anzahl der verteilten Karten.
 */
data class RsImmutableRoundState(
    /**
     * Spieler.
     */
    val players: List<RsParticipant>,

    /**
     * Anzahl der verteilten Karten.
     */
    val numberOfCards: Int,

    /**
     * Oberste Stapelkarte.
     */
    val trumpCard: RsWizardCard?
)

fun ImmutableRoundState.toRsImmutableRoundState(): RsImmutableRoundState {
    return RsImmutableRoundState(
        this.players.map { it as Participant }.map { it.toRsParticipant() },
        this.numberOfCards,
        this.trumpCard?.toRsWizardCard()
    )
}
