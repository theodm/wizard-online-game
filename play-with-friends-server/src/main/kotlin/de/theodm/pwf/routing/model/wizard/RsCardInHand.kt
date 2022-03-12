package de.theodm.pwf.routing.model.wizard

import de.theodm.wizard.card.WizardCard
import de.theodm.wizard.game.players.Players
import de.theodm.wizard.game.stich.Stich

data class RsCardInHand(
    val wizardCard: RsWizardCard,
    val cmpAllowedToPlay: Boolean
)

fun WizardCard.toRsCardInHand(players: Players, handOfPlayer: List<WizardCard>, currentStich: Stich): RsCardInHand {
    return RsCardInHand(
        wizardCard = this.toRsWizardCard(),
        cmpAllowedToPlay = currentStich.isAllowedToPlayCard(players, handOfPlayer, this)
    )
}