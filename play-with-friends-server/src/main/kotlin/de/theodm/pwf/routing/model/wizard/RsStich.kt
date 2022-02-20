package de.theodm.pwf.routing.model.wizard

import de.theodm.pwf.routing.lobby.LobbyParticipant
import de.theodm.pwf.routing.lobby.toRsParticipant
import de.theodm.pwf.routing.model.RsParticipant
import de.theodm.wizard.stich.Stich
import de.theodm.wizard.stich.StichColor

data class RsStich(
    val startPlayer: RsParticipant,
    val playedCards: List<RsWizardCard>,

    val cmpPlayerToCardMap: Map<String, RsWizardCard>,
    val cmpCurrentStichColor: StichColor
)

fun Stich.toRsStich(): RsStich {
    return RsStich(
        startPlayer = (this.startPlayer as LobbyParticipant).toRsParticipant(),
        playedCards = this.cards.map { it.toRsWizardCard() },
        cmpPlayerToCardMap = this.playerToCardMap()
            .mapKeys { it.key.userPublicID() }
            .mapValues { it.value.toRsWizardCard() },
        cmpCurrentStichColor = this.stichColor
    )
}