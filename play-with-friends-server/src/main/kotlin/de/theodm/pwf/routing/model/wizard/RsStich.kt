package de.theodm.pwf.routing.model.wizard

import de.theodm.Participant
import de.theodm.pwf.routing.lobby.toRsParticipant
import de.theodm.pwf.routing.model.RsParticipant
import de.theodm.wizard.game.players.Players
import de.theodm.wizard.game.stich.Stich
import de.theodm.wizard.game.stich.StichColor

data class RsStich(
    val startPlayer: RsParticipant,
    val playedCards: List<RsWizardCard>,

    val cmpPlayerToCardMap: Map<String, RsWizardCard>,
    val cmpCurrentStichColor: StichColor
)

fun Stich.toRsStich(players: Players): RsStich {
    return RsStich(
        startPlayer = (this.startPlayer as Participant).toRsParticipant(),
        playedCards = this.cards.map { it.toRsWizardCard() },
        cmpPlayerToCardMap = this.playerToCardMap(players)
            .mapKeys { it.key.userPublicID() }
            .mapValues { it.value.toRsWizardCard() },
        cmpCurrentStichColor = this.stichColor
    )
}